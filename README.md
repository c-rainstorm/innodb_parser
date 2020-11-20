# Innodb Parser

Innodb 数据页解析工具

## Quick Start

### 所需依赖

- Docker for Desktop
- JRE 1.8

### 快速构建 innodb-parser

```bash
git clone git@github.com:c-rainstorm/innodb_parser.git && cd innodb-parser

chmod +x run-mysql-in-docker.sh

// 需要本地安装 Docker for Desktop
// 依赖镜像 mysql:5.7
//
// 创建用户 traceless
// 创建数据库 sparrow
// 创建表 test
./run-mysql-in-docker.sh

// 会打包成 fat jar，可以直接执行
mvn clean package 

// 建议保存到 shell 配置文件里
alias innodb_parser="java -jar $(pwd)/target/innodb-parser-*.jar"

innodb_parser -h
```

### 操作指北

// TODO