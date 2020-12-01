package me.rainstorm.innodb.domain.page.index;

import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * @author traceless
 */
@Getter
public class SegmentPointer {
    private final int spaceId;
    private final int inodePageNo;
    private final short offset;

    public SegmentPointer(ByteBuffer buffer) {
        this.spaceId = buffer.getInt();
        this.inodePageNo = buffer.getInt();
        this.offset = buffer.getShort();
    }
}
