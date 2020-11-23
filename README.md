# Innodb Parser

Innodb 数据页解析工具

## Quick Start

### 所需依赖

- Docker for Desktop
- JRE 1.8
- Maven

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

### Usage

```bash
$ innodb_parser -h

usage: java -jar /path/to/your/innodb-parser.jar  [OPTION]...

Analyze InnoDB data files according to the options.
For the complete DEMO operation, please refer to README.
------------------------------------------------------------------------------------------------------------------------
 -d,--database <arg>                 The name of the database to be analyzed
 -h,--help                           Print help document
 -l,--locale <arg>                   International support，At present, only Chinese[zh_CN] and English[en_US] are
                                     supported
 -p,--page <arg>                     The table space page number to be analyzed, starting from 0
 -r,--root-dir-of-data <arg>         Data directory. All table spaces are in this directory by default, default
                                     /var/lib/mysql
 -s,--system-tablespace-file <arg>   System table space file path, relative path of Option -r, default ibdata1
 -t,--table <arg>                    Table name to be analyzed
 -V,--verbose                        Print more detailed information
 -v,--version                        Print version number
------------------------------------------------------------------------------------------------------------------------
If you have any questions, please contact Developers in pom.xml.

```

### 操作指北

#### 国际化支持

```bash
$ innodb_parser -h

usage: java -jar /path/to/your/innodb-parser.jar  [OPTION]...

Analyze InnoDB data files according to the options.
For the complete DEMO operation, please refer to README.
------------------------------------------------------------------------------------------------------------------------
 -d,--database <arg>                 The name of the database to be analyzed
 -h,--help                           Print help document
 -l,--locale <arg>                   International support，At present, only Chinese[zh_CN] and English[en_US] are
                                     supported
 -p,--page <arg>                     The table space page number to be analyzed, starting from 0
 -r,--root-dir-of-data <arg>         Data directory. All table spaces are in this directory by default, default
                                     /var/lib/mysql
 -s,--system-tablespace-file <arg>   System table space file path, relative path of Option -r, default ibdata1
 -t,--table <arg>                    Table name to be analyzed
 -V,--verbose                        Print more detailed information
 -v,--version                        Print version number
------------------------------------------------------------------------------------------------------------------------
If you have any questions, please contact Developers in pom.xml.

$ innodb_parser -h -l=zh_CN

usage: java -jar /path/to/your/innodb-parser.jar  [OPTION]...

根据选项解析 Innodb 数据文件，具体操作完整 DEMO 请查看 README
------------------------------------------------------------------------------------------------------------------------
 -d,--database <arg>                 需要分析的数据库名称
 -h,--help                           打印帮助文档
 -l,--locale <arg>                   国际化支持，目前仅支持 中文[zh_CN], 英文[en_US] 两种
 -p,--page <arg>                     需要分析的表空间页号，页号从 0 开始
 -r,--root-dir-of-data <arg>         数据目录，所有表空间默认在该目录下, 默认值 /var/lib/mysql
 -s,--system-tablespace-file <arg>   系统表空间文件路径，-r 的相对路径, 默认值 ibdata1
 -t,--table <arg>                    需要分析的表名
 -V,--verbose                        打印更详细的信息
 -v,--version                        打印版本号
------------------------------------------------------------------------------------------------------------------------
如有问题，可以联系 pom.xml 中的开发者
```

#### 独立表空间页结构分析

```bash

$ innodb_parser -r=/tmp/mysql -d=sparrow -t=test

[    PageNo][   SpaceID]Page <PageType> ...
-------------------------------------------
[         0][        23]Page <File space header> 
[         1][        23]Page <Insert buffer bitmap> 
[         2][        23]Page <Segment info> 
[         3][        23]Page <B-tree node> 
[         4][        23]Page <B-tree node> 
[          ][          ]Page <Freshly allocated>
[          ][          ]Page <Freshly allocated>
```

### Tips

- **i18n 部分，IDEA 建议勾选 Preferences | Editor | File Encodings 的 Transparent native-to-ascii conversion，中文在IDEA就可以正常展示了**