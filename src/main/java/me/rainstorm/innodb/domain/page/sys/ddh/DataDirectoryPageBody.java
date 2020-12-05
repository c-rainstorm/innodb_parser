package me.rainstorm.innodb.domain.page.sys.ddh;

import lombok.Getter;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.core.FileHeader;
import me.rainstorm.innodb.domain.page.core.PageBody;
import me.rainstorm.innodb.domain.segment.SegmentPointer;

import java.nio.ByteBuffer;

/**
 * @author traceless
 */
@Getter
public class DataDirectoryPageBody extends PageBody {
    public static final int OFFSET = FileHeader.OFFSET + FileHeader.LENGTH;

    private final long maxRowId;
    private final long maxTableId;
    private final long maxIndexId;
    private final int maxSpaceId;

    private final int sysTablesPrimaryIndexRootPage;
    private final int sysTablesSecondaryIndexForIdRootPage;
    private final int sysColumnsPrimaryIndexRootPage;
    private final int sysIndexesPrimaryIndexRootPage;
    private final int sysFieldsPrimaryIndexRootPage;

    private final SegmentPointer segmentPointer;

    public DataDirectoryPageBody(PhysicalPage physicalPage) {
        ByteBuffer buffer = physicalPage.getData(OFFSET);

        maxRowId = buffer.getLong();
        maxTableId = buffer.getLong();
        maxIndexId = buffer.getLong();
        maxSpaceId = buffer.getInt();
        buffer.getInt();
        sysTablesPrimaryIndexRootPage = buffer.getInt();
        sysTablesSecondaryIndexForIdRootPage = buffer.getInt();
        sysColumnsPrimaryIndexRootPage = buffer.getInt();
        sysIndexesPrimaryIndexRootPage = buffer.getInt();
        sysFieldsPrimaryIndexRootPage = buffer.getInt();
        buffer.getInt();
        segmentPointer = new SegmentPointer(buffer);
    }
}
