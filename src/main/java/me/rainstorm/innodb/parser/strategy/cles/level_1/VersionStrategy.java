package me.rainstorm.innodb.parser.strategy.cles.level_1;

import lombok.extern.slf4j.Slf4j;
import me.rainstorm.innodb.parser.options.CommandLineArgs;
import me.rainstorm.innodb.parser.strategy.cles.CLESExecuteWithOutCLA;

/**
 * @author traceless
 */
@Slf4j
public class VersionStrategy extends CLESExecuteWithOutCLA {
    private static final int ORDER = HelpStrategy.ORDER - 1;

    public VersionStrategy() {
        setOrder(ORDER);
    }

    @Override
    public boolean match(CommandLineArgs commandLineArgs) {
        return commandLineArgs.containVersion();
    }

    @Override
    public void execute() {
        System.out.println(CommandLineArgs.version());
    }
}
