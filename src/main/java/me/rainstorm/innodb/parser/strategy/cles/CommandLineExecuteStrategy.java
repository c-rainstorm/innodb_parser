package me.rainstorm.innodb.parser.strategy.cles;

import lombok.extern.slf4j.Slf4j;
import me.rainstorm.innodb.parser.options.CommandLineArgs;
import me.rainstorm.innodb.parser.strategy.AbstractStrategy;
import me.rainstorm.innodb.parser.strategy.cles.level_1.HelpStrategy;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static me.rainstorm.innodb.common.i18n.I18nMsgCodeEnum.LogCommandLineExecuteStrategyMatchOrder;
import static me.rainstorm.innodb.common.i18n.I18nUtil.message;
import static me.rainstorm.innodb.parser.ParserConstants.VERBOSE;

/**
 * @author traceless
 */
@Slf4j
public abstract class CommandLineExecuteStrategy extends AbstractStrategy<CommandLineArgs> {
    static List<CommandLineExecuteStrategy> commandLineExecuteStrategies;

    static {
        ServiceLoader<CommandLineExecuteStrategy> serviceLoader =
                ServiceLoader.load(CommandLineExecuteStrategy.class);
        commandLineExecuteStrategies = StreamSupport.stream(serviceLoader.spliterator(), false)
                .distinct().sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public static Optional<CommandLineExecuteStrategy> discover(CommandLineArgs commandLineArgs) {
        if (VERBOSE && log.isDebugEnabled()) {
            int i = 0;
            for (CommandLineExecuteStrategy strategy : commandLineExecuteStrategies) {
                log.debug(message(LogCommandLineExecuteStrategyMatchOrder, ++i, strategy.getClass().getSimpleName()));
            }
        }

        return commandLineExecuteStrategies.stream().filter((x) -> x.match(commandLineArgs)).findFirst();
    }

    public static CLESExecuteWithOutCLA defaultStrategy() {
        assert commandLineExecuteStrategies.get(0) instanceof HelpStrategy;

        return (CLESExecuteWithOutCLA) commandLineExecuteStrategies.get(0);
    }
}
