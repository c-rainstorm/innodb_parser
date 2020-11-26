package me.rainstorm.innodb.domain.page.core.list;

import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * @author traceless
 */
@Getter
public class ListNode {
    public static final int LENGTH = 12;
    private final int preInodePageNumber;
    private final short prePageOffset;
    private final int nextInodePageNumber;
    private final short nextPageOffset;

    public ListNode(ByteBuffer buffer) {
        preInodePageNumber = buffer.getInt();
        prePageOffset = buffer.getShort();
        nextInodePageNumber = buffer.getInt();
        nextPageOffset = buffer.getShort();
    }
}
