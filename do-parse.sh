#!/bin/bash

source iprc.sh

./mvnw clean package -DskipTests

java -Dme.rainstorm.innodb.parser.neo4j.url="${NEO4J_URL}" \
    -Dme.rainstorm.innodb.parser.neo4j.user="${NEO4J_USER}" \
    -Dme.rainstorm.innodb.parser.neo4j.password="${NEO4J_PASSWORD}" \
    -jar target/innodb-parser-1.0-SNAPSHOT.jar -r="${MYSQL_MOUNT_VOLUME}" -d=sparrow -t=test -e
