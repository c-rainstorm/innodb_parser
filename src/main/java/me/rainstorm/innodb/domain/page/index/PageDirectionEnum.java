package me.rainstorm.innodb.domain.page.index;

import java.util.Arrays;

/**
 * #define	PAGE_LEFT		1
 * #define	PAGE_RIGHT		2
 * #define	PAGE_SAME_REC		3
 * #define	PAGE_SAME_PAGE		4
 * #define	PAGE_NO_DIRECTION	5
 *
 * @author traceless
 */
public enum PageDirectionEnum {
    /**
     * 未定义
     */
    Undefine(-1),
    /**
     * 左插入
     */
    PAGE_LEFT(1),
    /**
     * 右插入
     */
    PAGE_RIGHT(2),
    /**
     * todo
     */
    PAGE_SAME_REC(3),
    /**
     * todo
     */
    PAGE_SAME_PAGE(4),
    /**
     * todo
     */
    PAGE_NO_DIRECTION(5);

    PageDirectionEnum(int code) {
        this.code = (short) code;
    }

    private final short code;

    public static PageDirectionEnum of(short code) {
        return Arrays.stream(PageDirectionEnum.values()).filter(x -> x.code == code).findAny()
                .orElseThrow(() -> new RuntimeException("Page Direction of " + code + " is undefined"));
    }
}
