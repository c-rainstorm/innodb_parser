package me.rainstorm.innodb.domain.page.sys.rbseg;

import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * @author traceless
 */
@Getter
public class RollbackSegmentPagePointer {
    private final int spaceId;
    private final int pageNo;

    public RollbackSegmentPagePointer(ByteBuffer buffer) {
        spaceId = buffer.getInt();
        pageNo = buffer.getInt();
    }

    public boolean isValid() {
        return spaceId != -1 && pageNo != -1;
    }
}
