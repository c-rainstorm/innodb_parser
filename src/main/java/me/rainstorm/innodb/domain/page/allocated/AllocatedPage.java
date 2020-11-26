package me.rainstorm.innodb.domain.page.allocated;

import me.rainstorm.innodb.common.neo4j.Neo4jHelper;
import me.rainstorm.innodb.domain.page.LogicPage;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.core.Undefined;

/**
 * @author traceless
 */
public class AllocatedPage extends LogicPage<Undefined> {
    public AllocatedPage(PhysicalPage physicalPage) {
        super(physicalPage);
    }

    @Override
    protected Undefined createPageBody(PhysicalPage physicalPage) {
        return Undefined.INSTANCE;
    }

    @Override
    public String toString() {
        return String.format("[%10s][%10s]Page <%s>", "", "", pageTypeDesc());
    }

    @Override
    public void addNodes(Neo4jHelper neo4jHelper) {
        // 新分配页不做任何处理
    }

    @Override
    public void linkNodes(Neo4jHelper neo4jHelper) {
        // 新分配页不做任何处理
    }
}
