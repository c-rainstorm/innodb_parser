package me.rainstorm.innodb.parser.strategy.cles.level_2;

import lombok.extern.slf4j.Slf4j;
import me.rainstorm.innodb.domain.page.LogicPage;
import me.rainstorm.innodb.domain.tablespace.IndependentTableSpace;
import me.rainstorm.innodb.parser.options.CommandLineArgs;
import me.rainstorm.innodb.parser.options.CommandLineOptionEnum;
import me.rainstorm.innodb.parser.strategy.cles.CommandLineExecuteStrategy;

import static me.rainstorm.innodb.common.i18n.I18nMsgCodeEnum.LogTableSpaceSummary;
import static me.rainstorm.innodb.common.i18n.I18nUtil.message;
import static me.rainstorm.innodb.parser.ParserConstants.VERBOSE;

/**
 * @author traceless
 */
@Slf4j
public class FilePerTableTableSpacePages extends CommandLineExecuteStrategy {
    public static final int ORDER = SystemTableSpaceAllPage.ORDER - 1;

    public FilePerTableTableSpacePages() {
        setOrder(ORDER);
    }

    @Override
    public boolean match(CommandLineArgs commandLineArgs) {
        return commandLineArgs.contains(CommandLineOptionEnum.Database) &&
                commandLineArgs.contains(CommandLineOptionEnum.Table);
    }

    @Override
    public void execute(CommandLineArgs commandLineArgs) {
        IndependentTableSpace independentTableSpace = new IndependentTableSpace(commandLineArgs.filePerTableTableSpace());
        if (VERBOSE && log.isDebugEnabled()) {
            log.debug(message(LogTableSpaceSummary, independentTableSpace,
                    independentTableSpace.totalExtendNumber(),
                    independentTableSpace.totalPageNumber()));
        }
        System.out.println(LogicPage.title());
        independentTableSpace.sequentialTraversalIterator().forEachRemaining(System.out::println);
    }
}
