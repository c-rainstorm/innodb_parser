package me.rainstorm.innodb.domain.page.index.record.compact;

import lombok.Getter;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.index.record.RecordHeader;
import me.rainstorm.innodb.domain.page.index.record.RecordTypeEnum;

import java.nio.ByteBuffer;

/**
 * @author traceless
 */
@Getter
public class CompactRecordHeader extends RecordHeader {
    public static final int LENGTH = 5;

    /**
     * 3bit
     * <p>
     * 表示当前记录的类型，0表示普通记录，1表示B+树非叶节点记录，2表示最小记录，3表示最大记录
     */
    private final RecordTypeEnum recordType;

    public CompactRecordHeader(PhysicalPage physicalPage, int absoluteRecordOffset) {
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
