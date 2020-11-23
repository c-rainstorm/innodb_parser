package me.rainstorm.innodb.domain.page;

import lombok.Getter;
import me.rainstorm.innodb.common.i18n.I18nMsgCodeEnum;

import java.util.Arrays;

import static me.rainstorm.innodb.common.i18n.I18nMsgCodeEnum.*;

/**
 * 页类型
 *
 * @author traceless
 */
@Getter
public enum PageTypeEnum {
    /**
     * Freshly allocated page
     */
    Allocated(0, "FIL_PAGE_TYPE_ALLOCATED", TermPageTypeAllocated),
    /**
     * Undo log page
     */
    UndoLog(2, "FIL_PAGE_UNDO_LOG", TermPageTypeUndoLog),
    /**
     * Segment info page
     */
    Inode(3, "FIL_PAGE_INODE", TermPageTypeInode),
    /**
     * Insert buffer free list
     */
    InsertBufferFreeList(4, "FIL_PAGE_IBUF_FREE_LIST", TermPageTypeInsertBufferFreeList),

    /**
     * Insert buffer bitmap
     */
    IBufBitmap(5, "FIL_PAGE_IBUF_BITMAP", TermPageTypeIBufBitmap),
    /**
     * System page
     */
    System(6, "FIL_PAGE_TYPE_SYS", TermPageTypeSystem),
    /**
     * Transaction system data
     */
    TransactionSystem(7, "FIL_PAGE_TYPE_TRX_SYS", TermPageTypeTransactionSystem),
    /**
     * File space header
     */
    FileSpaceHeader(8, "FIL_PAGE_TYPE_FSP_HDR", TermPageTypeFileSpaceHeader),
    /**
     * Extent descriptor page
     */
    ExtendDescriptor(9, "FIL_PAGE_TYPE_XDES", TermPageTypeExtendDescriptor),
    /**
     * Uncompressed BLOB page
     */
    Blob(10, "FIL_PAGE_TYPE_BLOB", TermPageTypeBlob),
    /**
     * First compressed BLOB page
     */
    ZBlob(11, "FIL_PAGE_TYPE_ZBLOB", TermPageTypeZBlob),
    /**
     * Subsequent compressed BLOB page
     */
    ZBlob2(12, "FIL_PAGE_TYPE_ZBLOB2", TermPageTypeZBlob2),
    /**
     * In old tablespaces, garbage in FIL_PAGE_TYPE is replaced with this
     * value when flushing pages
     */
    Unknown(13, "FIL_PAGE_TYPE_UNKNOWN", TermPageTypeUnknown),
    /**
     * Compressed page
     */
    Compressed(14, "FIL_PAGE_COMPRESSED", TermPageTypeCompressed),
    /**
     * Encrypted page
     */
    Encrypted(15, "FIL_PAGE_ENCRYPTED", TermPageTypeEncrypted),
    /**
     * Compressed and Encrypted page
     */
    CompressedAndEncrypted(16, "FIL_PAGE_COMPRESSED_AND_ENCRYPTED", TermPageTypeCompressedAndEncrypted),
    /**
     * Encrypted R-tree page
     */
    EncryptedRTree(17, "FIL_PAGE_ENCRYPTED_RTREE", TermPageTypeEncryptedRTree),
    /**
     * B-tree node
     */
    Index(17855, "FIL_PAGE_INDEX", TermPageTypeIndex),

    /**
     * R-tree node
     */
    RTree(17854, "FIL_PAGE_RTREE", TermPageTypeRTree);

    PageTypeEnum(int code, String messageKey, I18nMsgCodeEnum msgCode) {
        this.code = (short) code;
        this.messageKey = messageKey;
        this.msgCode = msgCode;
    }

    private final short code;
    private final String messageKey;
    private final I18nMsgCodeEnum msgCode;

    public static PageTypeEnum of(short code) {
        return Arrays.stream(PageTypeEnum.values()).filter(x -> x.code == code).findAny()
                .orElseThrow(() -> new RuntimeException("PageType of " + code + " is undefined"));
    }
}
