package me.rainstorm.innodb.domain.page.sys.rbseg;

import me.rainstorm.innodb.domain.page.LogicPage;
import me.rainstorm.innodb.domain.page.PhysicalPage;

/**
 * storage/innobase/trx/trx0rseg.cc
 * trx_rseg_header_create
 * <p>
 * <p>
 * TRX_RSEG_TYPE_REDO       -    redo rollback segment. 非临时表空间
 * TRX_RSEG_TYPE_NOREDO     -    non-redo rollback segment. 临时表空间
 *
 * @author traceless
 */
public class RollbackSegmentPage extends LogicPage<RollbackSegmentPageBody> {
    public RollbackSegmentPage(PhysicalPage physicalPage) {
        super(physicalPage);
    }

    @Override
    protected RollbackSegmentPageBody createPageBody(PhysicalPage physicalPage) {
        return new RollbackSegmentPageBody(physicalPage);
    }

    @Override
    protected String getPageType() {
        return "RollbackSegmentPage";
    }
}
