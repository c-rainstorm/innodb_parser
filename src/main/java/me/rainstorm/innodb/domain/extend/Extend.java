package me.rainstorm.innodb.domain.extend;

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
 * An Extend in TableSpace
 *
 * @author traceless
 */
@Slf4j
@Getter
public class Extend {
    private final TableSpace tableSpace;
    private final int extendOffset;
    private final ByteBuffer data;

    public Extend(TableSpace tableSpace, int extendOffset, ByteBuffer data) {
        this.tableSpace = tableSpace;
        this.extendOffset = extendOffset;
        this.data = data;
    }

    /**
     * load nth Page in this Extend
     *
     * @param pageOffsetInExtend n
     * @param <Page>             the Page
     * @return the Page
     */
    public <Page extends LogicPage<? extends PageBody>> Page page(int pageOffsetInExtend) {
        PhysicalPage physicalPage = new PhysicalPage(this, pageOffsetInExtend,
                ByteBuffer.wrap(data.array(), pageOffsetInExtend * PAGE_SIZE, PAGE_SIZE).asReadOnlyBuffer());
        return (Page) LogicPageFactory.of(physicalPage);
    }

    public static int pageOffsetOfExtend(int pageNo) {
        return pageNo % PAGE_NUM_IN_EXTEND;
    }
}
