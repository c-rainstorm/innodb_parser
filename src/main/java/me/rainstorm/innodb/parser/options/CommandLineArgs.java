package me.rainstorm.innodb.parser.options;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

import static me.rainstorm.innodb.domain.InnodbConstants.MYSQL_INNODB_VERSION;
import static me.rainstorm.innodb.parser.ParserConstants.INNODB_PARSER_VERSION;
import static me.rainstorm.innodb.parser.ParserConstants.verbose;
import static me.rainstorm.innodb.parser.options.CommandLineOptionEnum.*;

/**
 * @author traceless
 */
@Slf4j
public class CommandLineArgs {
    private final CommandLine commandLineParsed;

    public CommandLineArgs(String[] args) throws ParseException {
        this.commandLineParsed = new DefaultParser().parse(CommandLineOptionEnum.getOptions(), args);
    }

    public boolean contains(CommandLineOptionEnum verbose) {
        return commandLineParsed.hasOption(verbose.getOpt());
    }

    public boolean containHelp() {
        return contains(Help);
    }

    public boolean containVersion() {
        return contains(Version);
    }

    public static String version() {
        return String.format("MySQL Innodb version: %s\nInnodb parser version: %s", MYSQL_INNODB_VERSION, INNODB_PARSER_VERSION);
    }

    public boolean withoutOptions() {
        return commandLineParsed.getOptions().length == 0;
    }

    public Path systemTableSpace() {
        String dataDir = dataDir();

        String systemTableSpaceFileName = commandLineParsed.getOptionValue(SystemTableSpace.getOpt(), SystemTableSpace.getDefaultValue());
        Path systemTableSpace = Paths.get(dataDir, systemTableSpaceFileName);

        if (verbose && log.isDebugEnabled()) {
            log.debug("已解析的系统表空间文件名：{}, 绝对路径：{} ", systemTableSpaceFileName, systemTableSpace);
        }
        return systemTableSpace;
    }

    public Path filePerTableTableSpace() {
        Path tableSpacePath = Paths.get(dataDir(), database(), table() + ".ibd");

        if (verbose && log.isDebugEnabled()) {
            log.debug("已解析的独立表空间绝对路径：{} ", tableSpacePath);
        }
        return tableSpacePath;
    }

    public String dataDir() {
        return StringUtils.trimToEmpty(commandLineParsed.getOptionValue(DataDir.getOpt(), DataDir.getDefaultValue()));
    }

    public String database() {
        return StringUtils.trimToEmpty(commandLineParsed.getOptionValue(Database.getOpt()));
    }

    public String table() {
        return StringUtils.trimToEmpty(commandLineParsed.getOptionValue(Table.getOpt()));
    }
}
