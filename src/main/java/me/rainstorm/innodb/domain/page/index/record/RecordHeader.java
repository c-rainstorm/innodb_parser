package me.rainstorm.innodb.domain.page.index.record;

import lombok.Getter;

/**
 * @author traceless
 */
@Getter
public class RecordHeader {
    protected static final byte REC_INFO_MIN_REC_FLAG = 0x10;
    protected static final byte REC_INFO_DELETED_FLAG = 0x20;
    protected static final byte REC_INFO_OWNED_MASK = 0xF;

    protected static final int REC_HEAP_NO_MASK = 0xFFF8;


    /**
     * 1 bit
     * <p>
     * 删除标志位
     */
    protected boolean deleted;

    /**
     * 1 bit
     * <p>
     * B+树的每层非叶子节点中的最小记录为 true
     */
    protected boolean minRecord;

    /**
     * 4 bit
     * <p>
     * 该记录所在槽的最大那条记录才会有值，代表本槽里一共多少个记录
     */
    protected byte ownNumber;

    /**
     * 13bit
     * <p>
     * 表示当前记录在记录堆的位置信息
     */
    protected short indexOfAllRecordsInThisPage;

    /**
     * 16 bit
     * <p>
     * 表示下一条记录的相对位置
     */
    protected short nextRecordOffset;


}
