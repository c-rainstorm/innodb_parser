package me.rainstorm.innodb.domain.page;

import lombok.Getter;

import java.util.Arrays;

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
    Allocated(0, "FIL_PAGE_TYPE_ALLOCATED"),
    /**
     * Undo log page
     */
    UndoLog(2, "FIL_PAGE_UNDO_LOG"),
    /**
     * Segment info page
     */
    Inode(3, "FIL_PAGE_INODE"),
    /**
     * Insert buffer free list
     */
    InsertBufferFreeList(4, "FIL_PAGE_IBUF_FREE_LIST"),

    /**
     * Insert buffer bitmap
     */
    IBufBitmap(5, "FIL_PAGE_IBUF_BITMAP"),
    /**
     * System page
     */
    System(6, "FIL_PAGE_TYPE_SYS"),
    /**
     * Transaction system data
     */
    TransactionSystem(7, "FIL_PAGE_TYPE_TRX_SYS"),
    /**
     * File space header
     */
    FileSpaceHeader(8, "FIL_PAGE_TYPE_FSP_HDR"),
    /**
     * Extent descriptor page
     */
    ExtendDescriptor(9, "FIL_PAGE_TYPE_XDES"),
    /**
     * Uncompressed BLOB page
     */
    Blob(10, "FIL_PAGE_TYPE_BLOB"),
    /**
     * First compressed BLOB page
     */
    ZBlob(11, "FIL_PAGE_TYPE_ZBLOB"),
    /**
     * Subsequent compressed BLOB page
     */
    ZBlob2(12, "FIL_PAGE_TYPE_ZBLOB2"),
    /**
     * In old tablespaces, garbage in FIL_PAGE_TYPE is replaced with this
     * value when flushing pages
     */
    Unknown(13, "FIL_PAGE_TYPE_UNKNOWN"),
    /**
     * Compressed page
     */
    Compressed(14, "FIL_PAGE_COMPRESSED"),
    /**
     * Encrypted page
     */
    Encrypted(15, "FIL_PAGE_ENCRYPTED"),
    /**
     * Compressed and Encrypted page
     */
    CompressedAndEncrypted(16, "FIL_PAGE_COMPRESSED_AND_ENCRYPTED"),
    /**
     * Encrypted R-tree page
     */
    EncryptedRTree(17, "FIL_PAGE_ENCRYPTED_RTREE"),
    /**
     * B-tree node
     */
    Index(17855, "FIL_PAGE_INDEX"),

    /**
     * R-tree node
     */
    RTree(17854, "FIL_PAGE_RTREE");

    PageTypeEnum(int code, String messageKey) {
        this.code = (short) code;
        this.messageKey = messageKey;
    }

    private final short code;
    private final String messageKey;

    public static PageTypeEnum of(short code) {
        return Arrays.stream(PageTypeEnum.values()).filter(x -> x.code == code).findAny()
                .orElseThrow(() -> new RuntimeException("PageType of " + code + " is undefined"));
    }
}
