use sparrow;

CREATE TABLE test
(
    id bigint auto_increment not null,
    c1 VARCHAR(4010)         NOT NULL,
    c2 varchar(750)          NOT NULL,
    primary key (id),
    index (c2)
) character set ascii
  engine = innodb
  ROW_FORMAT = COMPACT;
