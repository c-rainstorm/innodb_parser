package me.rainstorm.innodb.domain.page.index.record.redundant;

import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.index.record.AbstractRecord;
import me.rainstorm.innodb.domain.page.index.record.RecordHeader;

/**
 * @author traceless
 */
public class RedundantRecord extends AbstractRecord {
    public RedundantRecord(PhysicalPage physicalPage, int recordContentOffset) {
        super(physicalPage, false, recordContentOffset);
    }

    @Override
    protected RecordHeader loadRecordHeader(PhysicalPage physicalPage, int recordContentOffset) {
        return new RedundantRecordHeader(physicalPage, loadRecordMetaDataOffset(recordContentOffset));
    }

    @Override
    public int next() {
        return recordHeader.getNextRecordOffset();
    }
}
