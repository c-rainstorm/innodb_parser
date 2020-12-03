package me.rainstorm.innodb.domain.page.sys;

import me.rainstorm.innodb.domain.page.LogicPage;
import me.rainstorm.innodb.domain.page.PhysicalPage;

/**
 * @author traceless
 */
public class DataDirectoryHeaderPage extends LogicPage<DataDirectoryPageBody> {

    public DataDirectoryHeaderPage(PhysicalPage physicalPage) {
        super(physicalPage);
    }

    @Override
    protected DataDirectoryPageBody createPageBody(PhysicalPage physicalPage) {
        return new DataDirectoryPageBody(physicalPage);
    }
}
