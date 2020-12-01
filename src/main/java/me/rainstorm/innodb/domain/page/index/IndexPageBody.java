package me.rainstorm.innodb.domain.page.index;

import lombok.Getter;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.core.FileTrailer;
import me.rainstorm.innodb.domain.page.core.PageBody;
import me.rainstorm.innodb.domain.page.index.record.Infimum;
import me.rainstorm.innodb.domain.page.index.record.Record;
import me.rainstorm.innodb.domain.page.index.record.RecordHeader;
import me.rainstorm.innodb.domain.page.index.record.Supremum;

import java.nio.ByteBuffer;
import java.util.*;

import static me.rainstorm.innodb.domain.InnodbConstants.PAGE_SIZE;

/**
 * @author traceless
 */
@Getter
public class IndexPageBody extends PageBody {
    public static final int OFFSET = IndexPageHeader.OFFSET + IndexPageHeader.LENGTH;

    private final IndexPageHeader pageHeader;

    private final Infimum infimum;

    private final Supremum supremum;

    private final List<Record> records = new LinkedList<>();

    private final List<Record> deletedRecords = new LinkedList<>();

    private final List<Record> pageDirectory;

    public IndexPageBody(PhysicalPage physicalPage) {
        pageHeader = new IndexPageHeader(physicalPage);

        Set<Short> pageDirectoryOffsets = new HashSet<>(pageHeader.getDirectorySlotNumber());
        ByteBuffer buffer = physicalPage.getData(PAGE_SIZE - FileTrailer.LENGTH - 2 * pageHeader.getDirectorySlotNumber());
        for (int i = 0; i < pageHeader.getDirectorySlotNumber(); i++) {
            pageDirectoryOffsets.add((short) (buffer.getShort() - RecordHeader.LENGTH));
        }

        pageDirectory = new ArrayList<>(pageHeader.getDirectorySlotNumber());

        infimum = new Infimum(physicalPage);
        supremum = new Supremum(physicalPage);

        Record r = infimum;
        while (r.hasNext()) {
            records.add(r);
            r = new Record(physicalPage, r.next());
        }
        records.add(supremum);

        if (hasDeletedRecord()) {
            r = new Record(physicalPage, pageHeader.getFirstRecordInDeletedList());
            deletedRecords.add(r);
            while (r.hasNext()) {
                Record record = new Record(physicalPage, r.next());
                deletedRecords.add(record);
                r = record;
            }
        }

        records.stream().filter(x -> pageDirectoryOffsets.contains(x.getOffset())).forEach(pageDirectory::add);
    }

    public boolean hasDeletedRecord() {
        return pageHeader.getFirstRecordInDeletedList() > 0;
    }
}
