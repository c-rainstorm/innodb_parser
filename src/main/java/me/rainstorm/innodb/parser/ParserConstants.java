package me.rainstorm.innodb.parser;

import me.rainstorm.innodb.common.i18n.SupportedLocaleEnum;

import java.util.Locale;

/**
 * @author traceless
 */
public class ParserConstants {
    public static final String INNODB_PARSER_VERSION = "1.0";
    public static boolean verbose = false;

    public static final Locale LOCALE = SupportedLocaleEnum.EN_US.getLocale();

    public static int EXTEND_LRU_CACHE_SIZE = 10;
}
