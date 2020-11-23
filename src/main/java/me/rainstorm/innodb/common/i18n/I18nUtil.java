package me.rainstorm.innodb.common.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import static me.rainstorm.innodb.parser.ParserConstants.LOCALE;

/**
 * @author traceless
 */
public class I18nUtil {
    private static ResourceBundle rb;

    static {
        setLocale(LOCALE);
    }

    public static void setLocale(Locale locale) {
        I18nUtil.rb = ResourceBundle.getBundle("i18n/message", locale);
    }

    public static String message(String code, Object... args) {
        return MessageFormat.format(rb.getString(code), args);
    }
}
