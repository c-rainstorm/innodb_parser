package me.rainstorm.innodb.domain.page.xdes.fsp;

import lombok.Getter;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.core.FileHeader;

import java.nio.ByteBuffer;

/**
 * @author traceless
 */
@Getter
class FileSpaceHeader {
    public static final int OFFSET = FileHeader.OFFSET + FileHeader.LENGTH;
    public static final int LENGTH = 112;

    /**
     * 表空间ID
     */
    private final int spaceId;
    /**
     * 表空间页面总数量
     */
    private final int totalPageNumber;

    public FileSpaceHeader(PhysicalPage physicalPage) {
        ByteBuffer byteBuffer = physicalPage.getData(OFFSET);
        spaceId = byteBuffer.getInt();
        // 4 字节未使用
        byteBuffer.getInt();
        totalPageNumber = byteBuffer.getInt();
        //todo
    }
}
