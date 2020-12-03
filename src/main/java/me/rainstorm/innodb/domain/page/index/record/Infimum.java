package me.rainstorm.innodb.domain.page.index.record;

import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.index.IndexPageBody;

/**
 * #define PAGE_OLD_INFIMUM	(PAGE_DATA + 1 + REC_N_OLD_EXTRA_BYTES)
 * #define PAGE_NEW_INFIMUM	(PAGE_DATA + REC_N_NEW_EXTRA_BYTES)
 *
 * @author traceless
 */
public class Infimum extends SpecialRecord {
    public static final int OFFSET = IndexPageBody.OFFSET;

    public Infimum(PhysicalPage physicalPage, boolean newCompactFormat) {
        super(physicalPage, newCompactFormat, getContentOffset(newCompactFormat));
    }

    private static int getContentOffset(boolean newCompactFormat) {
        return newCompactFormat ? IndexPageBody.OFFSET + headerLength(true) :
                IndexPageBody.OFFSET + 1 + headerLength(false);
    }

    @Override
    public String toString() {
        return "Infimum{" +
                "desc='" + desc + '\'' +
                '}';
    }
}
