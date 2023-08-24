# psql2solr
Indexer for any PostgreSQL tables with Apache Solr.
https://solr.apache.org

(In each table it uses one integer "key" and any number of string fields)

## Solr queue example:
http://localhost:8983/solr/test/select?q=description:monkey

## How to deploy Solr (create "test" storage):
```
 docker run -p 8983:8983 -t solr:8.9
 docker ps  
*(from another terminal see container ID like 3b625b336d26)*
 docker exec -it *put_ID_here* solr create -c test
```
