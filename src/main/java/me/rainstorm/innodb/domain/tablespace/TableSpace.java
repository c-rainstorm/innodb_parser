package me.rainstorm.innodb.domain.tablespace;

import lombok.extern.slf4j.Slf4j;
import me.rainstorm.innodb.common.LRUCache;
import me.rainstorm.innodb.domain.InnodbConstants;
import me.rainstorm.innodb.domain.extend.Extend;
import me.rainstorm.innodb.domain.page.LogicPage;
import me.rainstorm.innodb.domain.page.core.PageBody;
import me.rainstorm.innodb.domain.page.xdes.fsp.FileSpaceHeaderPage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.Iterator;

import static me.rainstorm.innodb.domain.InnodbConstants.*;
import static me.rainstorm.innodb.domain.extend.Extend.pageOffsetOfExtend;
import static me.rainstorm.innodb.parser.ParserConstants.EXTEND_LRU_CACHE_SIZE;
import static me.rainstorm.innodb.parser.ParserConstants.verbose;

/**
 * @author traceless
 */
@Slf4j
public abstract class TableSpace implements AutoCloseable {
    public static final int PAGE_ZERO = FileSpaceHeaderPage.PAGE_NO;

    private final FileChannel tableSpaceChannel;
    private final Path tableSpacePath;
    /**
     * 每个 Extend 正常情况下 1M，LRU 非线程安全
     *
     * @see InnodbConstants#PAGE_NUM_IN_EXTEND
     */
    private final LRUCache<Integer, Extend> extendLRUCache;

    protected FileSpaceHeaderPage fileSpaceHeaderPage;

    public TableSpace(Path tableSpacePath) {
        this.tableSpacePath = tableSpacePath;
        extendLRUCache = new LRUCache<>(EXTEND_LRU_CACHE_SIZE);
        RandomAccessFile reader;
        try {
            reader = new RandomAccessFile(tableSpacePath.toFile(), "rw");
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            tableSpaceChannel = null;
            System.exit(-1);
            return;
        }
        tableSpaceChannel = reader.getChannel();
        fileSpaceHeaderPage = page(PAGE_ZERO);
    }

    @Override
    public void close() throws Exception {
        if (tableSpaceChannel != null && tableSpaceChannel.isOpen()) {
            tableSpaceChannel.close();
        }
    }

    public <Page extends LogicPage<? extends PageBody>> Page page(int pageNo) {
        int extendOffset = extendOffsetOfTableSpace(pageNo);
        int pageOffsetInExtend = pageOffsetOfExtend(pageNo);
        if (verbose && log.isDebugEnabled()) {
            log.info("pageNo {} 区号 {}，区内偏移量 {}", pageNo, extendOffset, pageOffsetInExtend);
        }

        Extend extend = extendLRUCache.computeIfAbsent(extendOffset, this::extend);
        return extend.page(pageOffsetInExtend);
    }

    public Extend extend(int extendOffset) {
        try {
            tableSpaceChannel.position(extendOffset * EXTEND_SIZE);

            ByteBuffer byteBuffer = ByteBuffer.allocate(EXTEND_SIZE);
            int bytesRead = tableSpaceChannel.read(byteBuffer);
            if (bytesRead < EXTEND_SIZE && log.isDebugEnabled()) {
                log.warn("加载 TableSpace {} 的 {} 号 Extend 读取数据不足，期待读取 {} 页, 实际读取 {} 页", tableSpacePath, extendOffset, PAGE_NUM_IN_EXTEND, bytesRead / PAGE_SIZE);
            }
            return new Extend(this, extendOffset, byteBuffer);
        } catch (IOException e) {
            System.err.printf("加载 TableSpace %s 的 %s 号 Extend 失败\n", tableSpacePath, extendOffset);
            System.exit(-1);
            return null;
        }
    }

    public int tableSpaceId() {
        return fileSpaceHeaderPage.getFileHeader().getTableSpaceId();
    }

    public String tableSpaceName() {
        return tableSpacePath.toString();
    }

    @Override
    public String toString() {
        return "[spaceId: " + tableSpaceId() + "][spacePath: " + tableSpaceName() + "]";
    }

    public static int extendOffsetOfTableSpace(int pageNo) {
        return pageNo / PAGE_NUM_IN_EXTEND;
    }

    public int totalPageNumber() {
        return fileSpaceHeaderPage.totalPageNumber();
    }

    public int totalExtendNumber() {
        return extendOffsetOfTableSpace(totalPageNumber()) + 1;
    }

    public Iterator<LogicPage<?>> sequentialTraversalIterator() {
        return new SequentialTraversalIterator(fileSpaceHeaderPage.totalPageNumber());
    }

    class SequentialTraversalIterator implements Iterator<LogicPage<?>> {
        private int currentPage;
        private final int totalPage;

        public SequentialTraversalIterator(int totalPage) {
            this.currentPage = -1;
            this.totalPage = totalPage;
        }

        @Override
        public boolean hasNext() {
            return currentPage < totalPage - 1;
        }

        @Override
        public LogicPage<?> next() {
            return page(++currentPage);
        }
    }
}
