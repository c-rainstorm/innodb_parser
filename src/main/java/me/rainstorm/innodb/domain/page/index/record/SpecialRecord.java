package me.rainstorm.innodb.domain.page.index.record;

import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.index.record.compact.CompactRecordHeader;
import me.rainstorm.innodb.domain.page.index.record.redundant.RedundantRecordHeader;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public abstract class SpecialRecord extends AbstractRecord {
    protected String desc;

    public SpecialRecord(PhysicalPage physicalPage, boolean newCompactFormat, int recordContentOffset) {
        super(physicalPage, newCompactFormat, recordContentOffset);

        ByteBuffer buffer = physicalPage.getData(recordContentOffset);
        byte[] bytes = new byte[8];
        buffer.get(bytes);
        desc = new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    protected RecordHeader loadRecordHeader(PhysicalPage physicalPage, int recordContentOffset) {
        return isNewCompactFormat() ? new CompactRecordHeader(physicalPage, loadRecordMetaDataOffset(recordContentOffset)) :
                new RedundantRecordHeader(physicalPage, loadRecordMetaDataOffset(recordContentOffset));
    }
}
