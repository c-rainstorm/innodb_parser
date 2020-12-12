package me.rainstorm.innodb.domain.page.index;

import lombok.Getter;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.core.FileHeader;
import me.rainstorm.innodb.domain.page.index.record.compact.CompactRecordHeader;
import me.rainstorm.innodb.domain.page.index.record.redundant.RedundantRecordHeader;
import me.rainstorm.innodb.domain.segment.SegmentPointer;

import java.nio.ByteBuffer;

/**
 * @author traceless
 */
@Getter
public class IndexPageHeader {
    public static final int OFFSET = FileHeader.OFFSET + FileHeader.LENGTH;
    public static final int LENGTH = 56;

    /**
     * 在页目录中的槽数量
     * PAGE_N_DIR_SLOTS
     * number of slots in page directory
     */
    private final short directorySlotNumber;


    /**
     * PAGE_HEAP_TOP	2字节	还未使用的空间最小地址，也就是说从该地址之后就是Free Space
     * pointer to start of page free record list
     */
    private final short unusedSpaceOffset;

    /**
     * 新的 Compact 格式
     */
    private final boolean newCompactFormat;

    /**
     * PAGE_N_HEAP	2字节
     * 本页中的记录的数量（包括最小和最大记录以及标记为删除的记录）
     * number of records in the heap, bit 15=flag: new-style compact page format
     */
    private final short totalRecordNumber;

    /**
     * PAGE_FREE
     * 第一个已经标记为删除的记录地址（各个已删除的记录通过next_record也会组成一个单链表，这个单链表中的记录可以被重新利用）
     */
    private final short firstRecordInDeletedList;

    /**
     * PAGE_GARBAGE
     * number of bytes in deleted records
     * 已删除记录占用的字节数
     */
    private final short totalBytesAllDeletedRecords;

    /**
     * PAGE_LAST_INSERT
     * 最后插入记录的位置
     */
    private final short lastInsertPosition;

    /**
     * PAGE_DIRECTION
     * 记录插入的方向
     * last insert direction: PAGE_LEFT...
     */
    private final PageDirectionEnum pageDirection;
    /**
     * PAGE_N_DIRECTION
     * 一个方向连续插入的记录数量
     */
    private final short insertRecordNumberInThisDirection;
    /**
     * PAGE_N_RECS
     * 该页中记录的数量（不包括最小和最大记录以及被标记为删除的记录）
     */
    private final short validRecordNumber;

    /**
     * PAGE_MAX_TRX_ID
     * 修改当前页的最大事务ID，该值仅在二级索引中定义
     */
    private final long maxTransactionIdModifyThisPage;

    /**
     * PAGE_LEVEL
     * 当前页在B+树中所处的层级
     */
    private final short pageLevel;

    /**
     * PAGE_INDEX_ID
     * 索引ID，表示当前页属于哪个索引
     */
    public final long indexId;

    /**
     * PAGE_BTR_SEG_LEAF
     * B+树叶子段的头部信息，仅在B+树的Root页定义
     */
    public final SegmentPointer leafSegmentPagePointer;
    /**
     * PAGE_BTR_SEG_TOP
     * B+树非叶子段的头部信息，仅在B+树的Root页定义
     */
    public final SegmentPointer nonLeafSegmentPagePointer;

    public IndexPageHeader(PhysicalPage physicalPage) {
        ByteBuffer buffer = physicalPage.getData(OFFSET);

        directorySlotNumber = buffer.getShort();
        unusedSpaceOffset = buffer.getShort();
        short s = buffer.getShort();
        totalRecordNumber = (short) (s & 0x7FFF);
        newCompactFormat = (s & 0x8000) != 0;
        firstRecordInDeletedList = buffer.getShort();
        totalBytesAllDeletedRecords = buffer.getShort();
        lastInsertPosition = buffer.getShort();
        pageDirection = PageDirectionEnum.of(buffer.getShort());
        insertRecordNumberInThisDirection = buffer.getShort();
        validRecordNumber = buffer.getShort();
        maxTransactionIdModifyThisPage = buffer.getLong();
        pageLevel = buffer.getShort();
        indexId = buffer.getLong();
        leafSegmentPagePointer = new SegmentPointer(buffer);
        nonLeafSegmentPagePointer = new SegmentPointer(buffer);
    }
}
