package me.rainstorm.innodb.domain.page;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.rainstorm.innodb.common.neo4j.Neo4jHelper;
import me.rainstorm.innodb.domain.extent.Extent;
import me.rainstorm.innodb.domain.page.core.FileHeader;
import me.rainstorm.innodb.domain.page.core.FileTrailer;
import me.rainstorm.innodb.domain.page.core.PageBody;
import me.rainstorm.innodb.domain.page.inode.InodePage;
import me.rainstorm.innodb.domain.segment.SegmentEntry;
import me.rainstorm.innodb.domain.segment.SegmentPointer;
import me.rainstorm.innodb.domain.tablespace.TableSpace;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.driver.Result;

import static me.rainstorm.innodb.common.i18n.I18nUtil.message;
import static me.rainstorm.innodb.parser.ParserConstants.VERBOSE;
import static org.neo4j.driver.Values.parameters;

/**
 * @author traceless
 */
@Getter
@Slf4j
public abstract class LogicPage<Body extends PageBody> {
    protected final PhysicalPage physicalPage;

    protected final FileHeader fileHeader;

    protected final Body body;

    protected final FileTrailer fileTrailer;

    public LogicPage(PhysicalPage physicalPage) {
        this.physicalPage = physicalPage;
        this.fileHeader = new FileHeader(physicalPage);
        this.fileTrailer = new FileTrailer(physicalPage);
        this.body = createPageBody(physicalPage);
    }

    protected abstract Body createPageBody(PhysicalPage physicalPage);

    public Extent extent() {
        return physicalPage.getExtent();
    }

    public TableSpace tableSpace() {
        return extent().getTableSpace();
    }

    public static String title() {
        String header = String.format("[%10s][%10s]Page <%s> ...", "PageNo", "SpaceID", "PageType");
        return header + System.lineSeparator() + StringUtils.repeat("-", 80);
    }

    @Override
    public String toString() {
        return String.format("[%10s][%10d]Page <%s> %s", getPageNo(), fileHeader.getTableSpaceId(),
                pageTypeDesc(), body);
    }

    protected String pageTypeDesc() {
        return message(fileHeader.getPageType().getMsgCode());
    }

    public String getPageNo() {
        return String.valueOf(fileHeader.getPageNo());
    }

    public String verbose() {
        return toString();
    }

    public PageTypeEnum pageType() {
        return fileHeader.getPageType();
    }

    public boolean hasNext() {
        return fileHeader.getNextPageNo() != -1;
    }

    public boolean hasPrevious() {
        return fileHeader.getPrePageNo() != -1;
    }

    public void addNodes(Neo4jHelper neo4jHelper) {
        addNodePage(neo4jHelper);
        addNodePageFileHeader(neo4jHelper);
        addNodePageFileTrailer(neo4jHelper);
    }


    private void addNodePage(Neo4jHelper neo4jHelper) {
        neo4jHelper.execute(session -> session.writeTransaction(tx -> {
            Result result = tx.run("MERGE (p:Page {pID: toInteger($pageNo)})\n" +
                            "SET p.pageType=$pageType" +
                            " RETURN p;",
                    parameters("pageNo", getPageNo(), "pageType", getPageType()));

            if (VERBOSE && log.isDebugEnabled() && result.hasNext()) {
                log.debug("add page node of {} done.", getPageNo());
            }
            return null;
        }));
    }

    protected String getPageType() {
        return pageType().name();
    }

    private void addNodePageFileHeader(Neo4jHelper neo4jHelper) {
        neo4jHelper.execute(session -> session.writeTransaction(tx -> {
            Result result = tx.run("MERGE (pfh:PageFileHeader {pfhID: toInteger($pageNo)})\n" +
                            "SET pfh.pageType = $pageType," +
                            "pfh.lastModifiedLogSequenceNumber = $lastModifiedLogSequenceNumber," +
                            "pfh.lastFlushedLogSequenceNumber = $lastFlushedLogSequenceNumber," +
                            "pfh.checksum = $checksum\n" +
                            "RETURN pfh;",
                    parameters("pageNo", fileHeader.getPageNo(),
                            "pageType", fileHeader.getPageType().getMessageKey(),
                            "lastModifiedLogSequenceNumber", fileHeader.getLastModifiedLogSequenceNumber(),
                            "lastFlushedLogSequenceNumber", fileHeader.getLastFlushedLogSequenceNumber(),
                            "checksum", fileHeader.getChecksum()));

            if (VERBOSE && log.isDebugEnabled() && result.hasNext()) {
                log.debug("add PageFileHeader node of {} done.", fileHeader.getPageNo());
            }

            return null;
        }));
    }

    private void addNodePageFileTrailer(Neo4jHelper neo4jHelper) {
        neo4jHelper.execute(session -> session.writeTransaction(tx -> {
            Result result = tx.run("MERGE (pft:PageFileTrailer {pftID: toInteger($pageNo)})\n" +
                            "SET pft.lastModifiedLogSequenceNumber = $lastModifiedLogSequenceNumber," +
                            "pft.checksum = $checksum\n" +
                            "RETURN pft;",
                    parameters("pageNo", fileHeader.getPageNo(),
                            "lastModifiedLogSequenceNumber", fileHeader.getLastModifiedLogSequenceNumber(),
                            "checksum", fileHeader.getChecksum()));

            if (VERBOSE && log.isDebugEnabled() && result.hasNext()) {
                log.debug("add PageFileTrailer node of {} done.", fileHeader.getPageNo());
            }

            return null;
        }));
    }

