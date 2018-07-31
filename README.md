# WordnetCsvCreator
### External Libraries
* net.sf.extjwnl:extjwnl:1.9.4
* net.sf.extjwnl:extjwnl-data-wn30:1.2
* org.apache.commons:commons-csv:1.5

## Import
### NEO4j
* put header files and created files "igraph_nodes.csv, relationship.csv, synset_nodes.csv" to import folder in neo4j main folder.
* run the code -> 
'''
./neo4j-admin import --nodes "../import/igraph_header.csv,../import/igraph_nodes.csv" --nodes "../import/wn_header.csv,../import/synset_nodes.csv" --relationships "../import/rel_header.csv,../import/relationship.csv"
'''
* neo4j-admin in bin folder in neo4j main folder.

### PostgreSQL
* first create database and table "definitions" in postgresql -> 

'''
create table definitions(
id text primary key not null,
eng text not null
);
'''

* put "definition.csv" to /tmp folder
* connect to postgres user -> sudo su - postgres
* run code in terminal of postgres -> 
'''
psql --command="\\copy def(id,eng) from '/tmp/definition.csv' with delimiter ',';"
'''
