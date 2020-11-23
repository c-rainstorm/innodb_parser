package me.rainstorm.innodb.parser.strategy.cles.level_1;

import me.rainstorm.innodb.parser.options.CommandLineArgs;
import me.rainstorm.innodb.parser.options.CommandLineOptionEnum;
import me.rainstorm.innodb.parser.strategy.cles.CLESExecuteWithOutCLA;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.lang3.StringUtils;

import static me.rainstorm.innodb.common.i18n.I18nMsgCodeEnum.*;
import static me.rainstorm.innodb.common.i18n.I18nUtil.message;
import static me.rainstorm.innodb.parser.ParserConstants.USAGE_WIDTH;

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
        String dividingLine = StringUtils.repeat('-', USAGE_WIDTH);
        FORMATTER.printHelp(USAGE_WIDTH, message(OptionUsageSyntax, requiredArg()),
                message(OptionUsageHeader, dividingLine),
                CommandLineOptionEnum.getOptions(),
                message(OptionUsageFooter, dividingLine));
    }

    private static String requiredArg() {
        StringBuilder builder = new StringBuilder();
        for (CommandLineOptionEnum commandLineArg : CommandLineOptionEnum.values()) {
            Option option = commandLineArg.getOption();
            if (option.isRequired()) {
                builder.append("-").append(option.getOpt());

                if (option.hasArg()) {
                    builder.append("=<arg> ");
                }
            }
        }

        return builder.toString();
    }
}
