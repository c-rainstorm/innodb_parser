package me.rainstorm.innodb.domain.tablespace;

import me.rainstorm.innodb.domain.page.LogicPage;
import me.rainstorm.innodb.domain.page.sys.DataDirectoryHeaderPage;
import me.rainstorm.innodb.domain.page.sys.DataDirectoryPageHeader;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author traceless
 */
public class SystemTableSpace extends TableSpace {
    /**
     * insert buffer header page, in tablespace 0
     * <p>
     * Allocate first the ibuf header page
     * storage/innobase/btr/btr0btr.cc
     * btr_create
     */
    public static final int FSP_IBUF_HEADER_PAGE_NO = 3;

    /**
     * insert buffer B-tree root page in tablespace 0
     * storage/innobase/btr/btr0btr.cc
     * btr_create
     */
    public static final int FSP_IBUF_TREE_ROOT_PAGE_NO = 4;

    /**
     * transaction system header, in tablespace 0
     */
    public static final int FSP_TRX_SYS_PAGE_NO = 5;

    /**
     * first rollback segment page, in tablespace 0
     */
    public static final int FSP_FIRST_RSEG_PAGE_NO = 6;

    /**
     * data dictionary header page, in tablespace 0
     */
    public static final int FSP_DICT_HDR_PAGE_NO = 7;

    Set<Integer> allPageNeedProcess = new TreeSet<>();

    public SystemTableSpace(Path tableSpacePath) {
        super(tableSpacePath, tableSpacePath);

        for (int i = 0; i <= FSP_DICT_HDR_PAGE_NO; ++i) {
            allPageNeedProcess.add(i);
        }

        DataDirectoryHeaderPage dataDirectoryHeaderPage = page(FSP_DICT_HDR_PAGE_NO);
        DataDirectoryPageHeader dataDirectoryPageHeader = dataDirectoryHeaderPage.getBody().getDataDirectoryPageHeader();
        allPageNeedProcess.add(dataDirectoryPageHeader.getSysTablesPrimaryIndexRootPage());
        allPageNeedProcess.add(dataDirectoryPageHeader.getSysTablesSecondaryIndexForIdRootPage());
        allPageNeedProcess.add(dataDirectoryPageHeader.getSysIndexesPrimaryIndexRootPage());
        allPageNeedProcess.add(dataDirectoryPageHeader.getSysFieldsPrimaryIndexRootPage());
        allPageNeedProcess.add(dataDirectoryPageHeader.getSysColumnsPrimaryIndexRootPage());

        allPageNeedProcess.remove(FSP_IBUF_TREE_ROOT_PAGE_NO);
    }


    @Override
    public Iterator<LogicPage<?>> sequentialTraversalIterator() {
        return new SystemSequentialTraversalIterator(allPageNeedProcess.iterator());
    }

    class SystemSequentialTraversalIterator implements Iterator<LogicPage<?>> {
        Iterator<Integer> iterator;

        public SystemSequentialTraversalIterator(Iterator<Integer> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public LogicPage<?> next() {
            return page(iterator.next());
        }
    }
}
