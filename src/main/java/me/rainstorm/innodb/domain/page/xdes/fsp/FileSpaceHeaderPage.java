package me.rainstorm.innodb.domain.page.xdes.fsp;

import me.rainstorm.innodb.domain.page.LogicPage;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.xdes.ExtentDescriptorPageBody;

/**
 * 特殊的区描述符页，
 * 表空间，第 0 个区的 0 号页
 *
 * @author traceless
 */
public class FileSpaceHeaderPage extends LogicPage<ExtentDescriptorPageBody<FileSpaceHeader>> {
    public static final int PAGE_NO = 0;

    public FileSpaceHeaderPage(PhysicalPage physicalPage) {
        super(physicalPage);
    }

    @Override
    protected ExtentDescriptorPageBody<FileSpaceHeader> createPageBody(PhysicalPage physicalPage) {
        return new ExtentDescriptorPageBody<>(physicalPage, new FileSpaceHeader(physicalPage));
    }

    public int totalPageNumber() {
        return body.getFileSpaceHeader().getTotalPageNumber();
    }
}
