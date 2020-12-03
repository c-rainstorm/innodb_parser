package me.rainstorm.innodb.parser;


import me.rainstorm.innodb.parser.options.CommandLineArgs;
import me.rainstorm.innodb.parser.options.CommandLineOptionEnum;
import me.rainstorm.innodb.parser.strategy.cles.CommandLineExecuteStrategy;
import me.rainstorm.innodb.parser.strategy.cles.level_1.HelpStrategy;
import me.rainstorm.innodb.parser.strategy.cles.level_1.VersionStrategy;
import me.rainstorm.innodb.parser.strategy.cles.level_2.FilePerTableTableSpacePageExport;
import me.rainstorm.innodb.parser.strategy.cles.level_2.SystemTableSpacePageExport;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.util.Arrays;
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
    @CsvSource({"/tmp/mysql/,bolt://localhost:7688,neo4j,innodb"})
    public void SystemTableSpaceExport(String dataDir, String neo4jUrl, String neo4jUser, String neo4jPassword) throws ParseException, IOException {
        String[] args = new String[]{
                CommandLineOptionEnum.DataDir.getShortOpt() + "=" + dataDir,
                CommandLineOptionEnum.Verbose.getShortOpt(),
                CommandLineOptionEnum.Export.getShortOpt()
        };

        System.setProperty(ParserConstants.PROPERTY_NEO4J_URL, neo4jUrl);
        System.setProperty(ParserConstants.PROPERTY_NEO4J_USER, neo4jUser);
        System.setProperty(ParserConstants.PROPERTY_NEO4J_PASSWORD, neo4jPassword);

        strategyAssert(args, SystemTableSpacePageExport.class);

        Bootstrap.main(args);
    }

    @ParameterizedTest
    @CsvSource({"/tmp/mysql/,sparrow,test,bolt://localhost:7687,neo4j,innodb"})
    public void FilePerTableTableSpacePageExport(String dataDir, String database, String table, String neo4jUrl, String neo4jUser, String neo4jPassword) throws ParseException, IOException {
        String[] args = new String[]{
                CommandLineOptionEnum.DataDir.getShortOpt() + "=" + dataDir,
                CommandLineOptionEnum.Database.getShortOpt() + "=" + database,
                CommandLineOptionEnum.Table.getShortOpt() + "=" + table,
                CommandLineOptionEnum.Verbose.getShortOpt(),
                CommandLineOptionEnum.Export.getShortOpt()
        };

        System.setProperty(ParserConstants.PROPERTY_NEO4J_URL, neo4jUrl);
        System.setProperty(ParserConstants.PROPERTY_NEO4J_USER, neo4jUser);
        System.setProperty(ParserConstants.PROPERTY_NEO4J_PASSWORD, neo4jPassword);

        strategyAssert(args, FilePerTableTableSpacePageExport.class);

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
