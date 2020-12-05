package me.rainstorm.innodb.domain.page.sys.ddh;

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
public class DataDirectoryHeaderPage extends LogicPage<DataDirectoryPageBody> {

    public DataDirectoryHeaderPage(PhysicalPage physicalPage) {
        super(physicalPage);
    }

    @Override
    protected DataDirectoryPageBody createPageBody(PhysicalPage physicalPage) {
        return new DataDirectoryPageBody(physicalPage);
    }

    @Override
    public void addNodes(Neo4jHelper neo4jHelper) {
        super.addNodes(neo4jHelper);
    }

    @Override
    public void linkNodes(Neo4jHelper neo4jHelper) {
        super.linkNodes(neo4jHelper);
        linkNodeBetweenDataDirectoryAnd(neo4jHelper, body.getSysTablesPrimaryIndexRootPage(), "SYS_TABLE_Primary_Index");
        linkNodeBetweenDataDirectoryAnd(neo4jHelper, body.getSysTablesSecondaryIndexForIdRootPage(), "SYS_TABLE_Secondary_Index");
        linkNodeBetweenDataDirectoryAnd(neo4jHelper, body.getSysColumnsPrimaryIndexRootPage(), "SYS_COLUMNS_Primary_Index");
        linkNodeBetweenDataDirectoryAnd(neo4jHelper, body.getSysFieldsPrimaryIndexRootPage(), "SYS_FIELDS_Primary_Index");
        linkNodeBetweenDataDirectoryAnd(neo4jHelper, body.getSysIndexesPrimaryIndexRootPage(), "SYS_INDEXES_Primary_Index");
    }

    private void linkNodeBetweenDataDirectoryAnd(Neo4jHelper neo4jHelper, int indexPageNo, String relation) {
        neo4jHelper.execute(session -> session.writeTransaction(tx -> {
            Result result = tx.run("MATCH (ddp:Page)\n" +
                            "WHERE ddp.pID = toInteger($DDP)\n" +
                            "MATCH (ip:Page)\n" +
                            "WHERE ip.pID = toInteger($IP)\n" +
                            "MERGE (ddp)-[r: " + relation + "]->(ip)\n" +
                            "return r;",
                    parameters("DDP", getPageNo(),
                            "IP", indexPageNo));

            if (VERBOSE && log.isDebugEnabled() && result.hasNext()) {
                log.debug("add page relation of ({})-[:{}]->({}) done.", getPageNo(), relation, getFileHeader().getPrePageNo());
            }
            return null;
        }));
    }

    @Override
    protected String getPageType() {
        return "DataDirectoryHeader";
    }
}
