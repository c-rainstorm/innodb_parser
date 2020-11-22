package me.rainstorm.innodb.parser.strategy;

/**
 * @author traceless
 */
public abstract class AbstractStrategy<ConditionProvider> implements Strategy<ConditionProvider>, Ordered<AbstractStrategy<?>> {
    protected int order;

    protected void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int order() {
        return order;
    }

    @Override
    public int compareTo(AbstractStrategy o) {
        int r = this.order() - o.order();
        return r != 0 ? r : this.getClass().getName().compareTo(o.getClass().getName());
    }
}
