package me.rainstorm.innodb.domain;

/**
 * @author traceless
 */
public class InnodbConstants {
    public static final String MYSQL_INNODB_VERSION = "5.7.32";

    public static int ONE_K = 1024;
    public static int ONE_M = ONE_K * ONE_K;
    public static int PAGE_SIZE = 16 * ONE_K;

    /**
     * storage/innobase/include/fsp0types.h
     * <p>
     * File space extent size in pages
     * <p>
     * page size | file space extent size
     * <p>
     * ----------+-----------------------
     * <p>
     * 4 KiB  | 256 pages = 1 MiB
     * <p>
     * 8 KiB  | 128 pages = 1 MiB
     * <p>
     * 16 KiB  |  64 pages = 1 MiB
     * <p>
     * 32 KiB  |  64 pages = 2 MiB
     * <p>
     * 64 KiB  |  64 pages = 4 MiB
     * <p>
     * #define FSP_EXTENT_SIZE((UNIV_PAGE_SIZE <=(16384) ?
     * <p>
     * (1048576/UNIV_PAGE_SIZE):    \
     * <p>
     * ((UNIV_PAGE_SIZE <=(32768))?    \
     * <p>
     * (2097152/UNIV_PAGE_SIZE):    \
     * <p>
     * (4194304/UNIV_PAGE_SIZE))))
     */
    public static int PAGE_NUM_IN_EXTEND =
            PAGE_SIZE <= 16 * ONE_K ?
                    ONE_M / PAGE_SIZE :
                    PAGE_SIZE <= 32 * ONE_K ?
                            2 * ONE_M / PAGE_SIZE :
                            4 * ONE_M / PAGE_SIZE;

    public static int EXTEND_SIZE = PAGE_SIZE * PAGE_NUM_IN_EXTEND;
}
