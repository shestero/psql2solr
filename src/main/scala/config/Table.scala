package com.shestero.psql2solr.config


case class Table(name: String, key: String, fields: List[String]) {

  val select: String = s"SELECT ${(key :: fields).mkString(", ")} FROM $name"  // beware of injections!

}

