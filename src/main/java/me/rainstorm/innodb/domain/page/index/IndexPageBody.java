package me.rainstorm.innodb.domain.page.index;

import lombok.Getter;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.core.FileTrailer;
import me.rainstorm.innodb.domain.page.core.PageBody;
import me.rainstorm.innodb.domain.page.index.record.AbstractRecord;
import me.rainstorm.innodb.domain.page.index.record.Infimum;
import me.rainstorm.innodb.domain.page.index.record.Supremum;
import me.rainstorm.innodb.domain.page.index.record.compact.CompactRecord;
import me.rainstorm.innodb.domain.page.index.record.redundant.RedundantRecord;

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

    private final List<AbstractRecord> records = new LinkedList<>();

    private final List<AbstractRecord> deletedRecords = new LinkedList<>();

    private final List<AbstractRecord> pageDirectory;

    public IndexPageBody(PhysicalPage physicalPage) {
        pageHeader = new IndexPageHeader(physicalPage);

        Set<Integer> pageDirectoryOffsets = new HashSet<>(pageHeader.getDirectorySlotNumber());
        ByteBuffer buffer = physicalPage.getData(PAGE_SIZE - FileTrailer.LENGTH - 2 * pageHeader.getDirectorySlotNumber());
        for (int i = 0; i < pageHeader.getDirectorySlotNumber(); i++) {
            pageDirectoryOffsets.add((buffer.getShort() - AbstractRecord.headerLength(pageHeader.isNewCompactFormat())));
        }

        pageDirectory = new ArrayList<>(pageHeader.getDirectorySlotNumber());

        infimum = new Infimum(physicalPage, pageHeader.isNewCompactFormat());
        supremum = new Supremum(physicalPage, pageHeader.isNewCompactFormat());

        AbstractRecord r = infimum;
        while (r.hasNext()) {
            records.add(r);
            r = nextRecord(physicalPage, r.next());
        }
        records.add(supremum);

        if (hasDeletedRecord()) {
            r = nextRecord(physicalPage, pageHeader.getFirstRecordInDeletedList());
            deletedRecords.add(r);
            while (r.hasNext()) {
                AbstractRecord record = nextRecord(physicalPage, r.next());
                deletedRecords.add(record);
                r = record;
            }
        }

        records.stream().filter(x -> pageDirectoryOffsets.contains(x.getRecordContentOffset())).forEach(pageDirectory::add);
    }

    private AbstractRecord nextRecord(PhysicalPage physicalPage, int next) {
        return pageHeader.isNewCompactFormat() ? new CompactRecord(physicalPage, next) : new RedundantRecord(physicalPage, next);
    }

    public boolean hasDeletedRecord() {
        return pageHeader.getFirstRecordInDeletedList() > 0;
    }
}
