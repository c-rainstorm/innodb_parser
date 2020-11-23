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
    public static boolean VERBOSE;

    public static Locale LOCALE;

    public static int USAGE_WIDTH;
    public static int EXTEND_LRU_CACHE_SIZE;

    public static void reset() {
        VERBOSE = false;
        LOCALE = SupportedLocaleEnum.EN_US.getLocale();
        USAGE_WIDTH = 120;
        EXTEND_LRU_CACHE_SIZE = 10;
    }
}
