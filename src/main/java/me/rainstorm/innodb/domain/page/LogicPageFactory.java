package me.rainstorm.innodb.domain.page;

import lombok.extern.slf4j.Slf4j;
import me.rainstorm.innodb.domain.page.core.PageBody;
import me.rainstorm.innodb.domain.page.core.SimplePage;
import me.rainstorm.innodb.domain.page.xdes.fsp.FileSpaceHeaderPage;

import static me.rainstorm.innodb.parser.ParserConstants.verbose;

/**
 * @author traceless
 */
@Slf4j
public class LogicPageFactory {
    public static LogicPage<? extends PageBody> of(PhysicalPage physicalPage) {
        PageTypeEnum pageType = physicalPage.pageType();
        switch (pageType) {
            case Allocated:
                return new AllocatedPage(physicalPage);
            case FileSpaceHeader:
                return new FileSpaceHeaderPage(physicalPage);
            default:
                if (verbose && log.isDebugEnabled()) {
                    log.debug("当前页类型 [{}] 详细解析暂不支持", pageType);
                }
                return new SimplePage(physicalPage);
        }
    }
}
