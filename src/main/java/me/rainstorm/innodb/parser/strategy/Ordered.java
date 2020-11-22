package me.rainstorm.innodb.parser.strategy;

/**
 * @author traceless
 */
public interface Ordered<T> extends Comparable<T> {
    /**
     * 大的优先匹配
     *
     * @return 已定义的顺序
     */
    int order();
}
