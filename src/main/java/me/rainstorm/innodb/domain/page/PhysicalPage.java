package me.rainstorm.innodb.domain.page;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.rainstorm.innodb.domain.extent.Extent;
import me.rainstorm.innodb.domain.page.core.FileHeader;

import java.nio.ByteBuffer;

import static me.rainstorm.innodb.domain.InnodbConstants.PAGE_NUM_IN_EXTENT;

/**
 * @author traceless
 */
@Slf4j
@Getter
public class PhysicalPage {
    private final int pageNo;
    private final Extent extent;
    private final int pageOffsetInExtent;
    private final ByteBuffer data;

    public PhysicalPage(Extent extent, int pageOffsetInExtent, ByteBuffer data) {
        assert data.isReadOnly();
        this.pageNo = extent.getExtentNo() * PAGE_NUM_IN_EXTENT + pageOffsetInExtent;
        this.extent = extent;
        this.pageOffsetInExtent = pageOffsetInExtent;
        this.data = data;
    }

    public ByteBuffer getData(int offset) {
        assert offset >= 0;
        ByteBuffer buffer = data.duplicate();
        buffer.position(buffer.position() + offset);
        return buffer;
    }

    public PageTypeEnum pageType() {
        return FileHeader.pageType(this);
    }
}
