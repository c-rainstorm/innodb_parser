package me.rainstorm.innodb.domain.page.sys;

import lombok.Getter;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.core.FileHeader;
import me.rainstorm.innodb.domain.page.core.PageBody;
import me.rainstorm.innodb.domain.page.index.SegmentPointer;

import java.nio.ByteBuffer;

/**
 * @author traceless
 */
@Getter
public class DataDirectoryPageBody extends PageBody {
    public static final int OFFSET = FileHeader.OFFSET + FileHeader.LENGTH;
    public static final int LENGTH = DataDirectoryPageHeader.LENGTH + 4 + SegmentPointer.LENGTH;

    private final DataDirectoryPageHeader dataDirectoryPageHeader;

    private final SegmentPointer segmentPointer;

    public DataDirectoryPageBody(PhysicalPage physicalPage) {
        ByteBuffer buffer = physicalPage.getData(OFFSET);

        dataDirectoryPageHeader = new DataDirectoryPageHeader(buffer);
        buffer.getInt();
        segmentPointer = new SegmentPointer(buffer);
    }
}
