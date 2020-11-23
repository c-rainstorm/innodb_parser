package me.rainstorm.innodb.parser.options;

import lombok.Getter;
import me.rainstorm.innodb.common.i18n.I18nMsgCodeEnum;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static me.rainstorm.innodb.common.i18n.I18nMsgCodeEnum.*;
import static me.rainstorm.innodb.common.i18n.I18nUtil.message;

/**
 * @author traceless
 */
@Getter
public enum CommandLineOptionEnum {
    DataDir(false, 'r', "root-dir-of-data", false, true, OptionDataDir, "/var/lib/mysql"),
    SystemTableSpace(false, 's', "system-tablespace-file", false, true, OptionSystemTableSpace, "ibdata1"),
    Verbose(false, 'V', "verbose", false, OptionVerbose),

    Help(true, 'h', "help", false, OptionHelp),
    Version(true, 'v', "version", false, OptionVersion),
    I18N(false, 'l', "locale", false, true, OptionI18N),

    Database(true, 'd', "database", true, OptionDatabase),
    Table(true, 't', "table", true, OptionTable),
    Page(true, 'p', "page", true, OptionPage);

    private final boolean controlOpt;
    private final char opt;
    private final String defaultValue;
    private final Supplier<Option> option;

    /**
     * 含长选项的命令行参数
     *
     * @param opt     短选项
     * @param longOpt 长选项
     * @param hasArg  是否有参数
     * @param msgCode 描述信息的 i18n key
     */
    CommandLineOptionEnum(boolean controlOpt, char opt, String longOpt, boolean hasArg, I18nMsgCodeEnum msgCode) {
        this(controlOpt, opt, longOpt, false, hasArg, msgCode);
    }

    /**
     * 含长选项的命令行参数
     *
     * @param opt     短选项
     * @param longOpt 长选项
     * @param hasArg  是否有参数
     * @param desc    描述
     */
    CommandLineOptionEnum(boolean controlOpt, char opt, String longOpt, boolean required, boolean hasArg, I18nMsgCodeEnum desc) {
        this(controlOpt, opt, longOpt, required, hasArg, desc, null);
    }


    /**
     * 含长选项的命令行参数
     *
     * @param opt     短选项
     * @param longOpt 长选项
     * @param hasArg  是否有参数
     * @param msgCode 描述
     */
    CommandLineOptionEnum(boolean controlOpt, char opt, String longOpt, boolean required, boolean hasArg, final I18nMsgCodeEnum msgCode, String defaultValue) {
        this.controlOpt = controlOpt;

        this.option = () -> {
            String descStr = message(msgCode);
            if (hasArg && defaultValue != null && defaultValue.length() > 0) {
                descStr = String.format("%s, %s %s", descStr, message(ConstDefault), defaultValue);
            }
            Option option = new Option(String.valueOf(opt), longOpt, hasArg, descStr);
            option.setRequired(required);
            return option;
        };
        this.opt = opt;
        this.defaultValue = defaultValue;
    }

    public static Options getOptions() {
        Options options = new Options();
        for (CommandLineOptionEnum commandLineArg : CommandLineOptionEnum.values()) {
            options.addOption(commandLineArg.getOption());
        }
        return options;
    }


    public static Set<CommandLineOptionEnum> controlOptions() {
        return optionsMatch(CommandLineOptionEnum::isControlOpt);
    }

    private static Set<CommandLineOptionEnum> optionsMatch(Predicate<CommandLineOptionEnum> predicate) {
        return Arrays.stream(CommandLineOptionEnum.values())
                .filter(predicate)
                .collect(Collectors.toSet());
    }

    public String getShortOpt() {
        return "-" + opt;
    }

    public Option getOption() {
        return option.get();
    }
}

