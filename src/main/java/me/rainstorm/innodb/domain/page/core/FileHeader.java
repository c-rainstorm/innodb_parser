package me.rainstorm.innodb.domain.page.core;

import lombok.Getter;
import me.rainstorm.innodb.domain.extend.Extend;
import me.rainstorm.innodb.domain.page.PageTypeEnum;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.tablespace.SystemTableSpace;
import me.rainstorm.innodb.domain.tablespace.TableSpace;

import java.nio.ByteBuffer;

/**
 * 数据页页头
 *
 * @author traceless
 */
@Getter
public class FileHeader {
    public static final int OFFSET = 0;
    public static final int LENGTH = 38;
    public static final int PAGE_TYPE_OFFSET = 24;

    private final TableSpace tableSpace;
    private final Extend extend;

    /**
     * 页面校验和
     */
    private final String checksum;

    /**
     * 当前页页号
     */
    private final int pageNo;
    /**
     * 上一页页号
     */
    private final int prePageNo;
    /**
     * 下一页页号
     */
    private final int nextPageNo;
    /**
     * 当前页最后被修改的LSN
     */
    private final long lastModifiedLogSequenceNumber;
    /**
     * 页类型
     */
    private final PageTypeEnum pageType;
    /**
     * 最后刷盘的LSN，仅系统表空间某个页有效
     */
    private final long lastFlushedLogSequenceNumber;

    /**
     * 表空间ID
     */
    private final int tableSpaceId;

    public FileHeader(PhysicalPage page) {
        this.extend = page.getExtend();
        this.tableSpace = extend.getTableSpace();

        ByteBuffer buffer = page.getData(OFFSET);

        this.checksum = Integer.toHexString(buffer.getInt()).toUpperCase();
        this.pageNo = buffer.getInt();
        this.prePageNo = buffer.getInt();
        this.nextPageNo = buffer.getInt();
        this.lastModifiedLogSequenceNumber = buffer.getLong();
        this.pageType = PageTypeEnum.of(buffer.getShort());
        this.lastFlushedLogSequenceNumber = buffer.getLong();
        this.tableSpaceId = buffer.getInt();
    }

    public long getLastFlushedLogSequenceNumber() {
        if (tableSpace instanceof SystemTableSpace) {
            return lastFlushedLogSequenceNumber;
        }
        return -1;
    }

    public static PageTypeEnum pageType(PhysicalPage physicalPage) {
        ByteBuffer buffer = physicalPage.getData(PAGE_TYPE_OFFSET);
        return PageTypeEnum.of(buffer.getShort());
    }
}
