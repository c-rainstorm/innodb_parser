package me.rainstorm.innodb.parser;

import me.rainstorm.innodb.parser.options.CommandLineArgs;
import me.rainstorm.innodb.parser.options.CommandLineOptionEnum;
import me.rainstorm.innodb.parser.strategy.cles.CommandLineExecuteStrategy;
import me.rainstorm.innodb.parser.strategy.cles.level_1.HelpStrategy;
import me.rainstorm.innodb.parser.strategy.cles.level_1.VersionStrategy;
import me.rainstorm.innodb.parser.strategy.cles.level_2.FilePerTableTableSpacePages;
import me.rainstorm.innodb.parser.strategy.cles.level_2.SystemTableSpaceAllPage;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;

public class BootstrapTest {
    @Test
    public void none() throws ParseException {
        String[] args = new String[]{};

        strategyAssert(args, HelpStrategy.class);

        Bootstrap.main(args);
    }

    @Test
    public void help() throws ParseException {
        String[] args = new String[]{CommandLineOptionEnum.Help.getLongOpt()};

        strategyAssert(args, HelpStrategy.class);

        Bootstrap.main(args);
    }

    @Test
    public void version() throws ParseException {
        String[] args = new String[]{CommandLineOptionEnum.Version.getLongOpt()};

        strategyAssert(args, VersionStrategy.class);

        Bootstrap.main(args);
    }

    @ParameterizedTest
    @CsvSource("/tmp/mysql/")
    public void systemSpaceAllPage(String dataDir) throws ParseException {
        List<String> argsList = new ArrayList<>();

        argsList.add(CommandLineOptionEnum.DataDir.getLongOpt() + "=" + dataDir);

        argsList.add(CommandLineOptionEnum.Verbose.getLongOpt());

        String[] args = argsList.toArray(new String[0]);

        strategyAssert(args, SystemTableSpaceAllPage.class);

        Bootstrap.main(args);
    }

    @ParameterizedTest
    @CsvSource("/tmp/mysql/,sparrow,test")
    public void FilePerTableTableSpacePages(String dataDir, String database, String table) throws ParseException {
        String[] args = new String[]{
                CommandLineOptionEnum.DataDir.getLongOpt() + "=" + dataDir,
                CommandLineOptionEnum.Database.getLongOpt() + "=" + database,
                CommandLineOptionEnum.Table.getLongOpt() + "=" + table
        };

        strategyAssert(args, FilePerTableTableSpacePages.class);

        Bootstrap.main(args);
    }

    private void strategyAssert(String[] args, Class<? extends CommandLineExecuteStrategy> expectedStrategyClass) throws ParseException {
        CommandLineArgs commandLineArgs = new CommandLineArgs(args);
        Bootstrap.setGlobalConstants(commandLineArgs);
        CommandLineExecuteStrategy strategy = Bootstrap.findExecuteStrategy(commandLineArgs);
        Assertions.assertEquals(expectedStrategyClass, strategy.getClass());
    }
}
