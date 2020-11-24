package me.rainstorm.innodb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author traceless
 */
@Slf4j
@SpringBootApplication
public class InnodbParserApplication {
    public static void main(String[] args) {
        SpringApplication.run(InnodbParserApplication.class, args);
    }
}
