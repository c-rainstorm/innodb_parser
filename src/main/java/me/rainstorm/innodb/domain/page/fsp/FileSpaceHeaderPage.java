package me.rainstorm.innodb.domain.page.fsp;

import lombok.extern.slf4j.Slf4j;
import me.rainstorm.innodb.common.neo4j.Neo4jHelper;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.core.list.ListBaseNode;
import me.rainstorm.innodb.domain.page.xdes.AbstractExtentDescriptorPage;
import me.rainstorm.innodb.domain.page.xdes.ExtentDescriptorPageBody;
import org.neo4j.driver.Result;

import static me.rainstorm.innodb.parser.ParserConstants.VERBOSE;
import static org.neo4j.driver.Values.parameters;

/**
 * 特殊的区描述符页，
 * 表空间，第 0 个区的 0 号页
 *
 * @author traceless
 */
@Slf4j
public class FileSpaceHeaderPage extends AbstractExtentDescriptorPage<FileSpaceHeader> {
    public static final int PAGE_NO = 0;

    public FileSpaceHeaderPage(PhysicalPage physicalPage) {
        super(physicalPage);
    }

    @Override
    protected ExtentDescriptorPageBody<FileSpaceHeader> createPageBody(PhysicalPage physicalPage) {
        return new ExtentDescriptorPageBody<>(physicalPage, new FileSpaceHeader(physicalPage));
    }

    public int totalPageNumber() {
        return body.getFileSpaceHeader().getTotalPageNumber();
    }

    @Override
    public void addNodes(Neo4jHelper neo4jHelper) {
        super.addNodes(neo4jHelper);
        addNodeTableSpace(neo4jHelper);
    }

    private void addNodeTableSpace(Neo4jHelper neo4jHelper) {
        FileSpaceHeader fileSpaceHeader = body.getFileSpaceHeader();

        neo4jHelper.execute(session -> session.writeTransaction(tx -> {
            Result result = tx.run("MERGE (ts:TableSpace {tsID: toInteger($tableSpaceId)})\n" +
                            "SET ts.file=$path," +
                            "ts.totalPageNumber=$totalPageNumber," +
                            "ts.minimumUninitializedPageNumber=$minimumUninitializedPageNumber," +
                            "ts.nextUnusedSegmentId=$nextUnusedSegmentId" +
                            " RETURN ts;",
                    parameters("tableSpaceId", fileSpaceHeader.getSpaceId(),
                            "path", tableSpace().relativePath(),
                            "totalPageNumber", fileSpaceHeader.getTotalPageNumber(),
                            "minimumUninitializedPageNumber", fileSpaceHeader.getMinimumUninitializedPageNumber(),
                            "nextUnusedSegmentId", fileSpaceHeader.getNextUnusedSegmentId()
                    ));

            if (VERBOSE && log.isDebugEnabled() && result.hasNext()) {
                log.debug("add table space of {} done.", fileSpaceHeader.getSpaceId());
            }

            return null;
        }));
    }

    @Override
    public void linkNodes(Neo4jHelper neo4jHelper) {
        super.linkNodes(neo4jHelper);
        linkBetweenPageAndTableSpace(neo4jHelper);
        linkSegmentList(neo4jHelper);
    }

    private void linkBetweenPageAndTableSpace(Neo4jHelper neo4jHelper) {
        neo4jHelper.execute(session -> session.writeTransaction(tx -> {
            // add relation between Page and TableSpace
            Result result = tx.run("MATCH (p:Page)\n" +
                            "WHERE p.pID = toInteger($currentPage)\n" +
                            "MATCH (ts:TableSpace)\n" +
                            "WHERE ts.tsID = toInteger($tableSpaceId)\n" +
                            "MERGE (ts)-[r:contain]->(p)\n" +
                            "return r;",
                    parameters("currentPage", getPageNo(), "tableSpaceId", getFileHeader().getTableSpaceId()));

            if (VERBOSE && log.isDebugEnabled() && result.hasNext()) {
                log.debug("add page relation of ({})-[:contain]->({}) done.", getFileHeader().getTableSpaceId(), getPageNo());
            }
            return null;
        }));
    }


    private void linkSegmentList(Neo4jHelper neo4jHelper) {
        FileSpaceHeader fileSpaceHeader = body.getFileSpaceHeader();

        doLinkSegmentList(neo4jHelper, fileSpaceHeader.getSegmentInodesFreeList(), "free");
        doLinkSegmentList(neo4jHelper, fileSpaceHeader.getSegmentInodesFullList(), "full");
    }

    private void doLinkSegmentList(Neo4jHelper neo4jHelper, ListBaseNode segmentInodeList, String scene) {
        if (segmentInodeList.getLength() <= 0) {
            return;
        }

        neo4jHelper.execute(session -> session.writeTransaction(tx -> {
            Result result = tx.run("MATCH (cp:Page)\n" +
                            "WHERE cp.pID = toInteger($currentPageNo)\n" +
                            "MATCH (pp:Page)\n" +
                            "WHERE pp.pID = toInteger($firstInodePageNo)\n" +
                            "MERGE (cp)-[r:first_inode_in_" + scene + "_list {length: $length}]->(pp)\n" +
                            "return r;",
                    parameters("currentPageNo", getFileHeader().getPageNo(),
                            "firstInodePageNo", segmentInodeList.getFirstNodePageNumber(),
                            "length", segmentInodeList.getLength()));

            if (VERBOSE && log.isDebugEnabled() && result.hasNext()) {
                log.debug("add ({})-[:first_inode_in_{}_list {length: {}})->({}) relation done.",
                        getPageNo(), scene, segmentInodeList.getLength(), segmentInodeList.getFirstNodePageNumber());
            }

            result = tx.run("MATCH (cp:Page)\n" +
                            "WHERE cp.pID = toInteger($currentPageNo)\n" +
                            "MATCH (pp:Page)\n" +
                            "WHERE pp.pID = toInteger($lastInodePageNo)\n" +
                            "MERGE (cp)-[r:last_inode_in_" + scene + "_list {length: $length}]->(pp)\n" +
                            "return r",
                    parameters("currentPageNo", getFileHeader().getPageNo(),
                            "lastInodePageNo", segmentInodeList.getLastNodePageNumber(),
                            "length", segmentInodeList.getLength()));

            if (VERBOSE && log.isDebugEnabled() && result.hasNext()) {
                log.debug("add ({})-[:last_inode_in_{}_list {length: {}}]->({}) relation done.",
                        getPageNo(), scene, segmentInodeList.getLength(), segmentInodeList.getLastNodePageNumber());
            }
            return null;
        }));
    }
}
