package me.rainstorm.innodb.parser.strategy.cles.level_2;

import lombok.extern.slf4j.Slf4j;
import me.rainstorm.innodb.domain.page.LogicPage;
import me.rainstorm.innodb.domain.tablespace.SystemTableSpace;
import me.rainstorm.innodb.parser.options.CommandLineArgs;
import me.rainstorm.innodb.parser.options.CommandLineOptionEnum;
import me.rainstorm.innodb.parser.strategy.cles.CommandLineExecuteStrategy;
import me.rainstorm.innodb.parser.strategy.cles.level_1.HelpStrategy;

import java.util.Iterator;

import static me.rainstorm.innodb.parser.ParserConstants.verbose;

/**
 * @author traceless
 */
@Slf4j
public class SystemTableSpaceAllPage extends CommandLineExecuteStrategy {
    public static final int ORDER = HelpStrategy.ORDER - 100;

    public SystemTableSpaceAllPage() {
        setOrder(ORDER);
    }

    @Override
    public boolean match(CommandLineArgs commandLineArgs) {
        return CommandLineOptionEnum.controlOptions()
                .stream().noneMatch(commandLineArgs::contains);
    }

    @Override
    public void execute(CommandLineArgs commandLineArgs) {
        SystemTableSpace systemTableSpace = new SystemTableSpace(commandLineArgs.systemTableSpace());
        Iterator<LogicPage<?>> iterator = systemTableSpace.sequentialTraversalIterator();
        if (verbose && log.isDebugEnabled()) {
            log.debug("Tablespace {} 总区数 {} 总页数 {}", systemTableSpace,
                    systemTableSpace.totalExtendNumber(),
                    systemTableSpace.totalPageNumber());
        }

        System.out.println(LogicPage.title());
        System.out.println();
        iterator.forEachRemaining(System.out::println);
    }
}
