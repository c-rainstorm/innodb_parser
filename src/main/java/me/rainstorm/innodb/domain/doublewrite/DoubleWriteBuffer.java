package me.rainstorm.innodb.domain.doublewrite;

import lombok.Getter;
import me.rainstorm.innodb.domain.InnodbConstants;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.segment.SegmentPointer;

import java.nio.ByteBuffer;

import static me.rainstorm.innodb.domain.InnodbConstants.PAGE_SIZE;

/**
 * storage/innobase/buf/buf0dblwr.cc
 * buf_dblwr_create
 *
 * @author traceless
 */
@Getter
public class DoubleWriteBuffer {
    /**
     * Contents of TRX_SYS_DOUBLEWRITE_MAGIC
     * <p>
     * #define TRX_SYS_DOUBLEWRITE_MAGIC_N	536853855
     */
    public static final int TRX_SYS_DOUBLEWRITE_MAGIC_N = 536853855;

    /**
     * Size of the doublewrite block in pages
     * <p>
     * #define TRX_SYS_DOUBLEWRITE_BLOCK_SIZE	FSP_EXTENT_SIZE
     */
    public static final int TRX_SYS_DOUBLEWRITE_BLOCK_SIZE = InnodbConstants.EXTEND_SIZE;

    /**
     * The offset of the doublewrite buffer header on the trx system header page
     * <p>
     * #define TRX_SYS_DOUBLEWRITE		(UNIV_PAGE_SIZE - 200)
     */
    public static final int OFFSET_TRX_SYS_DOUBLE_WRITE = PAGE_SIZE - 200;

    /**
     * fseg header of the fseg containing the doublewrite buffer
     * <p>
     * #define TRX_SYS_DOUBLEWRITE_FSEG	0
     */
    public static final int OFFSET_TRX_SYS_DOUBLEWRITE_FSEG = OFFSET_TRX_SYS_DOUBLE_WRITE;

    /**
     * 4-byte magic number which shows if we already have created the doublewrite buffer
     * <p>
     * #define TRX_SYS_DOUBLEWRITE_MAGIC	FSEG_HEADER_SIZE
     */
    public static final int OFFSET_TRX_SYS_DOUBLEWRITE_MAGIC = OFFSET_TRX_SYS_DOUBLEWRITE_FSEG + SegmentPointer.LENGTH;

    /**
     * page number of the first page in the first sequence of
     * <p>
     * 64 (= FSP_EXTENT_SIZE) consecutive pages in the doublewrite buffer
     * <p>
     * #define TRX_SYS_DOUBLEWRITE_BLOCK1	(4 + FSEG_HEADER_SIZE)
     */
    public static final int OFFSET_TRX_SYS_DOUBLEWRITE_BLOCK1 = OFFSET_TRX_SYS_DOUBLEWRITE_MAGIC + 4;

    /**
     * #define TRX_SYS_DOUBLEWRITE_BLOCK2	(8 + FSEG_HEADER_SIZE)
     */
    public static final int OFFSET_TRX_SYS_DOUBLEWRITE_BLOCK2 = OFFSET_TRX_SYS_DOUBLEWRITE_BLOCK1 + 4;

    public DoubleWriteBuffer(PhysicalPage physicalPage) {
        ByteBuffer buffer = physicalPage.getData(OFFSET_TRX_SYS_DOUBLE_WRITE);

        segmentPointer = new SegmentPointer(buffer);
        magicNumber = buffer.getInt();
        firstPageNoInFirstBlock = buffer.getInt();
        firstPageNoInSecondBlock = buffer.getInt();
    }

    private final SegmentPointer segmentPointer;
    private final int magicNumber;
    private final int firstPageNoInFirstBlock;
    private final int firstPageNoInSecondBlock;
}
