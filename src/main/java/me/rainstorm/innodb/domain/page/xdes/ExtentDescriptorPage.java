package me.rainstorm.innodb.domain.page.xdes;

import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.core.Undefined;

/**
 * @author traceless
 */
public class ExtentDescriptorPage extends AbstractExtentDescriptorPage<Undefined> {
    public ExtentDescriptorPage(PhysicalPage physicalPage) {
        super(physicalPage);
    }

    @Override
    protected ExtentDescriptorPageBody<Undefined> createPageBody(PhysicalPage physicalPage) {
        return new ExtentDescriptorPageBody<>(physicalPage, Undefined.INSTANCE);
    }
}
