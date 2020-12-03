package me.rainstorm.innodb.domain.page.index.record.redundant;

import lombok.Getter;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.index.record.RecordHeader;

import java.nio.ByteBuffer;

/**
 * @author traceless
 */
@Getter
public class RedundantRecordHeader extends RecordHeader {
    public static final int LENGTH = 6;

    public RedundantRecordHeader(PhysicalPage physicalPage, int absoluteRecordOffset) {
        ByteBuffer buffer = physicalPage.getData(absoluteRecordOffset);

        byte b = buffer.get();
        deleted = (REC_INFO_DELETED_FLAG & b) != 0;
        minRecord = (REC_INFO_MIN_REC_FLAG & b) != 0;
        ownNumber = (byte) (REC_INFO_OWNED_MASK & b);
        short s = buffer.getShort();
        indexOfAllRecordsInThisPage = (short) ((s & REC_HEAP_NO_MASK) >> 3);
        buffer.get();
        nextRecordOffset = buffer.getShort();
    }
}
