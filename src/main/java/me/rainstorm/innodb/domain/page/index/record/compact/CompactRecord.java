package me.rainstorm.innodb.domain.page.index.record.compact;

import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.index.record.AbstractRecord;
import me.rainstorm.innodb.domain.page.index.record.RecordHeader;

/**
 * @author traceless
 */
public class CompactRecord extends AbstractRecord {

    public CompactRecord(PhysicalPage physicalPage, int recordContentOffset) {
        super(physicalPage, true, recordContentOffset);
    }

    @Override
    protected RecordHeader loadRecordHeader(PhysicalPage physicalPage, int recordContentOffset) {
        return new CompactRecordHeader(physicalPage, loadRecordMetaDataOffset(recordContentOffset));
    }
}
