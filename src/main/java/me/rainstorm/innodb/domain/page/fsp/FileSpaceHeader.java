package me.rainstorm.innodb.domain.page.fsp;

import lombok.Getter;
import me.rainstorm.innodb.domain.page.PageTypeEnum;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.core.FileHeader;
import me.rainstorm.innodb.domain.page.core.list.ListBaseNode;

import java.nio.ByteBuffer;

/**
 * @author traceless
 */
@Getter
public class FileSpaceHeader {
    public static final int OFFSET = FileHeader.OFFSET + FileHeader.LENGTH;
    public static final int LENGTH = 112;

    /**
     * 表空间ID
     */
    private final int spaceId;
    /**
     * 表空间页面总数量
     */
    private final int totalPageNumber;

    /**
     * 未被初始化的最小页号
     * 大于或等于这个页号的区对应的XDES Entry结构都没有被加入FREE链表
     */
    private final int minimumUninitializedPageNumber;

    /**
     * 表空间的一些占用存储空间比较小的属性
     */
    private final int tableSpaceFlags;

    /**
     * FREE_FRAG 链表中已使用的页面数量
     */
    private final int pageUsedInFreeFragmentList;

    /**
     * 空闲的区,直属于表空间
     */
    private final ListBaseNode freeList;

    /**
     * 有剩余空间的碎片区,直属于表空间
     */
    private final ListBaseNode freeFragmentList;

    /**
     * 没有剩余空间的碎片区,直属于表空间
     */
    private final ListBaseNode fullFragmentList;

    /**
     * 当前表空间中下一个未使用的 Segment ID
     */
    private final long nextUnusedSegmentId;

    /**
     * 每个段一个 Segment Entry 条目，专门放在 INODE 页
     * 当页内放满了，页就会从 segmentInodesFreeList 移动到 segmentInodesFullList
     *
     * @see PageTypeEnum#Inode
     */
    private final ListBaseNode segmentInodesFullList;

    private final ListBaseNode segmentInodesFreeList;

    public FileSpaceHeader(PhysicalPage physicalPage) {
        ByteBuffer byteBuffer = physicalPage.getData(OFFSET);
        spaceId = byteBuffer.getInt();
        // 4 字节未使用
        byteBuffer.getInt();
        totalPageNumber = byteBuffer.getInt();
        minimumUninitializedPageNumber = byteBuffer.getInt();
        tableSpaceFlags = byteBuffer.getInt();
        pageUsedInFreeFragmentList = byteBuffer.getInt();
        freeList = new ListBaseNode(byteBuffer);
        freeFragmentList = new ListBaseNode(byteBuffer);
        fullFragmentList = new ListBaseNode(byteBuffer);
        nextUnusedSegmentId = byteBuffer.getLong();
        segmentInodesFullList = new ListBaseNode(byteBuffer);
        segmentInodesFreeList = new ListBaseNode(byteBuffer);
    }
}
