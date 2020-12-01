package me.rainstorm.innodb.domain.page.index.record;

import java.util.Arrays;

/**
 * @author traceless
 */

public enum RecordTypeEnum {
    /**
     * REC_STATUS_ORDINARY	0
     * 数据页节点的普通记录
     */
    Ordinary(0),
    /**
     * REC_STATUS_NODE_PTR	1
     * 索引节点的索引记录
     */
    NodePointer(1),
    /**
     * REC_STATUS_INFIMUM	2
     * 页的 Infimum 记录
     */
    Infimum(2),
    /**
     * REC_STATUS_SUPREMUM	3
     * 页的 Supremum 记录
     */
    Supremum(3);

    private int code;

    RecordTypeEnum(int code) {
        this.code = code;
    }

    public static RecordTypeEnum of(short code) {
        return Arrays.stream(RecordTypeEnum.values()).filter(x -> x.code == code).findAny()
                .orElseThrow(() -> new RuntimeException("Record Type of " + code + " is undefined"));
    }
}
