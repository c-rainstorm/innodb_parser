package me.rainstorm.innodb.domain.tablespace;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.rainstorm.innodb.common.LRUCache;
import me.rainstorm.innodb.domain.InnodbConstants;
import me.rainstorm.innodb.domain.extent.Extent;
import me.rainstorm.innodb.domain.page.LogicPage;
import me.rainstorm.innodb.domain.page.core.PageBody;
import me.rainstorm.innodb.domain.page.fsp.FileSpaceHeaderPage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.Iterator;

import static me.rainstorm.innodb.common.i18n.I18nMsgCodeEnum.*;
import static me.rainstorm.innodb.common.i18n.I18nUtil.message;
import static me.rainstorm.innodb.domain.InnodbConstants.*;
import static me.rainstorm.innodb.domain.extent.Extent.pageOffsetOfExtent;
import static me.rainstorm.innodb.parser.ParserConstants.EXTEND_LRU_CACHE_SIZE;
import static me.rainstorm.innodb.parser.ParserConstants.VERBOSE;

/**
 * @author traceless
 */
@Slf4j
public abstract class TableSpace implements AutoCloseable {
    public static final int PAGE_ZERO = FileSpaceHeaderPage.PAGE_NO;

    private final FileChannel tableSpaceChannel;
    private final Path tableSpacePath;
    private final Path relativePath;
    /**
     * 每个 Extent 正常情况下 1M，LRU 非线程安全
     *
     * @see InnodbConstants#PAGE_NUM_IN_EXTEND
     */
    private final LRUCache<Integer, Extent> extendLRUCache;

    @Getter
    protected FileSpaceHeaderPage fileSpaceHeaderPage;

    public TableSpace(Path tableSpacePath, Path relativePath) {
        this.tableSpacePath = tableSpacePath;
        this.relativePath = relativePath;
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
        int pageOffsetInExtent = pageOffsetOfExtent(pageNo);
        if (VERBOSE && log.isDebugEnabled()) {
            log.debug(message(LogPageLocate, pageNo, extendOffset, pageOffsetInExtent));
        }

        Extent extent = extendLRUCache.computeIfAbsent(extendOffset, this::extend);
        return extent.page(pageOffsetInExtent);
    }

    public Extent extend(int extendOffset) {
        try {
            tableSpaceChannel.position(extendOffset * EXTEND_SIZE);

            ByteBuffer byteBuffer = ByteBuffer.allocate(EXTEND_SIZE);
            int bytesRead = tableSpaceChannel.read(byteBuffer);
            if (bytesRead < EXTEND_SIZE && log.isDebugEnabled()) {
                log.debug(message(LogPageNumInExtentLessThanExpected, tableSpacePath, extendOffset, PAGE_NUM_IN_EXTEND, bytesRead / PAGE_SIZE));
            }
            return new Extent(this, extendOffset, byteBuffer);
        } catch (IOException e) {
            System.err.println(message(LogLoadExtentFailure, tableSpacePath, extendOffset));
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

    public int totalExtentNumber() {
        return extendOffsetOfTableSpace(totalPageNumber()) + 1;
    }

    public Iterator<LogicPage<?>> sequentialTraversalIterator() {
        return new SequentialTraversalIterator(fileSpaceHeaderPage.totalPageNumber());
    }

    public String relativePath() {
        return relativePath.toString();
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
