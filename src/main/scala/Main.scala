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
  val app = "psql2solr"
  val cfg = "config.yaml"

  println(s"$app - PostgreSQL tables indexer. Configuration in $cfg")

  val json: Either[ParsingFailure, Json] = yaml.parser.parse(new FileReader(cfg))
  val deploy: Deploy = json
    .leftMap(err => err: Error)
    .flatMap(_.as[Deploy])
    .valueOr(throw _)

  import deploy.*
  deploy.tables.foreach { case t @ Table(tname, _, _) =>
    println(s"Processing table $tname ...")
    val st: Statement = db.statement()
    val rs: ResultSet = st.executeQuery(t.select)
    rs.metaMap.grouped(batchSize).foreach(solr.add)
    st.close()
  }

  println("Done!")
  close()
}
