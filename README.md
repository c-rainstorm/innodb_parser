# Innodb Parser

Innodb 数据页解析工具

## Quick Start

### 所需依赖

- Docker for Desktop
- Neo4j Desktop
- JRE 1.8

### 默认配置

`iprc.sh`

```
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
```

### Step 1: 创建并启动本地 Neo4j 数据库

使用 Neo4j Desktop 创建一个 Neo4j 数据库，密码为 innodb，并启动

![](http://image.rainstorm.vip/mysql/parser/create-database-preview-1.png)

### Step 2: 创建/重启 MySQL Docker 实例

运行脚本 `./run-mysql-in-docker.sh`。 在 Docker Desktop 里创建一个实例数据库。

### Step 3: 解析表空间结构到 Neo4j 数据库

运行 `./do-parse.sh`。 打包项目为 jar，解析并导出 `./sparrow/test.ibd` 独立表空间到 Neo4j 数据库。

### Step 4: 使用 Neo4j Bloom 预览

![](http://image.rainstorm.vip/mysql/parser/neo4j-bloom-preview-2.png)



### Tips

- **i18n 部分，IDEA 建议勾选 Preferences | Editor | File Encodings 的 Transparent native-to-ascii conversion，中文在IDEA就可以正常展示了**