package me.rainstorm.innodb.domain.segment;

import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * @author traceless
 */
@Getter
public class SegmentPointer {
    public static final int LENGTH = 10;
    private final int spaceId;
    private final int inodePageNo;
    private final short offset;

    public SegmentPointer(ByteBuffer buffer) {
        this.spaceId = buffer.getInt();
        this.inodePageNo = buffer.getInt();
        this.offset = buffer.getShort();
    }
}
