package me.rainstorm.innodb.parser.strategy.cles.level_2;

import lombok.extern.slf4j.Slf4j;
import me.rainstorm.innodb.common.neo4j.Neo4jHelper;
import me.rainstorm.innodb.domain.tablespace.IndependentTableSpace;
import me.rainstorm.innodb.parser.options.CommandLineArgs;
import me.rainstorm.innodb.parser.options.CommandLineOptionEnum;
import me.rainstorm.innodb.parser.strategy.cles.CommandLineExecuteStrategy;

import java.nio.file.Paths;

import static me.rainstorm.innodb.common.i18n.I18nMsgCodeEnum.LogTableSpaceSummary;
import static me.rainstorm.innodb.common.i18n.I18nUtil.message;
import static me.rainstorm.innodb.parser.ParserConstants.VERBOSE;

/**
 * @author traceless
 */
@Slf4j
public class FilePerTableTableSpacePageExport extends CommandLineExecuteStrategy {
    public static final int ORDER = FilePerTableTableSpacePages.ORDER - 1;

    public FilePerTableTableSpacePageExport() {
        setOrder(ORDER);
    }

    @Override
    public boolean match(CommandLineArgs commandLineArgs) {
        return commandLineArgs.contains(CommandLineOptionEnum.Export) &&
                commandLineArgs.contains(CommandLineOptionEnum.Database) &&
                commandLineArgs.contains(CommandLineOptionEnum.Table);
    }

    @Override
    public void execute(CommandLineArgs commandLineArgs) {
        IndependentTableSpace independentTableSpace = new IndependentTableSpace(commandLineArgs.filePerTableTableSpace(),
                Paths.get(".", commandLineArgs.database(), commandLineArgs.table()));
        if (VERBOSE && log.isDebugEnabled()) {
            log.debug(message(LogTableSpaceSummary, independentTableSpace,
                    independentTableSpace.totalExtentNumber(),
                    independentTableSpace.totalPageNumber()));
        }

        try (Neo4jHelper neo4jHelper = new Neo4jHelper()) {
            independentTableSpace.sequentialTraversalIterator().forEachRemaining(page -> page.addNodes(neo4jHelper));
            independentTableSpace.sequentialTraversalIterator().forEachRemaining(page -> page.linkNodes(neo4jHelper));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
