package me.rainstorm.innodb.parser.strategy.cles.level_2;

import me.rainstorm.innodb.domain.page.LogicPage;
import me.rainstorm.innodb.domain.tablespace.FilePerTableTableSpace;
import me.rainstorm.innodb.parser.options.CommandLineArgs;
import me.rainstorm.innodb.parser.options.CommandLineOptionEnum;
import me.rainstorm.innodb.parser.strategy.cles.CommandLineExecuteStrategy;

/**
 * @author traceless
 */
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
        FilePerTableTableSpace filePerTableTableSpace = new FilePerTableTableSpace(commandLineArgs.filePerTableTableSpace());
        System.out.println(LogicPage.title());
        filePerTableTableSpace.sequentialTraversalIterator().forEachRemaining(System.out::println);
    }
}
