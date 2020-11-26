package me.rainstorm.innodb.common.neo4j;

import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.*;
import org.neo4j.driver.summary.ResultSummary;
import org.neo4j.driver.summary.SummaryCounters;

import java.util.function.Consumer;

import static me.rainstorm.innodb.parser.ParserConstants.*;

/**
 * @author traceless
 */
@Slf4j
public class Neo4jHelper implements AutoCloseable {
    private final Driver driver;
    private final Session session;

    public Neo4jHelper() {
        driver = GraphDatabase.driver(System.getProperty(PROPERTY_NEO4J_URL),
                AuthTokens.basic(System.getProperty(PROPERTY_NEO4J_USER),
                        System.getProperty(PROPERTY_NEO4J_PASSWORD)));
        session = driver.session();

        try {
            clearDatabase();

            createIndex();
        } catch (Exception e) {
            System.err.println("Connect to local neo4j database error. " + e.getMessage() + " please make sure your configuration in iprc.sh is correct.");
            System.exit(-1);
        }
    }

    private void createIndex() {
        session.writeTransaction(tx -> {
            tx.run("CREATE INDEX page_idx IF NOT EXISTS FOR (p:Page) ON (p.pID)");
            tx.run("CREATE INDEX pfh_idx IF NOT EXISTS FOR (pfh:PageFileHeader) ON (pfh.pfhID)");
            tx.run("CREATE INDEX pft_idx IF NOT EXISTS FOR (pft:PageFileHeader) ON (pft.pftID)");
            return null;
        });
    }

    private void clearDatabase() {
        session.writeTransaction(tx -> {
            Result result = tx.run("MATCH (n) DETACH DELETE n;");
            if (VERBOSE && log.isDebugEnabled()) {
                ResultSummary resultSummary = result.consume();
                SummaryCounters counters = resultSummary.counters();
                log.debug("clear neo4j database [{}] on [{}]. [{}] nodes deleted, [{}] relationships deleted.",
                        resultSummary.database().name(), resultSummary.server().address(),
                        counters.nodesDeleted(), counters.relationshipsDeleted());
            }

            return null;
        });
    }

    @Override
    public void close() throws Exception {
        session.close();
        driver.close();
    }

    public void execute(Consumer<Session> sessionConsumer) {
        sessionConsumer.accept(session);
    }
}
