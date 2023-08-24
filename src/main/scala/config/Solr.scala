package com.shestero.psql2solr.config

import com.github.takezoe.solr.scala.{BatchRegister, SolrClient}
import com.shestero.psql2solr.Document
import org.apache.solr.client.solrj.response.UpdateResponse
import cats.syntax.option.*

case class Solr(url: String) {
  val client: SolrClient = new SolrClient(url)

  private def map(doc: Document): Map[String, Any] =
    doc.fields ++ Map("key" -> doc.key)

  def add(docs: Iterable[Document]): UpdateResponse =
    client.add(docs.map(map).toSeq :_*).commit()

  def add(doc: Document): UpdateResponse =
    client.add(map(doc)).commit()

  def close(): Unit = client.shutdown()
}
