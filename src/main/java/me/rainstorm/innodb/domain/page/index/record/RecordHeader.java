package me.rainstorm.innodb.domain.page.index.record;

import lombok.Getter;
import me.rainstorm.innodb.domain.page.PhysicalPage;

import java.nio.ByteBuffer;

/**
 * @author traceless
 */
@Getter
public class RecordHeader {
    public static final int LENGTH = 5;

    private static final byte REC_INFO_MIN_REC_FLAG = 0x10;
    private static final byte REC_INFO_DELETED_FLAG = 0x20;
    private static final byte REC_INFO_OWNED_MASK = 0xF;

    private static final int REC_HEAP_NO_MASK = 0xFFF8;
    /**
     * 1 bit
     * <p>
     * 删除标志位
     */
    public final boolean deleted;

    /**
     * 1 bit
     * <p>
     * B+树的每层非叶子节点中的最小记录为 true
     */
    public final boolean minRecord;

    /**
     * 4 bit
     * <p>
     * 该记录所在槽的最大那条记录才会有值，代表本槽里一共多少个记录
     */
    public final byte ownNumber;

    /**
     * 13bit
     * <p>
     * 表示当前记录在记录堆的位置信息
     */
    private final short indexOfAllRecordsInThisPage;

    /**
     * 3bit
     * <p>
     * 表示当前记录的类型，0表示普通记录，1表示B+树非叶节点记录，2表示最小记录，3表示最大记录
     */
    private final RecordTypeEnum recordType;

    /**
     * 16 bit
     * <p>
     * 表示下一条记录的相对位置
     */
    private final short nextRecordOffset;

    public RecordHeader(PhysicalPage physicalPage, int absoluteRecordOffset) {
        ByteBuffer buffer = physicalPage.getData(absoluteRecordOffset);

        byte b = buffer.get();
        deleted = (REC_INFO_DELETED_FLAG & b) != 0;
        minRecord = (REC_INFO_MIN_REC_FLAG & b) != 0;
        ownNumber = (byte) (REC_INFO_OWNED_MASK & b);
        short s = buffer.getShort();
        indexOfAllRecordsInThisPage = (short) ((s & REC_HEAP_NO_MASK) >> 3);
        recordType = RecordTypeEnum.of((short) (s & ~REC_HEAP_NO_MASK));
        nextRecordOffset = buffer.getShort();
    }
}
