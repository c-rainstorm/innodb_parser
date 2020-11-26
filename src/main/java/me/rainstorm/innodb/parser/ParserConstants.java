package me.rainstorm.innodb.parser;

import me.rainstorm.innodb.common.i18n.SupportedLocaleEnum;

import java.util.Locale;

/**
 * @author traceless
 */
public class ParserConstants {
    static {
        reset();
    }

    public static final String INNODB_PARSER_VERSION = "1.0";
    public static final String PROPERTY_NEO4J_URL = "me.rainstorm.innodb.parser.neo4j.url";
    public static final String PROPERTY_NEO4J_USER = "me.rainstorm.innodb.parser.neo4j.user";
    public static final String PROPERTY_NEO4J_PASSWORD = "me.rainstorm.innodb.parser.neo4j.password";

    public static final String PROPERTY_NEO4J_URL_DEFAULT = "bolt://localhost:7687";
    public static final String PROPERTY_NEO4J_USER_DEFAULT = "neo4j";
    public static final String PROPERTY_NEO4J_PASSWORD_DEFAULT = "innodb";

    public static boolean VERBOSE;

    public static Locale LOCALE;

    public static int USAGE_WIDTH;
    public static int EXTEND_LRU_CACHE_SIZE;

    public static void reset() {
        VERBOSE = false;
        LOCALE = SupportedLocaleEnum.EN_US.getLocale();
        USAGE_WIDTH = 120;
        EXTEND_LRU_CACHE_SIZE = 10;
        System.setProperty(PROPERTY_NEO4J_URL, System.getProperty(PROPERTY_NEO4J_URL, PROPERTY_NEO4J_URL_DEFAULT));
        System.setProperty(PROPERTY_NEO4J_USER, System.getProperty(PROPERTY_NEO4J_USER, PROPERTY_NEO4J_USER_DEFAULT));
        System.setProperty(PROPERTY_NEO4J_PASSWORD, System.getProperty(PROPERTY_NEO4J_PASSWORD, PROPERTY_NEO4J_PASSWORD_DEFAULT));
    }
}
