package me.rainstorm.innodb.parser.strategy.cles;

import me.rainstorm.innodb.parser.options.CommandLineArgs;

/**
 * @author traceless
 */
public abstract class CLESExecuteWithOutCLA extends CommandLineExecuteStrategy {

    @Override
    public void execute(CommandLineArgs commandLineArgs) {
        execute();
    }

    public abstract void execute();
}
