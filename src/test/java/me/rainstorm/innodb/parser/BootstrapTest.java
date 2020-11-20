package me.rainstorm.innodb.parser;

import me.rainstorm.innodb.parser.options.CommandLineArgEnum;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;

public class BootstrapTest {
    @Test
    public void none() {
        Bootstrap.main(new String[]{});
    }

    @Test
    public void help() {
        Bootstrap.main(new String[]{CommandLineArgEnum.Help.getLongOpt()});
    }

    @Test
    public void version() {
        Bootstrap.main(new String[]{CommandLineArgEnum.Version.getLongOpt()});
    }

    @ParameterizedTest
    @CsvSource("/tmp/mysql/,sparrow,test")
    public void table(String systemSpace, String database, String table) {
        List<String> args = new ArrayList<>();

        args.add(CommandLineArgEnum.SystemSpace.getLongOpt() + "=" + systemSpace + " ");

        if (StringUtils.isNoneBlank(database)) {
            args.add(CommandLineArgEnum.Database.getLongOpt() + "=" + database + " ");
        }

        if (StringUtils.isNoneBlank(table)) {
            args.add(CommandLineArgEnum.Table.getLongOpt() + "=" + table + " ");
        }

        args.add(CommandLineArgEnum.Verbose.getLongOpt());

        Bootstrap.main(args.toArray(new String[0]));
    }
}
