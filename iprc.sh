#!/bin/bash

PASSWORD_DIR=~/.password

# MySQL 配置
MYSQL_CONTAINER_NAME=mysql
MYSQL_MOUNT_VOLUME=/tmp/mysql
MYSQL_IMAGE=mysql:5.7
MYSQL_MY_CONF_DIR=$(pwd)/conf.d
MYSQL_INIT_DB_DIR=$(pwd)/docker-entrypoint-initdb.d
MYSQL_PASS_FILE=${PASSWORD_DIR}/mysql

# Neo4j 配置
NEO4J_URL=bolt://localhost:7687
NEO4J_USER=neo4j
NEO4J_PASSWORD=innodb
