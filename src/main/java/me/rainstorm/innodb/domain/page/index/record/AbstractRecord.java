package me.rainstorm.innodb.domain.page.index.record;

import lombok.Getter;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.index.record.compact.CompactRecordHeader;
import me.rainstorm.innodb.domain.page.index.record.redundant.RedundantRecordHeader;

/**
 * @author traceless
 */
@Getter
public abstract class AbstractRecord {
    protected final boolean newCompactFormat;
    protected final int recordContentOffset;
    protected final RecordHeader recordHeader;

    public AbstractRecord(PhysicalPage physicalPage, boolean newCompactFormat, int recordContentOffset) {
        this.newCompactFormat = newCompactFormat;
        recordHeader = loadRecordHeader(physicalPage, recordContentOffset);
        this.recordContentOffset = recordContentOffset;
    }

    protected abstract RecordHeader loadRecordHeader(PhysicalPage physicalPage, int recordContentOffset);

    protected int loadRecordMetaDataOffset(int recordContentOffset) {
        return recordContentOffset - headerLength(newCompactFormat);
    }

    public boolean hasNext() {
        return recordHeader.getNextRecordOffset() != 0;
    }

    public int next() {
        int offset = recordHeader.getNextRecordOffset();
        return newCompactFormat ? offset + recordContentOffset : offset;
    }

    public static int headerLength(boolean newCompactFormat) {
        return newCompactFormat ? CompactRecordHeader.LENGTH : RedundantRecordHeader.LENGTH;
    }
}
