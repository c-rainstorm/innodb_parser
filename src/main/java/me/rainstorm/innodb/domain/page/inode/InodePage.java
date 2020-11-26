package me.rainstorm.innodb.domain.page.inode;

import lombok.extern.slf4j.Slf4j;
import me.rainstorm.innodb.common.neo4j.Neo4jHelper;
import me.rainstorm.innodb.domain.page.LogicPage;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import org.neo4j.driver.Result;

import static me.rainstorm.innodb.parser.ParserConstants.VERBOSE;
import static org.neo4j.driver.Values.parameters;

/**
 * @author traceless
 */
@Slf4j
public class InodePage extends LogicPage<InodePageBody> {
    public InodePage(PhysicalPage physicalPage) {
        super(physicalPage);
    }

    @Override
    protected InodePageBody createPageBody(PhysicalPage physicalPage) {
        return new InodePageBody(physicalPage);
    }

    @Override
    public void addNodes(Neo4jHelper neo4jHelper) {
        super.addNodes(neo4jHelper);
    }

    @Override
    public void linkNodes(Neo4jHelper neo4jHelper) {
        super.linkNodes(neo4jHelper);
        linkBetweenInodes(neo4jHelper);
    }

    private void linkBetweenInodes(Neo4jHelper neo4jHelper) {
        InodePageBody inodePageBody = body;
        neo4jHelper.execute(session -> session.writeTransaction(tx -> {
            if (inodePageBody.hasPrevious()) {
                Result result = tx.run("MATCH (cp:Page)\n" +
                                "WHERE cp.pID = toInteger($currentPageNo)\n" +
                                "MATCH (pp:Page)\n" +
                                "WHERE pp.pID = toInteger($previousPageNo)\n" +
                                "MERGE (cp)-[r:previous_inode]->(pp)\n" +
                                "return r;",
                        parameters("currentPageNo", getPageNo(),
                                "previousPageNo", inodePageBody.getInodeListNode().getPreInodePageNumber()));

                if (VERBOSE && log.isDebugEnabled() && result.hasNext()) {
                    log.debug("add ({})-[:previous_inode]->({}) relation done.", getPageNo(), inodePageBody.getInodeListNode().getPreInodePageNumber());
                }
            } else {
                if (VERBOSE && log.isDebugEnabled()) {
                    log.debug("inode {} don't have previous inode ", getPageNo());
                }
            }

            if (inodePageBody.hasNext()) {
                Result result = tx.run("MATCH (cp:Page)\n" +
                                "WHERE cp.pID = toInteger($currentPageNo)\n" +
                                "MATCH (np:Page)\n" +
                                "WHERE np.pID = toInteger($nextPageNo)\n" +
                                "MERGE (cp)-[r:next_inode]->(np)\n" +
                                "return r;",
                        parameters("currentPageNo", getPageNo(),
                                "nextPageNo", inodePageBody.getInodeListNode().getNextInodePageNumber()));

                if (VERBOSE && log.isDebugEnabled() && result.hasNext()) {
                    log.debug("add ({})-[:next_inode]->({}) relation done.", getPageNo(), inodePageBody.getInodeListNode().getPreInodePageNumber());
                }
            } else {
                if (VERBOSE && log.isDebugEnabled()) {
                    log.debug("inode {} don't have next inode ", getPageNo());
                }
            }

            return null;
        }));
    }

}
