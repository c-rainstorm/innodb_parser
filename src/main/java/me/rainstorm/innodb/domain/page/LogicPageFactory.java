package me.rainstorm.innodb.domain.page;

import lombok.extern.slf4j.Slf4j;
import me.rainstorm.innodb.domain.page.allocated.AllocatedPage;
import me.rainstorm.innodb.domain.page.core.PageBody;
import me.rainstorm.innodb.domain.page.core.SimplePage;
import me.rainstorm.innodb.domain.page.fsp.FileSpaceHeaderPage;
import me.rainstorm.innodb.domain.page.index.IndexPage;
import me.rainstorm.innodb.domain.page.inode.InodePage;
import me.rainstorm.innodb.domain.page.sys.DataDirectoryHeaderPage;
import me.rainstorm.innodb.domain.page.xdes.ExtentDescriptorPage;

import static me.rainstorm.innodb.common.i18n.I18nMsgCodeEnum.LogPageTypeDetailNotSupport;
import static me.rainstorm.innodb.common.i18n.I18nUtil.message;
import static me.rainstorm.innodb.domain.tablespace.SystemTableSpace.FSP_DICT_HDR_PAGE_NO;
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
            case ExtentDescriptor:
                return new ExtentDescriptorPage(physicalPage);
            case Inode:
                return new InodePage(physicalPage);
            case Index:
                return new IndexPage(physicalPage);
            case System:
                switch (physicalPage.getPageNo()) {
                    case FSP_DICT_HDR_PAGE_NO:
                        return new DataDirectoryHeaderPage(physicalPage);
                    default:
                        return new SimplePage(physicalPage);
                }
            default:
                if (VERBOSE && log.isDebugEnabled()) {
                    log.debug(message(LogPageTypeDetailNotSupport, pageType));
                }
                return new SimplePage(physicalPage);
        }
    }
}
