package me.rainstorm.innodb.parser.strategy.cles.level_1;

import me.rainstorm.innodb.parser.options.CommandLineArgs;
import me.rainstorm.innodb.parser.options.CommandLineOptionEnum;
import me.rainstorm.innodb.parser.strategy.cles.CLESExecuteWithOutCLA;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;

/**
 * @author traceless
 */
public class HelpStrategy extends CLESExecuteWithOutCLA {
    private static final HelpFormatter FORMATTER = new HelpFormatter();

    public static final int ORDER = Integer.MAX_VALUE;

    public HelpStrategy() {
        setOrder(ORDER);
    }

    @Override
    public boolean match(CommandLineArgs commandLineArgs) {
        return commandLineArgs.withoutOptions() || commandLineArgs.containHelp();
    }

    @Override
    public void execute() {
        FORMATTER.printHelp("java -jar /path/to/your/innodb-parser.jar " + requiredArg() +
                        " [OPTION]...\n" +
                        "根据选项解析 Innodb 数据文件，具体操作完整 DEMO 请查看 README",
                "====================================",
                CommandLineOptionEnum.getOptions(),
                "====================================\n如有问题，可以联系 pom.xml 中的开发者");
    }

    private static String requiredArg() {
        StringBuilder builder = new StringBuilder();
        for (CommandLineOptionEnum commandLineArg : CommandLineOptionEnum.values()) {
            Option option = commandLineArg.getOption();
            if (option.isRequired()) {
                if (option.hasLongOpt()) {
                    builder.append("--").append(option.getLongOpt());
                } else {
                    builder.append("-").append(option.getOpt());
                }

                if (option.hasArg()) {
                    builder.append("=<arg> ");
                }
            }
        }

        return builder.toString();
    }
}
