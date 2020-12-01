package me.rainstorm.innodb.domain.page.index.record;

import lombok.Getter;
import me.rainstorm.innodb.domain.page.PhysicalPage;

/**
 * @author traceless
 */
@Getter
public class Record {
    private final short offset;
    private final RecordHeader recordHeader;

    public Record(PhysicalPage physicalPage, short absoluteRecordOffset) {
        recordHeader = new RecordHeader(physicalPage, absoluteRecordOffset);
        offset = absoluteRecordOffset;
    }

    public boolean hasNext() {
        return recordHeader.getNextRecordOffset() != 0;
    }

    public short next() {
        return (short) (recordHeader.getNextRecordOffset() + offset);
    }
}
