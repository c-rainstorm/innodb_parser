package me.rainstorm.innodb;

import lombok.extern.slf4j.Slf4j;
import me.rainstorm.innodb.common.i18n.I18nUtil;
import me.rainstorm.innodb.parser.ParserConstants;
import me.rainstorm.innodb.parser.options.CommandLineArgs;
import me.rainstorm.innodb.parser.strategy.cles.CommandLineExecuteStrategy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static me.rainstorm.innodb.common.i18n.I18nMsgCodeEnum.LogCommandLineExecuteStrategyDisMatched;
import static me.rainstorm.innodb.common.i18n.I18nMsgCodeEnum.LogCommandLineExecuteStrategyMatched;
import static me.rainstorm.innodb.common.i18n.I18nUtil.message;
import static me.rainstorm.innodb.parser.ParserConstants.VERBOSE;
import static me.rainstorm.innodb.parser.options.CommandLineOptionEnum.I18N;
import static me.rainstorm.innodb.parser.options.CommandLineOptionEnum.Verbose;

/**
 * @author traceless
 */
@Profile("!test")
@Slf4j
@Component
public class Bootstrap implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        try {
            CommandLineArgs commandLineArgs = new CommandLineArgs(args);

            setGlobalConstants(commandLineArgs);

            findExecuteStrategy(commandLineArgs)
                    .execute(commandLineArgs);
        } catch (Exception e) {
            e.printStackTrace();
            CommandLineExecuteStrategy.defaultStrategy()
                    .execute();
        }
    }

    public static void setGlobalConstants(CommandLineArgs commandLineArgs) {
        // multi junit test case can run in same jvm, so we need reset first.
        ParserConstants.reset();

        if (commandLineArgs.contains(Verbose)) {
            ParserConstants.VERBOSE = true;
        }
        if (commandLineArgs.contains(I18N)) {
            I18nUtil.setLocale(commandLineArgs.locale().getLocale());
        }
    }

    public static CommandLineExecuteStrategy findExecuteStrategy(CommandLineArgs commandLineArgs) {
        Optional<CommandLineExecuteStrategy> executeStrategyOpt = CommandLineExecuteStrategy.discover(commandLineArgs);
        CommandLineExecuteStrategy executeStrategy;
        if (executeStrategyOpt.isPresent()) {
            executeStrategy = executeStrategyOpt.get();

            if (VERBOSE && log.isDebugEnabled()) {
                log.debug(message(LogCommandLineExecuteStrategyMatched, executeStrategy.getClass().getSimpleName()));
            }
        } else {
            executeStrategy = CommandLineExecuteStrategy.defaultStrategy();
            if (VERBOSE && log.isDebugEnabled()) {
                log.debug(message(LogCommandLineExecuteStrategyDisMatched, executeStrategy.getClass().getSimpleName()));
            }
        }

        return executeStrategy;
    }
}
