package me.rainstorm.innodb.domain.page.xdes;

import me.rainstorm.innodb.domain.page.LogicPage;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.core.Undefined;

/**
 * 区描述符页，每区的第 0 号页
 *
 * @author traceless
 */
public class ExtentDescriptorPage extends LogicPage<ExtentDescriptorPageBody<Undefined>> {
    public ExtentDescriptorPage(PhysicalPage physicalPage) {
        super(physicalPage);
    }

    @Override
    protected ExtentDescriptorPageBody<Undefined> createPageBody(PhysicalPage physicalPage) {
        return new ExtentDescriptorPageBody<>(physicalPage, Undefined.INSTANCE);
    }
}
