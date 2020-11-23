package me.rainstorm.innodb.parser.strategy;

/**
 * @author traceless
 */
public interface Strategy<ConditionProvider> {
    /**
     * 规则是否匹配
     */
    boolean match(ConditionProvider conditionProvider);

    /**
     * 执行
     */
    void execute(ConditionProvider conditionProvider);
}
