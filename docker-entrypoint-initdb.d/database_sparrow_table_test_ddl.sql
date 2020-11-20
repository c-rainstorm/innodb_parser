use sparrow;

CREATE TABLE record_format_demo (
    id bigint auto_increment not null,
    c1 VARCHAR(10) NOT NULL,
    c2 VARCHAR(10) NOT NULL,
    primary key (id),
    index (c1)
) engine=innodb;
