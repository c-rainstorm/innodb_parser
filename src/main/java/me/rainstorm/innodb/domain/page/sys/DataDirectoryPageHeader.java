package me.rainstorm.innodb.domain.page.sys;

import lombok.Getter;
import me.rainstorm.innodb.domain.page.core.FileHeader;

import java.nio.ByteBuffer;

/**
 * @author traceless
 */
@Getter
public class DataDirectoryPageHeader {
    public static final int OFFSET = FileHeader.OFFSET + FileHeader.LENGTH;
    public static final int LENGTH = 52;

    private final long maxRowId;
    private final long maxTableId;
    private final long maxIndexId;
    private final int maxSpaceId;

    private final int sysTablesPrimaryIndexRootPage;
    private final int sysTablesSecondaryIndexForIdRootPage;
    private final int sysColumnsPrimaryIndexRootPage;
    private final int sysIndexesPrimaryIndexRootPage;
    private final int sysFieldsPrimaryIndexRootPage;

    public DataDirectoryPageHeader(ByteBuffer buffer) {
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
    }
}
