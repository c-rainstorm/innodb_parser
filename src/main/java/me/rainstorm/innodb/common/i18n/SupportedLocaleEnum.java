package me.rainstorm.innodb.common.i18n;

import lombok.Getter;

import java.util.Locale;

/**
 * @author traceless
 */

@Getter
public enum SupportedLocaleEnum {
    /**
     * 英文
     */
    EN_US(Locale.US),
    /**
     * 中文
     */
    ZH_CN(Locale.CHINA);

    SupportedLocaleEnum(Locale locale) {
        this.locale = locale;
    }

    private final Locale locale;
}
