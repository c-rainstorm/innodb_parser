package me.rainstorm.innodb.domain.page.core.list;

import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * 链表根节点
 *
 * @author traceless
 */
@Getter
public class ListBaseNode {
    private final int length;
    private final int firstNodePageNumber;
    private final short firstNodeOffset;
    private final int lastNodePageNumber;
    private final short lastNodeOffset;

    public ListBaseNode(ByteBuffer byteBuffer) {
        this.length = byteBuffer.getInt();
        this.firstNodePageNumber = byteBuffer.getInt();
        this.firstNodeOffset = byteBuffer.getShort();
        this.lastNodePageNumber = byteBuffer.getInt();
        this.lastNodeOffset = byteBuffer.getShort();
    }
}
