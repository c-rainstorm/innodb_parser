package me.rainstorm.innodb.domain.page.core;

import me.rainstorm.innodb.domain.page.LogicPage;
import me.rainstorm.innodb.domain.page.PhysicalPage;

/**
 * @author traceless
 */
public class SimplePage extends LogicPage<Undefined> {

    public SimplePage(PhysicalPage physicalPage) {
        super(physicalPage);
    }

    @Override
    protected Undefined createPageBody(PhysicalPage physicalPage) {
        return Undefined.INSTANCE;
    }
}
