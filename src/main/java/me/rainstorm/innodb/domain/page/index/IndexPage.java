package me.rainstorm.innodb.domain.page.index;

import lombok.extern.slf4j.Slf4j;
import me.rainstorm.innodb.common.neo4j.Neo4jHelper;
import me.rainstorm.innodb.domain.page.LogicPage;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.index.record.AbstractRecord;
import me.rainstorm.innodb.domain.page.index.record.Infimum;
import me.rainstorm.innodb.domain.page.index.record.RecordHeader;
import me.rainstorm.innodb.domain.page.index.record.Supremum;
import me.rainstorm.innodb.domain.page.index.record.compact.CompactRecord;
import me.rainstorm.innodb.domain.page.index.record.compact.CompactRecordHeader;
import me.rainstorm.innodb.domain.page.inode.InodePage;
import me.rainstorm.innodb.domain.page.inode.SegmentEntry;
import me.rainstorm.innodb.domain.tablespace.SystemTableSpace;
import me.rainstorm.innodb.domain.tablespace.TableSpace;
import org.neo4j.driver.Result;

import java.util.List;

import static me.rainstorm.innodb.parser.ParserConstants.VERBOSE;
import static org.neo4j.driver.Values.parameters;

/**
 * 创建 Index 树的根节点
 * storage/innobase/btr/btr0btr.cc
 * btr_create
 * <p>
 * <p>
 * ibuf: storage/innobase/include/ibuf0ibuf.h
 *
 * @author traceless
 * @see SystemTableSpace#FSP_IBUF_HEADER_PAGE_NO
 * @see SystemTableSpace#FSP_IBUF_TREE_ROOT_PAGE_NO
 */
@Slf4j
public class IndexPage extends LogicPage<IndexPageBody> {

    public IndexPage(PhysicalPage physicalPage) {
        super(physicalPage);
    }

    @Override
    protected IndexPageBody createPageBody(PhysicalPage physicalPage) {
        return new IndexPageBody(physicalPage);
    }

    /**
     * storage/innobase/include/page0page.ic
     * <p>
     * page_is_leaf
     *
     * @return tree - Index 页为叶子页
     */
    public boolean isLeaf() {
        return body.getPageHeader().getPageLevel() == 0;
    }

    /**
     * storage/innobase/include/page0page.ic
     * <p>
     * page_is_root
     *
     * @return tree - Index 页为 Root 页
     */
    public boolean isRoot() {
        return fileHeader.getPrePageNo() == -1 &&
                fileHeader.getNextPageNo() == -1;
    }

    private String getId(AbstractRecord record) {
        return getId(record.getRecordContentOffset());
    }

    private String getId(int offset) {
        return String.format("%s_%s", getPageNo(), offset);
    }

    @Override
    public void addNodes(Neo4jHelper neo4jHelper) {
        super.addNodes(neo4jHelper);
        setNodePageProperty(neo4jHelper);
        addNodeRecord(neo4jHelper);
    }

    private void setNodePageProperty(Neo4jHelper neo4jHelper) {
        IndexPageHeader header = body.getPageHeader();
        neo4jHelper.execute(session -> session.writeTransaction(tx -> {
            Result result = tx.run("MERGE (p:Page {pID: toInteger($pageNo)})\n" +
                            "SET " +
                            "p.IndexType=$IndexType, " +
                            "p.SlotNum=$SlotNum, " +
                            "p.IndexId=$IndexId, " +
                            "p.MaxTransactionIdModifyThisPage=$MaxTransactionIdModifyThisPage, " +
                            "p.PageLevel=$PageLevel, " +
                            "p.TotalRecordNumber=$TotalRecordNumber, " +
                            "p.ValidRecordNumber=$ValidRecordNumber, " +
                            "p.PageDirection=$PageDirection, " +
                            "p.InsertRecordNumberInThisDirection=$InsertRecordNumberInThisDirection " +
                            " RETURN p;",
                    parameters("pageNo", getPageNo(),
                            "IndexType", isRoot() ? "root" : isLeaf() ? "leaf" : "non-leaf",
                            "SlotNum", header.getDirectorySlotNumber(),
                            "IndexId", header.getIndexId(),
                            "MaxTransactionIdModifyThisPage", header.getMaxTransactionIdModifyThisPage(),
                            "PageLevel", header.getPageLevel(),
                            "TotalRecordNumber", header.getTotalRecordNumber(),
                            "ValidRecordNumber", header.getValidRecordNumber(),
                            "PageDirection", header.getPageDirection().name(),
                            "InsertRecordNumberInThisDirection", header.getInsertRecordNumberInThisDirection()
                    ));

            if (VERBOSE && log.isDebugEnabled() && result.hasNext()) {
                log.debug("add index page property of {} done.", getPageNo());
            }
            return null;
        }));
    }


