package me.rainstorm.innodb.parser;

import lombok.extern.slf4j.Slf4j;
import me.rainstorm.innodb.parser.options.CommandLineArgs;
import me.rainstorm.innodb.parser.strategy.cles.CommandLineExecuteStrategy;

import java.util.Optional;

import static me.rainstorm.innodb.parser.ParserConstants.verbose;
import static me.rainstorm.innodb.parser.options.CommandLineOptionEnum.Verbose;

/**
 * @author traceless
 */
@Slf4j
public class Bootstrap {
    public static void main(String[] args) {
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
        if (commandLineArgs.contains(Verbose)) {
            ParserConstants.verbose = true;
        }
    }

    public static CommandLineExecuteStrategy findExecuteStrategy(CommandLineArgs commandLineArgs) {
        Optional<CommandLineExecuteStrategy> executeStrategyOpt = CommandLineExecuteStrategy.discover(commandLineArgs);
        CommandLineExecuteStrategy executeStrategy;
        if (executeStrategyOpt.isPresent()) {
            executeStrategy = executeStrategyOpt.get();

            if (verbose && log.isDebugEnabled()) {
                log.debug("已匹配的执行策略：{}", executeStrategy.getClass().getSimpleName());
            }
        } else {
            executeStrategy = CommandLineExecuteStrategy.defaultStrategy();
            if (verbose && log.isDebugEnabled()) {
                log.debug("执行策略匹配失败，执行默认策略：{}", executeStrategy.getClass().getSimpleName());
            }
        }

        return executeStrategy;
    }
}
