package me.rainstorm.innodb.domain.page.sys.rbseg;

import lombok.Getter;
import me.rainstorm.innodb.domain.InnodbConstants;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.core.PageBody;
import me.rainstorm.innodb.domain.page.core.list.ListBaseNode;
import me.rainstorm.innodb.domain.segment.SegmentPointer;

import java.nio.ByteBuffer;

/**
 * DB_TOO_MANY_CONCURRENT_TRXS
 *
 * @author traceless
 */
@Getter
public class RollbackSegmentPageBody extends PageBody {
    /**
     * #define TRX_RSEG_N_SLOTS	(UNIV_PAGE_SIZE / 16)
     */
    public static final int SLOT_NUMBER = InnodbConstants.PAGE_SIZE / 16;

    /**
     * 每个回滚段支持的事务数
     * #define TRX_RSEG_MAX_N_TRXS	(TRX_RSEG_N_SLOTS / 2)
     */
    public static final int MAX_SUPPORT_TRANSACTION_PER_SEG = SLOT_NUMBER / 2;

    /**
     * 相关的 undo log page 总数
     */
    private final int maxRelativeUndoLogPageNumber;

    /**
     * committedTransactionUndoLogPageList 内的页数量
     */
    private final int historySize;

    /**
     * 已提交事务不能释放的 undo log page
     */
    private final ListBaseNode committedTransactionUndoLogPageList;

    /**
     * 段指针
     */
    private final SegmentPointer segmentPointer;

    /**
     * 各个Undo页面链表的 first undo page 的页号集合
     */
    private int[] undoLogPageSlots = new int[SLOT_NUMBER];

    public RollbackSegmentPageBody(PhysicalPage physicalPage) {
        ByteBuffer buffer = physicalPage.getData(OFFSET);

        maxRelativeUndoLogPageNumber = buffer.getInt();
        historySize = buffer.getInt();
        committedTransactionUndoLogPageList = new ListBaseNode(buffer);
        segmentPointer = new SegmentPointer(buffer);

        for (int i = 0; i < SLOT_NUMBER; i++) {
            undoLogPageSlots[i] = buffer.getInt();
        }
    }
}
