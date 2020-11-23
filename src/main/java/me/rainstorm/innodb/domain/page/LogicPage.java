package me.rainstorm.innodb.domain.page;

import lombok.Getter;
import me.rainstorm.innodb.domain.page.core.FileHeader;
import me.rainstorm.innodb.domain.page.core.FileTrailer;
import me.rainstorm.innodb.domain.page.core.PageBody;
import org.apache.commons.lang3.StringUtils;

import static me.rainstorm.innodb.common.i18n.I18nUtil.message;

/**
 * @author traceless
 */
@Getter
public abstract class LogicPage<Body extends PageBody> {
    protected final PhysicalPage physicalPage;

    protected final FileHeader fileHeader;

    protected final Body body;

    protected final FileTrailer fileTrailer;

    public LogicPage(PhysicalPage physicalPage) {
        this.physicalPage = physicalPage;
        this.fileHeader = new FileHeader(physicalPage);
        this.fileTrailer = new FileTrailer(physicalPage);
        this.body = createPageBody(physicalPage);
    }

    protected abstract Body createPageBody(PhysicalPage physicalPage);

    public static String title() {
        String header = String.format("[%10s][%10s]Page <%s> ...", "PageNo", "SpaceID", "PageType");
        return header + System.lineSeparator() + StringUtils.repeat("-", 80);
    }

    @Override
    public String toString() {
        return String.format("[%10s][%10d]Page <%s> %s", getPageNo(), fileHeader.getTableSpaceId(),
                pageTypeDesc(), body);
    }

    protected String pageTypeDesc() {
        return message(fileHeader.getPageType().getMsgCode());
    }

    public String getPageNo() {
        return String.valueOf(fileHeader.getPageNo());
    }

    public String verbose() {
        return toString();
    }
}
