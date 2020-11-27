package me.rainstorm.innodb.domain.page.xdes;

import lombok.Getter;
import me.rainstorm.innodb.domain.page.core.list.ListNode;

import java.nio.ByteBuffer;

import static me.rainstorm.innodb.domain.InnodbConstants.PAGE_NUM_IN_EXTENT;

/**
 * 代表一个区组里的一个区
 *
 * @author traceless
 */
@Getter
public class ExtentDescriptorEntry {
    private static final int BIT_PER_BYTE = 8;
    /**
     * How many bits are there per page
     */
    public static final int XDES_BITS_PER_PAGE = 2;
    /**
     * Index of the bit which tells if the page is free
     */
    public static final int XDES_FREE_BIT = 0;

    private final long segmentId;
    private final ListNode extentList;
    private final ExtentTypeEnum extentType;

    private static int pageStateBitMapLength() {
        return (XDES_BITS_PER_PAGE * PAGE_NUM_IN_EXTENT + BIT_PER_BYTE - 1) / BIT_PER_BYTE;
    }


    /**
     * 16 * 8 = 64 * 2
     * 每个 Page 两个 bit
     */
    private final byte[] pageStateBitMap = new byte[pageStateBitMapLength()];

    public ExtentDescriptorEntry(ByteBuffer buffer) {
        segmentId = buffer.getLong();
        extentList = new ListNode(buffer);
        int extentTypeCode = buffer.getInt();
        buffer.get(pageStateBitMap);

        if (extentTypeCode != 0) {
            extentType = ExtentTypeEnum.of(extentTypeCode);
        } else {
            extentType = null;
        }
    }

    public boolean isClean(int pageNo) {
        int pageOffset = pageNo % PAGE_NUM_IN_EXTENT;
        int bitOffset = XDES_FREE_BIT + pageOffset * XDES_BITS_PER_PAGE;

        int byteOffset = bitOffset / BIT_PER_BYTE;
        int offsetInByte = bitOffset % BIT_PER_BYTE;

        return ((pageStateBitMap[byteOffset] >> offsetInByte) & 1) > 0;
    }
}
