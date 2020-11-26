package me.rainstorm.innodb.domain.page.xdes;

import me.rainstorm.innodb.domain.page.LogicPage;
import me.rainstorm.innodb.domain.page.PhysicalPage;

/**
 * 区描述符页，每区的第 0 号页
 *
 * @author traceless
 */
public abstract class AbstractExtentDescriptorPage<T> extends LogicPage<ExtentDescriptorPageBody<T>> {
    public AbstractExtentDescriptorPage(PhysicalPage physicalPage) {
        super(physicalPage);
    }
}
