package com.shestero.psql2solr

import cats.syntax.either.*
import io.circe.generic.auto.*
import io.circe.*
import io.circe.yaml

import java.io.{File, FileReader}
import java.sql.*

import config.*
import config.Database.*


object Main extends App {
  val app = "psql2sold"
  val cfg = "config.yaml"

  println(s"$app - PostgreSQL tables indexer. Configuration in $cfg")

  val json: Either[ParsingFailure, Json] = yaml.parser.parse(new FileReader(cfg))
  val deploy: Deploy = json
    .leftMap(err => err: Error)
    .flatMap(_.as[Deploy])
    .valueOr(throw _)

  import deploy.*
  implicit val st: Statement = db.statement()
  deploy.tables.foreach { case Table(tname, key, fields ) =>
    println(s"Processing table $tname ...")
    val list: String = (key :: fields).mkString(", ")
    val rs: ResultSet = db.query(s"SELECT $list FROM $tname") // beware of injections!
    rs.metaMap.grouped(batchSize).foreach(solr.add)
  }

  println("Done!")
  st.close()
  close()
}
