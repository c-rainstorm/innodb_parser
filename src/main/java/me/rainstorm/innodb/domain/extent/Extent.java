package me.rainstorm.innodb.domain.extent;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.rainstorm.innodb.domain.page.LogicPage;
import me.rainstorm.innodb.domain.page.LogicPageFactory;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.core.PageBody;
import me.rainstorm.innodb.domain.tablespace.TableSpace;

import java.nio.ByteBuffer;

import static me.rainstorm.innodb.domain.InnodbConstants.PAGE_NUM_IN_EXTEND;
import static me.rainstorm.innodb.domain.InnodbConstants.PAGE_SIZE;

/**
 * An Extent in TableSpace
 *
 * @author traceless
 */
@Slf4j
@Getter
public class Extent {
    private final TableSpace tableSpace;
    private final int extentOffset;
    private final ByteBuffer data;

    public Extent(TableSpace tableSpace, int extendOffset, ByteBuffer data) {
        this.tableSpace = tableSpace;
        this.extentOffset = extendOffset;
        this.data = data;
    }

    /**
     * load nth Page in this Extent
     *
     * @param pageOffsetInExtent n
     * @param <Page>             the Page
     * @return the Page
     */
    public <Page extends LogicPage<? extends PageBody>> Page page(int pageOffsetInExtent) {
        PhysicalPage physicalPage = new PhysicalPage(this, pageOffsetInExtent,
                ByteBuffer.wrap(data.array(), pageOffsetInExtent * PAGE_SIZE, PAGE_SIZE).asReadOnlyBuffer());
        return (Page) LogicPageFactory.of(physicalPage);
    }

    public static int pageOffsetOfExtent(int pageNo) {
        return pageNo % PAGE_NUM_IN_EXTEND;
    }
}
