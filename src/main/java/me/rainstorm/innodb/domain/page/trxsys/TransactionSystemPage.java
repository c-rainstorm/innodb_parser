package me.rainstorm.innodb.domain.page.trxsys;

import lombok.extern.slf4j.Slf4j;
import me.rainstorm.innodb.common.neo4j.Neo4jHelper;
import me.rainstorm.innodb.domain.InnodbConstants;
import me.rainstorm.innodb.domain.doublewrite.DoubleWriteBuffer;
import me.rainstorm.innodb.domain.page.LogicPage;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.segment.SegmentEntry;
import me.rainstorm.innodb.domain.tablespace.SystemTableSpace;
import org.neo4j.driver.Result;

import static me.rainstorm.innodb.domain.page.sys.rbseg.RollbackSegmentPageBody.MAX_SUPPORT_TRANSACTION_PER_SEG;
import static me.rainstorm.innodb.parser.ParserConstants.VERBOSE;
import static org.neo4j.driver.Values.parameters;

/**
 * storage/innobase/include/trx0sys.h
 * storage/innobase/trx/trx0sys.cc
 * trx_sysf_create
 * <p>
 * 最大支持的并发事务数
 *
 * @author traceless
 * @see SystemTableSpace#FSP_TRX_SYS_PAGE_NO
 */
@Slf4j
public class TransactionSystemPage extends LogicPage<TransactionSystemPageBody> {
    public static final int MAX_SUPPORT_TRANSACTION_GLOBAL = MAX_SUPPORT_TRANSACTION_PER_SEG * 96;

    public TransactionSystemPage(PhysicalPage physicalPage) {
        super(physicalPage);
    }

    @Override
    protected TransactionSystemPageBody createPageBody(PhysicalPage physicalPage) {
        return new TransactionSystemPageBody(physicalPage);
    }

    public boolean isRollbackSegment(int pageNo) {
        return body.getTrxSysHeader().rollbackSegmentPointers().anyMatch(x -> x.getPageNo() == pageNo);
    }

    @Override
    public void addNodes(Neo4jHelper neo4jHelper) {
        super.addNodes(neo4jHelper);
        addNodeDoubleWriteBuffer(neo4jHelper);
    }

    private void addNodeDoubleWriteBuffer(Neo4jHelper neo4jHelper) {
        neo4jHelper.execute(session -> session.writeTransaction(tx -> {
            DoubleWriteBuffer dwBuffer = body.getDoubleWriteBuffer();
            Result result = tx.run("MERGE (dw:DoubleWriteBuffer {dwID: toInteger($pageNo)})\n" +
                            "SET dw.MagicNumber=$MagicNumber," +
                            "dw.FirstBlock=$FirstBlock," +
                            "dw.SecondBlock=$SecondBlock " +
                            " RETURN dw;",
                    parameters("pageNo", getPageNo(),
                            "MagicNumber", dwBuffer.getMagicNumber(),
                            "FirstBlock", doubleWriteBlock(dwBuffer.getFirstPageNoInFirstBlock()),
                            "SecondBlock", doubleWriteBlock(dwBuffer.getFirstPageNoInSecondBlock())));

            if (VERBOSE && log.isDebugEnabled() && result.hasNext()) {
                log.debug("add DoubleWriteBuffer node done.");
            }
            return null;
        }));
    }

    private String doubleWriteBlock(int firstPageNoInFirstBlock) {
        return String.format("[%s, %s]", firstPageNoInFirstBlock, firstPageNoInFirstBlock + InnodbConstants.PAGE_NUM_IN_EXTENT - 1);
    }

    @Override
    public void linkNodes(Neo4jHelper neo4jHelper) {
        super.linkNodes(neo4jHelper);
        addLinkBetweenDoubleWriteBufferAndTrxSysPage(neo4jHelper);
        addLinkBetweenDoubleWriteBufferAndSegment(neo4jHelper);
        addLinkBetweenRollbackSegmentPageAndTrxSysPage(neo4jHelper);
    }

    private void addLinkBetweenDoubleWriteBufferAndSegment(Neo4jHelper neo4jHelper) {
        SegmentEntry segmentEntry = segmentEntry(body.getDoubleWriteBuffer().getSegmentPointer());
        neo4jHelper.execute(session -> session.writeTransaction(tx -> {
            tx.run("MERGE (s:Segment {segID: toInteger($SegmentID)})\n" +
                            "SET s.SegmentType = $SegmentType\n" +
                            "return s;",
                    parameters("SegmentID", segmentEntry.getSegmentId(),
                            "SegmentType", "DoubleWriteBufferSegment"));

            tx.run("MATCH (s:Segment)\n" +
                            "WHERE s.segID = toInteger($SegmentID)\n" +
                            "MATCH (dw:DoubleWriteBuffer)\n" +
                            "WHERE dw.dwID = toInteger($DWID)\n" +
                            "MERGE (dw)-[r:double_write_buffer_segment]->(s)\n" +
                            "return r;",
                    parameters("SegmentID", segmentEntry.getSegmentId(),
                            "DWID", getPageNo()));
            return null;
        }));
    }

    private void addLinkBetweenDoubleWriteBufferAndTrxSysPage(Neo4jHelper neo4jHelper) {
        neo4jHelper.execute(session -> session.writeTransaction(tx -> {
            Result result = tx.run("MATCH (dw:DoubleWriteBuffer)\n" +
                            "WHERE dw.dwID = toInteger($pageNo)\n" +
                            "MATCH (p:Page)\n" +
                            "WHERE p.pID = toInteger($pageNo)\n" +
                            "MERGE (p)-[r:double_write_buffer]->(dw)\n" +
                            "return r;",
                    parameters("pageNo", fileHeader.getPageNo()));

            if (VERBOSE && log.isDebugEnabled() && result.hasNext()) {
                log.debug("add relation between DoubleWriteBuffer and trx sys Page done. pageNo: {}", fileHeader.getPageNo());
            }
            return null;
        }));
    }

    private void addLinkBetweenRollbackSegmentPageAndTrxSysPage(Neo4jHelper neo4jHelper) {
        body.getTrxSysHeader().rollbackSegmentPointers().forEach(rollbackSegmentPageNo ->
                neo4jHelper.execute(session -> session.writeTransaction(tx -> {
                    tx.run("MATCH (rp:Page)\n" +
                                    "WHERE rp.pID = toInteger($RollbackSegmentPage)\n" +
                                    "MATCH (tp:Page)\n" +
                                    "WHERE tp.pID = toInteger($pageNo)\n" +
                                    "MERGE (tp)-[r:rollback_segment_page]->(rp)\n" +
                                    "return r;",
                            parameters("RollbackSegmentPage", rollbackSegmentPageNo.getPageNo(),
                                    "pageNo", fileHeader.getPageNo()));
                    return null;
                })));
    }
}
