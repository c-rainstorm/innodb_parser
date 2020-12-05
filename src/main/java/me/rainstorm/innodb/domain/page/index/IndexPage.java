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
import me.rainstorm.innodb.domain.tablespace.SystemTableSpace;
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
        if (isRoot() && SystemTableSpace.FSP_IBUF_TREE_ROOT_PAGE_NO != getFileHeader().getPageNo()) {
            IndexPageHeader indexPageHeader = body.getPageHeader();
            doLinkSegment(indexPageHeader.getLeafSegmentPagePointer(), "leafSegment", neo4jHelper);
            doLinkSegment(indexPageHeader.getNonLeafSegmentPagePointer(), "nonLeafSegment", neo4jHelper);
        }
    }
}
