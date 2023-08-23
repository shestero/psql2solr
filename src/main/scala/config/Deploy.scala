package com.shestero.psql2solr.config

case class Deploy(batchSize: Int, db: Database, solr: Solr, tables: Seq[Table]) {
  def close(): Unit = {
    db.close()
    solr.close()
  }
}
