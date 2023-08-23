package com.shestero.psql2solr.config

import com.shestero.psql2solr.Document

import java.sql.*
import scala.util.chaining.*

case class Database(url: String) {

  val conn: Connection = DriverManager.getConnection(url)
  conn.setAutoCommit(false)

  def statement(): Statement = conn.createStatement(
    ResultSet.CONCUR_READ_ONLY,
    ResultSet.TYPE_FORWARD_ONLY,
    ResultSet.CLOSE_CURSORS_AT_COMMIT
  ) tap { _.setFetchSize(1024) }

  def query(sql: String)(implicit st: Statement): ResultSet = st.executeQuery(sql)

  def close(): Unit = conn.close()
}

object Database {

  type Extractor[T] = ResultSet => T

  implicit val longExtractor: Extractor[Long] = _.getLong(1)

  val stringTypes: Set[Int] = Set(
    java.sql.Types.VARCHAR,
    java.sql.Types.NVARCHAR,
    java.sql.Types.LONGVARCHAR,
    java.sql.Types.LONGNVARCHAR
  )

  implicit class ResultSetOps(rs: ResultSet) {
    implicit def map[T](implicit extractor: Extractor[T]): Iterator[T] =
      Iterator.continually(rs).takeWhile(_.next()).map(extractor)

    lazy val meta: Seq[String] = rs.getMetaData.pipe { meta =>
      (1 to meta.getColumnCount).collect { case i if stringTypes contains meta.getColumnType(i) =>
        meta.getColumnName(i)
      }
    }

    implicit lazy val metaExtractor: Extractor[Document] = r =>
      Document(r.getLong(1), meta.map(column => column -> r.getString(column)).toMap)

    def metaMap: Iterator[Document] = rs.map
  }
}
