package me.rainstorm.innodb.domain.page.xdes;

import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.core.PageBody;

/**
 * @author traceless
 */
public class ExtentDescriptorPageBody<Header> extends PageBody {
    private final Header fileSpaceHeader;

    public ExtentDescriptorPageBody(PhysicalPage physicalPage, Header header) {
        fileSpaceHeader = header;
        // todo
    }

    public Header getFileSpaceHeader() {
        return fileSpaceHeader;
    }
}
