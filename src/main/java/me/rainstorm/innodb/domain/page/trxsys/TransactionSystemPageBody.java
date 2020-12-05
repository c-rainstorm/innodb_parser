package me.rainstorm.innodb.domain.page.trxsys;

import lombok.Getter;
import me.rainstorm.innodb.domain.doublewrite.DoubleWriteBuffer;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.core.PageBody;

/**
 * @author traceless
 */
@Getter
public class TransactionSystemPageBody extends PageBody {
    private final TrxSysHeader trxSysHeader;

    private final DoubleWriteBuffer doubleWriteBuffer;

    public TransactionSystemPageBody(PhysicalPage physicalPage) {
        trxSysHeader = new TrxSysHeader(physicalPage);
        doubleWriteBuffer = new DoubleWriteBuffer(physicalPage);
    }
}
