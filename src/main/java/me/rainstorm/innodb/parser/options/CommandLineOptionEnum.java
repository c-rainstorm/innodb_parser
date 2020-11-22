package me.rainstorm.innodb.parser.options;

import lombok.Getter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author traceless
 */
@Getter
public enum CommandLineOptionEnum {
    DataDir(false, 'r', "root-dir-of-data", false, true, "数据目录，所有表空间默认在该目录下", "/var/lib/mysql"),
    SystemTableSpace(false, 's', "system-tablespace-file", false, true, "系统表空间文件路径，-r 的相对路径", "ibdata1"),
    Verbose(false, 'V', "verbose", false, "打印更详细的信息"),

    Help(true, 'h', "help", false, "打印帮助文档"),
    Version(true, 'v', "version", false, "打印版本号"),

    Database(true, 'd', "database", true, "需要分析的数据库名称"),
    Table(true, 't', "table", true, "需要分析的表名"),
    Page(true, 'p', "page", true, "需要分析的表空间页号，页号从 0 开始");

    private final boolean controlOpt;
    private final char opt;
    private String defaultValue;
    private Option option;

    /**
     * 含长选项的命令行参数
     *
     * @param opt     短选项
     * @param longOpt 长选项
     * @param hasArg  是否有参数
     * @param desc    描述
     */
    CommandLineOptionEnum(boolean controlOpt, char opt, String longOpt, boolean hasArg, String desc) {
        this(controlOpt, opt, longOpt, false, hasArg, desc);
    }

    /**
     * 含长选项的命令行参数
     *
     * @param opt     短选项
     * @param longOpt 长选项
     * @param hasArg  是否有参数
     * @param desc    描述
     */
    CommandLineOptionEnum(boolean controlOpt, char opt, String longOpt, boolean required, boolean hasArg, String desc) {
        this(controlOpt, opt, longOpt, required, hasArg, desc, null);
    }


    /**
     * 含长选项的命令行参数
     *
     * @param opt     短选项
     * @param longOpt 长选项
     * @param hasArg  是否有参数
     * @param desc    描述
     */
    CommandLineOptionEnum(boolean controlOpt, char opt, String longOpt, boolean required, boolean hasArg, String desc, String defaultValue) {
        this.controlOpt = controlOpt;
        if (hasArg && defaultValue != null && defaultValue.length() > 0) {
            desc = String.format("%s, 默认值 %s", desc, defaultValue);
        }

        this.option = new Option(String.valueOf(opt), longOpt, hasArg, desc);
        this.option.setRequired(required);
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

    public String getLongOpt() {
        if (option.hasLongOpt()) {
            return "--" + option.getLongOpt();
        } else {
            return getShortOpt();
        }
    }

    public String getShortOpt() {
        return "-" + option.getOpt();
    }
}

