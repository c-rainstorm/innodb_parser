package me.rainstorm.innodb.domain.page.xdes;

import lombok.Getter;

import java.util.Arrays;

/**
 * 页类型
 *
 * @author traceless
 */
@Getter
public enum ExtentTypeEnum {
    /**
     * 空闲的区
     * extent is in free list of space
     */
    SpaceFree(1, "XDES_FREE"),

    /**
     * 有剩余空间的碎片区
     * extent is in free fragment list of space
     */
    SpaceFreeFragment(2, "XDES_FREE_FRAG"),

    /**
     * 没有剩余空间的碎片区
     * extent is in full fragment list of space
     */
    SpaceFullFragment(3, "XDES_FULL_FRAG"),

    /**
     * 附属于某个段的区
     * extent belongs to a segment
     */
    Segment(4, "XDES_FSEG");

    ExtentTypeEnum(int code, String messageKey) {
        this.code = code;
        this.innodbConstantName = messageKey;
    }

    private final int code;
    private final String innodbConstantName;

    public static ExtentTypeEnum of(int code) {
        return Arrays.stream(ExtentTypeEnum.values()).filter(x -> x.code == code).findAny()
                .orElseThrow(() -> new RuntimeException("ExtentType of " + code + " is undefined"));
    }
}
