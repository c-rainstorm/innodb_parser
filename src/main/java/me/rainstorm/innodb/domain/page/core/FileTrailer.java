package me.rainstorm.innodb.domain.page.core;

import lombok.Getter;
import me.rainstorm.innodb.domain.page.PhysicalPage;

import java.nio.ByteBuffer;

import static me.rainstorm.innodb.domain.InnodbConstants.PAGE_SIZE;

/**
 * @author traceless
 */
@Getter
public class FileTrailer {
    public static final int LENGTH = 8;
    public static final int OFFSET = PAGE_SIZE - LENGTH;
    /**
     * 校验和
     */
    private final String checksum;
    /**
     * 最后修改页面LSN的后 4B
     *
     * @see FileHeader#getLastModifiedLogSequenceNumber()
     */
    private final long lastModifiedLogSequenceNumber;

    public FileTrailer(PhysicalPage page) {
        ByteBuffer buffer = page.getData(OFFSET);

        this.checksum = Integer.toHexString(buffer.getInt());
        this.lastModifiedLogSequenceNumber = Integer.toUnsignedLong(buffer.getInt());
    }
}
