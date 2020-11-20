package me.rainstorm.innodb.parser;

import lombok.extern.slf4j.Slf4j;
import me.rainstorm.innodb.parser.options.CommandLineArgsHelper;
import org.apache.commons.cli.ParseException;

import java.util.Arrays;

import static me.rainstorm.innodb.parser.constant.Constants.verbose;

/**
 * @author traceless
 */
@Slf4j
public class Bootstrap {
    public static void main(String[] args) {
        CommandLineArgsHelper commandLineArgsHelper;
        try {
            commandLineArgsHelper = new CommandLineArgsHelper(args);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            try {
                new CommandLineArgsHelper().printHelp();
            } catch (ParseException parseException) {
                log.error(e.getMessage(), e);
            }
            return;
        }

        commandLineArgsHelper.setGlobalConstants();
        if (verbose && log.isDebugEnabled()) {
            log.debug("原始命令行参数：" + Arrays.toString(args));
        }

        if (commandLineArgsHelper.withoutOptions() || commandLineArgsHelper.isHelp()) {
            commandLineArgsHelper.printHelp();
            return;
        }

        if (commandLineArgsHelper.isVersion()) {
            System.out.println(commandLineArgsHelper.version());
            return;
        }
    }
}
