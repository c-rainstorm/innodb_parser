package me.rainstorm.innodb.domain.page;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.rainstorm.innodb.domain.extend.Extend;
import me.rainstorm.innodb.domain.page.core.FileHeader;

import java.nio.ByteBuffer;

import static me.rainstorm.innodb.domain.InnodbConstants.PAGE_NUM_IN_EXTEND;

/**
 * @author traceless
 */
@Slf4j
@Getter
public class PhysicalPage {
    private final int pageNo;
    private final Extend extend;
    private final int pageOffsetInExtend;
    private final ByteBuffer data;

    public PhysicalPage(Extend extend, int pageOffsetInExtend, ByteBuffer data) {
        assert data.isReadOnly();
        this.pageNo = extend.getExtendOffset() * PAGE_NUM_IN_EXTEND + pageOffsetInExtend;
        this.extend = extend;
        this.pageOffsetInExtend = pageOffsetInExtend;
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
