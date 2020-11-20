package me.rainstorm.innodb.parser.options;

import lombok.extern.slf4j.Slf4j;
import me.rainstorm.innodb.parser.constant.Constants;
import org.apache.commons.cli.*;

import static me.rainstorm.innodb.parser.constant.Constants.INNODB_PARSER_VERSION;
import static me.rainstorm.innodb.parser.constant.Constants.MYSQL_INNODB_VERSION;

/**
 * @author traceless
 */
@Slf4j
public class CommandLineArgsHelper {
    private static final HelpFormatter formatter = new HelpFormatter();

    private final CommandLine commandLineParsed;

    public CommandLineArgsHelper(String[] args) throws ParseException {
        this.commandLineParsed = new DefaultParser().parse(CommandLineArgEnum.getOptions(), args);
    }

    public static void printHelp() {
        formatter.printHelp("java -jar /path/to/your/innodb-parser.jar " + requiredArg() +
                        " [OPTION]...\n" +
                        "根据选项解析 Innodb 数据文件",
                "====================================",
                CommandLineArgEnum.getOptions(),
                "====================================\n如有问题，可以联系 pom.xml 中的开发者");
    }

    private static String requiredArg() {
        StringBuilder builder = new StringBuilder();
        for (CommandLineArgEnum commandLineArg : CommandLineArgEnum.values()) {
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

    public void setGlobalConstants() {
        if (contains(CommandLineArgEnum.Verbose)) {
            Constants.verbose = true;
        }
    }

    private boolean contains(CommandLineArgEnum verbose) {
        return commandLineParsed.hasOption(verbose.getOpt());
    }

    public boolean isHelp() {
        return contains(CommandLineArgEnum.Help);
    }

    public boolean isVersion() {
        return contains(CommandLineArgEnum.Version);
    }

    public String version() {
        return String.format("MySQL Innodb version: %s\nInnodb parser version: %s", MYSQL_INNODB_VERSION, INNODB_PARSER_VERSION);
    }

    public boolean withoutOptions() {
        return commandLineParsed.getOptions().length == 0;
    }
}
