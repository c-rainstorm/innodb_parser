package me.rainstorm.innodb.domain.page.trxsys;

import lombok.Getter;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.core.PageBody;
import me.rainstorm.innodb.domain.segment.SegmentPointer;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author traceless
 */
@Getter
public class TrxSysHeader {
    /**
     * # define	TRX_SYS_N_RSEGS			128
     */
    public static final int ROLLBACK_SEGMENT_SLOT_N = 128;

    public static final int OFFSET = PageBody.OFFSET;
    public static final int LENGTH = 8 + SegmentPointer.LENGTH + 8 * ROLLBACK_SEGMENT_SLOT_N;


    private final long maxTransactionId;

    /**
     * 事务系统页所属的段
     */
    private final SegmentPointer segmentBelongTo;

    private final long[] rollbackSegmentSlots = new long[ROLLBACK_SEGMENT_SLOT_N];

    public TrxSysHeader(PhysicalPage physicalPage) {
        ByteBuffer buffer = physicalPage.getData(OFFSET);

        maxTransactionId = buffer.getLong();
        segmentBelongTo = new SegmentPointer(buffer);
        for (int i = 0; i < ROLLBACK_SEGMENT_SLOT_N; i++) {
            rollbackSegmentSlots[i] = buffer.getLong();
        }
    }

    public Stream<Long> rollbackSegmentSlots() {
        return Arrays.stream(rollbackSegmentSlots).boxed().filter(x -> x != -1);
    }
}
