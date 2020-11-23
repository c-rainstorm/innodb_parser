package me.rainstorm.innodb.domain.page;

import me.rainstorm.innodb.domain.page.core.Undefined;

/**
 * @author traceless
 */
public class AllocatedPage extends LogicPage<Undefined> {
    public AllocatedPage(PhysicalPage physicalPage) {
        super(physicalPage);
    }

    @Override
    protected Undefined createPageBody(PhysicalPage physicalPage) {
        return Undefined.INSTANCE;
    }

    @Override
    public String toString() {
        return String.format("[%10s][%10s]Page <%s>", "", "", pageTypeDesc());
    }
}
