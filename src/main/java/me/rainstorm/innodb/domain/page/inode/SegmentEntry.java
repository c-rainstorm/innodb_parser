package me.rainstorm.innodb.domain.page.inode;

import lombok.Getter;
import me.rainstorm.innodb.domain.page.core.list.ListBaseNode;

import java.nio.ByteBuffer;

/**
 * 段是一些零散页面和一些完整的区的集合
 *
 * @author traceless
 */
@Getter
public class SegmentEntry {
    public static final int LENGTH = 192;
    /**
     * 段 ID
     */
    private final long segmentId;
    /**
     * 段内未使用的页列表
     */
    private final ListBaseNode freeList;
    /**
     * 段内有剩余空间的页列表
     */
    private final ListBaseNode notFullList;
    /**
     * 段内无剩余空间的页链表
     */
    private final ListBaseNode fullList;
    /**
     * 这个值是用来标记这个 INODE Entry 是否已经被初始化了（初始化的意思就是把各个字段的值都填进去了）。
     * 如果这个数字是值的97937874，表明该INODE Entry已经初始化，否则没有被初始化。
     */
    private final int magicNumber;
    /**
     * 属于这个区的最多 32 个页，若超过 32，则会申请一个新 Extent，并将碎片页内容移过去
     */
    private final int[] fragmentPages = new int[32];

    public SegmentEntry(ByteBuffer buffer) {
        segmentId = buffer.getLong();
        buffer.getInt();
        freeList = new ListBaseNode(buffer);
        notFullList = new ListBaseNode(buffer);
        fullList = new ListBaseNode(buffer);
        magicNumber = buffer.getInt();
        for (int i = 0; i < fragmentPages.length; i++) {
            fragmentPages[i] = buffer.getInt();
        }
    }
}
