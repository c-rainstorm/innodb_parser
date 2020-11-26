package me.rainstorm.innodb.domain.page.xdes;

import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.core.PageBody;
import me.rainstorm.innodb.domain.page.fsp.FileSpaceHeader;

/**
 * @author traceless
 */
public class ExtentDescriptorPageBody<Header> extends PageBody {
    private static final int FIRST_XDES_ENTRY_OFFSET = FileSpaceHeader.OFFSET + FileSpaceHeader.LENGTH;

    private final Header fileSpaceHeader;

    public ExtentDescriptorPageBody(PhysicalPage physicalPage, Header header) {
        fileSpaceHeader = header;
        // todo
    }

    public Header getFileSpaceHeader() {
        return fileSpaceHeader;
    }
}
