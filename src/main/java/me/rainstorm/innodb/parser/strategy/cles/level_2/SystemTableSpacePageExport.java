package me.rainstorm.innodb.parser.strategy.cles.level_2;

import lombok.extern.slf4j.Slf4j;
import me.rainstorm.innodb.common.neo4j.Neo4jHelper;
import me.rainstorm.innodb.domain.tablespace.SystemTableSpace;
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
public class SystemTableSpacePageExport extends CommandLineExecuteStrategy {
    public static final int ORDER = FilePerTableTableSpacePageExport.ORDER - 1;

    public SystemTableSpacePageExport() {
        setOrder(ORDER);
    }

    @Override
    public boolean match(CommandLineArgs commandLineArgs) {
        return commandLineArgs.contains(CommandLineOptionEnum.Export) &&
                !commandLineArgs.contains(CommandLineOptionEnum.Database) &&
                !commandLineArgs.contains(CommandLineOptionEnum.Table);
    }

    @Override
    public void execute(CommandLineArgs commandLineArgs) {
        SystemTableSpace systemTableSpace = new SystemTableSpace(commandLineArgs.systemTableSpace());
        if (VERBOSE && log.isDebugEnabled()) {
            log.debug(message(LogTableSpaceSummary, systemTableSpace,
                    systemTableSpace.totalExtentNumber(),
                    systemTableSpace.totalPageNumber()));
        }

        try (Neo4jHelper neo4jHelper = new Neo4jHelper()) {
            systemTableSpace.sequentialTraversalIterator().forEachRemaining(page -> page.addNodes(neo4jHelper));
            systemTableSpace.sequentialTraversalIterator().forEachRemaining(page -> page.linkNodes(neo4jHelper));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
