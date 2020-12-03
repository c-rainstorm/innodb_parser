package me.rainstorm.innodb.domain.page.xdes;

import lombok.extern.slf4j.Slf4j;
import me.rainstorm.innodb.common.neo4j.Neo4jHelper;
import me.rainstorm.innodb.domain.page.LogicPage;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import org.neo4j.driver.Result;

import java.util.Iterator;

import static me.rainstorm.innodb.parser.ParserConstants.VERBOSE;
import static org.neo4j.driver.Values.parameters;

/**
 * 区描述符页，每区的第 0 号页
 *
 * @author traceless
 */
@Slf4j
public abstract class AbstractExtentDescriptorPage<T> extends LogicPage<ExtentDescriptorPageBody<T>> {
    public static final int PAGE_NO_IN_EXTENT = 0;

    public AbstractExtentDescriptorPage(PhysicalPage physicalPage) {
        super(physicalPage);
    }

    @Override
    public void addNodes(Neo4jHelper neo4jHelper) {
        super.addNodes(neo4jHelper);
        addNodeExtent(neo4jHelper);
    }

    @Override
    public void linkNodes(Neo4jHelper neo4jHelper) {
        super.linkNodes(neo4jHelper);
        linkExtendAndTableSpace(neo4jHelper);
        linkFirstPageAndExtend(neo4jHelper);
    }

    private void addNodeExtent(Neo4jHelper neo4jHelper) {
        neo4jHelper.execute(session -> session.writeTransaction(tx -> {
            Iterator<ExtentDescriptorEntry> iterator = body.extents();
            int extentNo = extent().getExtentNo();
            while (iterator.hasNext()) {
                ExtentDescriptorEntry extentDescriptorEntry = iterator.next();
                Result result = tx.run("MERGE (e:Extent {eID: toInteger($extendNo)})\n" +
                                "SET e.extentType = $extentType\n" +
                                "RETURN e;",
                        parameters("extendNo", extentNo,
                                "extentType", extentDescriptorEntry.getExtentType().name()));

                if (VERBOSE && log.isDebugEnabled() && result.hasNext()) {
                    log.debug("add Extent node of {} done.", extent().getExtentNo());
                }
                extentNo++;
            }
            return null;
        }));
    }

    private void linkExtendAndTableSpace(Neo4jHelper neo4jHelper) {
        neo4jHelper.execute(session -> session.writeTransaction(tx -> {
            Result result = tx.run("MATCH (e:Extent)\n" +
                            "WHERE e.eID = toInteger($extentNo)\n" +
                            "MATCH (ts:TableSpace)\n" +
                            "WHERE ts.tsID = toInteger($tableSpaceId)\n" +
                            "MERGE (ts)-[r:extent {number: $extentNo}]->(e)\n" +
                            "return r;",
                    parameters("extentNo", extent().getExtentNo(),
                            "tableSpaceId", extent().getTableSpace().tableSpaceId()));

            if (VERBOSE && log.isDebugEnabled() && result.hasNext()) {
                log.debug("add relation between TableSpace and Extent done. extentNo: {}", extent().getExtentNo());
            }
            return null;
        }));
    }

    private void linkFirstPageAndExtend(Neo4jHelper neo4jHelper) {
        neo4jHelper.execute(session -> session.writeTransaction(tx -> {
            Result result = tx.run("MATCH (e:Extent)\n" +
                            "WHERE e.eID = toInteger($extentNo)\n" +
                            "MATCH (p:Page)\n" +
                            "WHERE p.pID = toInteger($pageNo)\n" +
                            "MERGE (e)-[r:first_page]->(p)\n" +
                            "return r;",
                    parameters("extentNo", extent().getExtentNo(),
                            "pageNo", getPageNo()));

            if (VERBOSE && log.isDebugEnabled() && result.hasNext()) {
                log.debug("add relation between TableSpace and Extent done. extentNo: {}", extent().getExtentNo());
            }

            return null;
        }));
    }
}