    private void addNodeRecord(Neo4jHelper neo4jHelper) {
        List<AbstractRecord> records = body.getRecords();
        records.forEach(record -> {
            String id = getId(record);
            RecordHeader recordHeader = record.getRecordHeader();
            neo4jHelper.execute(session -> session.writeTransaction(tx -> {
                Result result = tx.run("MERGE (p:Record {rID: $RecordID})\n" +
                                "SET " +
                                "p.OwnNumber=$OwnNumber, " +
                                "p.RecordType=$RecordType, " +
                                "p.HeapNo=$HeapNo" +
                                " RETURN p;",
                        parameters("RecordID", id,
                                "OwnNumber", recordHeader.getOwnNumber(),
                                "RecordType", record instanceof CompactRecord ?
                                        ((CompactRecordHeader) recordHeader).getRecordType().name() :
                                        record instanceof Infimum ? "R_Infimum" :
                                                record instanceof Supremum ? "R_Supremum" :
                                                        "R_Ordinary",
                                "HeapNo", recordHeader.getIndexOfAllRecordsInThisPage()
                        ));

                if (VERBOSE && log.isDebugEnabled() && result.hasNext()) {
                    log.debug("add Record node of {} done.", id);
                }
                return null;
            }));
        });
    }

    @Override
    public void linkNodes(Neo4jHelper neo4jHelper) {
        super.linkNodes(neo4jHelper);
        addLinkBetweenPageAndRecord(neo4jHelper);
        addLinkBetweenRecords(neo4jHelper);
        addLinkSegments(neo4jHelper);
    }

    private void addLinkBetweenPageAndRecord(Neo4jHelper neo4jHelper) {
        List<AbstractRecord> records = body.getRecords();
        records.forEach(record -> {
            String recordId = getId(record);
            neo4jHelper.execute(session -> session.writeTransaction(tx -> {
                Result result = tx.run("MATCH (cp:Page)\n" +
                                "WHERE cp.pID = toInteger($currentPage)\n" +
                                "MATCH (pp:Record)\n" +
                                "WHERE pp.rID = $recordId\n" +
                                "MERGE (cp)-[r:contain]->(pp)\n" +
                                "return r;",
                        parameters("currentPage", getPageNo(),
                                "recordId", recordId));

                if (VERBOSE && log.isDebugEnabled() && result.hasNext()) {
                    log.debug("add relation between page and Record ({})-[:previous]->({}) done.", getPageNo(), recordId);
                }

                return null;
            }));
        });
    }

    private void addLinkBetweenRecords(Neo4jHelper neo4jHelper) {
        List<AbstractRecord> records = body.getRecords();
        records.forEach(record -> {
            String recordId = getId(record);
            if (record.hasNext()) {
                neo4jHelper.execute(session -> session.writeTransaction(tx -> {
                    Result result = tx.run("MATCH (cp:Record)\n" +
                                    "WHERE cp.rID = $currentRecordId\n" +
                                    "MATCH (pp:Record)\n" +
                                    "WHERE pp.rID = $nextRecordId\n" +
                                    "MERGE (cp)-[r:next]->(pp)\n" +
                                    "return r;",
                            parameters("currentRecordId", recordId,
                                    "nextRecordId", getId(record.next())));

                    if (VERBOSE && log.isDebugEnabled() && result.hasNext()) {
                        log.debug("add relation between Records ({})-[:previous]->({}) done.", getPageNo(), recordId);
                    }
                    return null;
                }));
            }
        });
    }

    private void addLinkSegments(Neo4jHelper neo4jHelper) {
        if (isRoot()) {
            TableSpace tableSpace = extent().getTableSpace();
            IndexPageHeader indexPageHeader = body.getPageHeader();

            SegmentPointer leafSegmentPointer = indexPageHeader.getLeafSegmentPagePointer();
            assert tableSpace.tableSpaceId() == leafSegmentPointer.getSpaceId();
            SegmentEntry leafSegment = segmentEntry(tableSpace, leafSegmentPointer.getInodePageNo(), leafSegmentPointer.getOffset());
            doLinkSegment(leafSegment, "leafSegment", neo4jHelper);

            SegmentPointer nonLeafSegmentPointer = indexPageHeader.getNonLeafSegmentPagePointer();
            assert tableSpace.tableSpaceId() == nonLeafSegmentPointer.getSpaceId();
            SegmentEntry nonLeafSegment = segmentEntry(tableSpace, nonLeafSegmentPointer.getInodePageNo(), nonLeafSegmentPointer.getOffset());
            doLinkSegment(nonLeafSegment, "nonLeafSegment", neo4jHelper);
        }
    }

    private void doLinkSegment(SegmentEntry segment, String segmentType, Neo4jHelper neo4jHelper) {
        neo4jHelper.execute(session -> session.writeTransaction(tx -> {
            tx.run("MERGE (s:Segment {segID: toInteger($SegmentID)})\n" +
                            "SET s.SegmentType = $SegmentType\n" +
                            "return s;",
                    parameters("SegmentID", segment.getSegmentId(),
                            "SegmentType", segmentType));

            tx.run("MATCH (s:Segment)\n" +
                            "WHERE s.segID = toInteger($SegmentID)\n" +
                            "MATCH (p:Page)\n" +
                            "WHERE p.pID = toInteger($PageID)\n" +
                            "MERGE (p)-[r:" + segmentType + "]->(s)\n" +
                            "return r;",
                    parameters("SegmentID", segment.getSegmentId(),
                            "PageID", getPageNo()));
            return null;
        }));
    }

    private SegmentEntry segmentEntry(TableSpace tableSpace, int pageNo, short offset) {
        InodePage inodePage = tableSpace.page(pageNo);
        return inodePage.getSegmentEntry(offset);
    }
}
