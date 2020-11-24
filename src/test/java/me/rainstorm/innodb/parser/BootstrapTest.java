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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class BootstrapTest {
    @Test
    public void none() throws ParseException {
        String[] args = new String[]{};

        strategyAssert(args, HelpStrategy.class);

        Bootstrap.main(args);
    }

    @ParameterizedTest
    @CsvSource({"EN_US", "ZH_CN", "xxxx"})
    public void help(String locale) throws ParseException {
        String[] args = new String[]{
                CommandLineOptionEnum.Help.getShortOpt(),
                CommandLineOptionEnum.I18N.getShortOpt() + "=" + locale
        };

        strategyAssert(args, HelpStrategy.class);

        Bootstrap.main(args);
    }

    @Test
    public void version() throws ParseException {
        String[] args = new String[]{CommandLineOptionEnum.Version.getShortOpt()};

        strategyAssert(args, VersionStrategy.class);

        Bootstrap.main(args);
    }

    @ParameterizedTest
    @CsvSource("/tmp/mysql/")
    public void systemSpaceAllPage(String dataDir) throws ParseException {
        List<String> argsList = new ArrayList<>();

        argsList.add(CommandLineOptionEnum.DataDir.getShortOpt() + "=" + dataDir);

        argsList.add(CommandLineOptionEnum.Verbose.getShortOpt());

        String[] args = argsList.toArray(new String[0]);

        strategyAssert(args, SystemTableSpaceAllPage.class);

        Bootstrap.main(args);
    }

    @ParameterizedTest
    @CsvSource({"true,ZH_CN,/tmp/mysql/,sparrow,test",
            "true,EN_US,/tmp/mysql/,sparrow,test",
            "false,ZH_CN,/tmp/mysql/,sparrow,test",
            "false,EN_US,/tmp/mysql/,sparrow,test"})
    public void FilePerTableTableSpacePages(boolean verbose, String locale, String dataDir, String database, String table) throws ParseException {
        String[] args = new String[]{
                CommandLineOptionEnum.DataDir.getShortOpt() + "=" + dataDir,
                CommandLineOptionEnum.Database.getShortOpt() + "=" + database,
                CommandLineOptionEnum.Table.getShortOpt() + "=" + table,
                CommandLineOptionEnum.I18N.getShortOpt() + "=" + locale
        };

        if (verbose) {
            args = addArg(args, CommandLineOptionEnum.Verbose.getShortOpt());
        }

        strategyAssert(args, FilePerTableTableSpacePages.class);

        Bootstrap.main(args);
    }

    private String[] addArg(String[] args, String shortOpt) {
        return Stream.concat(Stream.of(shortOpt), Arrays.stream(args)).toArray(String[]::new);
    }

    private void strategyAssert(String[] args, Class<? extends CommandLineExecuteStrategy> expectedStrategyClass) throws ParseException {
        CommandLineArgs commandLineArgs = new CommandLineArgs(args);
        Bootstrap.setGlobalConstants(commandLineArgs);
        CommandLineExecuteStrategy strategy = Bootstrap.findExecuteStrategy(commandLineArgs);
        Assertions.assertEquals(expectedStrategyClass, strategy.getClass());
    }
}
