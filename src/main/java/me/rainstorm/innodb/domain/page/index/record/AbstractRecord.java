package me.rainstorm.innodb.domain.page.index.record;

import lombok.Getter;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.index.record.compact.CompactRecord;
import me.rainstorm.innodb.domain.page.index.record.compact.CompactRecordHeader;
import me.rainstorm.innodb.domain.page.index.record.redundant.RedundantRecord;
import me.rainstorm.innodb.domain.page.index.record.redundant.RedundantRecordHeader;

/**
 * trx_undo_decode_roll_ptr
 * <p>
 * offset = (ulint) roll_ptr & 0xFFFF; roll_ptr >>= 16;
 * <p>
 * page_no = (ulint) roll_ptr & 0xFFFFFFFF; roll_ptr >>= 32;
 * <p>
 * rseg_id = (ulint) roll_ptr & 0x7F; roll_ptr >>= 7;
 * <p>
 * is_insert = (ibool) roll_ptr;
 *
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

    public AbstractRecord nextRecord(PhysicalPage physicalPage) {
        return newCompactFormat ? new CompactRecord(physicalPage, next()) :
                new RedundantRecord(physicalPage, next());
    }
}