    public void linkNodes(Neo4jHelper neo4jHelper) {
        linkBetweenPages(neo4jHelper);

        linkBetweenPageAndHeader(neo4jHelper);

        linkBetweenPageAndTrailer(neo4jHelper);
    }

    private void linkBetweenPages(Neo4jHelper neo4jHelper) {
        neo4jHelper.execute(session -> session.writeTransaction(tx -> {
            if (hasPrevious()) {
                Result result = tx.run("MATCH (cp:Page)\n" +
                                "WHERE cp.pID = toInteger($currentPage)\n" +
                                "MATCH (pp:Page)\n" +
                                "WHERE pp.pID = toInteger($previousPage)\n" +
                                "MERGE (cp)-[r:previous]->(pp)\n" +
                                "return r;",
                        parameters("currentPage", getPageNo(), "previousPage", getFileHeader().getPrePageNo()));

                if (VERBOSE && log.isDebugEnabled() && result.hasNext()) {
                    log.debug("add page relation of ({})-[:previous]->({}) done.", getPageNo(), getFileHeader().getPrePageNo());
                }
            }

            if (hasNext()) {
                Result result = tx.run("MATCH (cp:Page)\n" +
                                "WHERE cp.pID = toInteger($currentPage)\n" +
                                "MATCH (np:Page)\n" +
                                "WHERE np.pID = toInteger($nextPage)\n" +
                                "MERGE (cp)-[r:next]->(np)\n" +
                                "return r;",
                        parameters("currentPage", getPageNo(), "nextPage", getFileHeader().getNextPageNo()));

                if (VERBOSE && log.isDebugEnabled() && result.hasNext()) {
                    log.debug("add page relation of ({})-[:next]->({}) done.", getPageNo(), getFileHeader().getNextPageNo());
                }
            }
            return null;
        }));
    }

    private void linkBetweenPageAndHeader(Neo4jHelper neo4jHelper) {
        neo4jHelper.execute(session -> session.writeTransaction(tx -> {
            Result result = tx.run("MATCH (pfh:PageFileHeader)\n" +
                            "WHERE pfh.pfhID = toInteger($pageNo)\n" +
                            "MATCH (p:Page)\n" +
                            "WHERE p.pID = toInteger($pageNo)\n" +
                            "MERGE (p)-[r:page_header]->(pfh)\n" +
                            "return r;",
                    parameters("pageNo", fileHeader.getPageNo()));

            if (VERBOSE && log.isDebugEnabled() && result.hasNext()) {
                log.debug("add relation between PageFileHeader and Page done. pageNo: {}", fileHeader.getPageNo());
            }
            return null;
        }));
    }

    private void linkBetweenPageAndTrailer(Neo4jHelper neo4jHelper) {
        neo4jHelper.execute(session -> session.writeTransaction(tx -> {
            Result result = tx.run("MATCH (pft:PageFileTrailer)\n" +
                            "WHERE pft.pftID = toInteger($pageNo)\n" +
                            "MATCH (p:Page {pID: toInteger($pageNo)})\n" +
                            "WHERE p.pID = toInteger($pageNo)\n" +
                            "MERGE (p)-[r:page_trailer]->(pft)\n" +
                            "return r;",
                    parameters("pageNo", fileHeader.getPageNo()));

            if (VERBOSE && log.isDebugEnabled() && result.hasNext()) {
                log.debug("add relation between PageFileTrailer and Page done. pageNo: {}", fileHeader.getPageNo());
            }
            return null;
        }));
    }

    protected void doLinkSegment(SegmentPointer segmentPointer, String segmentType, Neo4jHelper neo4jHelper) {
        SegmentEntry segmentEntry = segmentEntry(segmentPointer);
        neo4jHelper.execute(session -> session.writeTransaction(tx -> {
            tx.run("MERGE (s:Segment {segID: toInteger($SegmentID)})\n" +
                            "SET s.SegmentType = $SegmentType\n" +
                            "return s;",
                    parameters("SegmentID", segmentEntry.getSegmentId(),
                            "SegmentType", segmentType));

            tx.run("MATCH (s:Segment)\n" +
                            "WHERE s.segID = toInteger($SegmentID)\n" +
                            "MATCH (p:Page)\n" +
                            "WHERE p.pID = toInteger($PageID)\n" +
                            "MERGE (p)-[r:" + segmentType + "]->(s)\n" +
                            "return r;",
                    parameters("SegmentID", segmentEntry.getSegmentId(),
                            "PageID", getPageNo()));
            return null;
        }));
    }

    protected SegmentEntry segmentEntry(SegmentPointer segmentPointer) {
        TableSpace tableSpace = extent().getTableSpace();
        assert tableSpace.tableSpaceId() == segmentPointer.getSpaceId();
        InodePage inodePage = tableSpace.page(segmentPointer.getInodePageNo());
        return inodePage.getSegmentEntry(segmentPointer.getOffset());
    }
}
