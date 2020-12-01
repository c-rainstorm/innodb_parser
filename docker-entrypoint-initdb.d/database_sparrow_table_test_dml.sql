use sparrow;

DROP FUNCTION IF EXISTS random_str;
DELIMITER $$
CREATE FUNCTION random_char() RETURNS char
BEGIN
    DECLARE chars_str VARCHAR(100)
        DEFAULT "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    RETURN SUBSTRING(chars_str, FLOOR(1 + RAND() * 52), 1);
END $$

insert into sparrow.test (c1, c2) VALUE (REPEAT(random_char(), 4000), REPEAT(random_char(), 750));
insert into sparrow.test (c1, c2) VALUE (REPEAT(random_char(), 4000), REPEAT(random_char(), 750));
insert into sparrow.test (c1, c2) VALUE (REPEAT(random_char(), 4000), REPEAT(random_char(), 750));
insert into sparrow.test (c1, c2) VALUE (REPEAT(random_char(), 4000), REPEAT(random_char(), 750));
insert into sparrow.test (c1, c2) VALUE (REPEAT(random_char(), 4000), REPEAT(random_char(), 750));
insert into sparrow.test (c1, c2) VALUE (REPEAT(random_char(), 4000), REPEAT(random_char(), 750));
insert into sparrow.test (c1, c2) VALUE (REPEAT(random_char(), 4000), REPEAT(random_char(), 750));
insert into sparrow.test (c1, c2) VALUE (REPEAT(random_char(), 4000), REPEAT(random_char(), 750));