package me.rainstorm.innodb.common.i18n;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class I18NUtilTest {
    @ParameterizedTest
    @CsvSource({"ZH_CN,你好！",
            "EN_US,hi!"})
    public void test(SupportedLocaleEnum locale, String expected) {
        I18nUtil.setLocale(locale.getLocale());

        String message = I18nUtil.message(I18nMsgCodeEnum.Greeting);
        System.out.println(message);
        Assertions.assertEquals(expected, message);
    }
}
