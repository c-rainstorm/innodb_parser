package me.rainstorm.innodb.domain.page;

import lombok.extern.slf4j.Slf4j;
import me.rainstorm.innodb.domain.page.core.PageBody;
import me.rainstorm.innodb.domain.page.core.SimplePage;
import me.rainstorm.innodb.domain.page.xdes.fsp.FileSpaceHeaderPage;

import static me.rainstorm.innodb.common.i18n.I18nMsgCodeEnum.LogPageTypeDetailNotSupport;
import static me.rainstorm.innodb.common.i18n.I18nUtil.message;
import static me.rainstorm.innodb.parser.ParserConstants.VERBOSE;

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
                if (VERBOSE && log.isDebugEnabled()) {
                    log.debug(message(LogPageTypeDetailNotSupport, pageType));
                }
                return new SimplePage(physicalPage);
        }
    }
}
