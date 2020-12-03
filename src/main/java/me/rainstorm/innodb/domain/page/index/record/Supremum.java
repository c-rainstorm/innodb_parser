package me.rainstorm.innodb.domain.page.index.record;

import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.index.IndexPageBody;

/**
 * #define PAGE_OLD_SUPREMUM	(PAGE_DATA + 2 + 2 * REC_N_OLD_EXTRA_BYTES + 8)
 * #define PAGE_OLD_SUPREMUM_END (PAGE_OLD_SUPREMUM + 9)
 * <p>
 * #define PAGE_NEW_SUPREMUM	(PAGE_DATA + 2 * REC_N_NEW_EXTRA_BYTES + 8)
 * #define PAGE_NEW_SUPREMUM_END (PAGE_NEW_SUPREMUM + 8)
 *
 * @author traceless
 */
public class Supremum extends SpecialRecord {

    public Supremum(PhysicalPage physicalPage, boolean newCompactFormat) {
        super(physicalPage, newCompactFormat, getContentOffset(newCompactFormat));
    }

    private static int getContentOffset(boolean newCompactFormat) {
        return newCompactFormat ? IndexPageBody.OFFSET + 2 * headerLength(true) + 8 :
                IndexPageBody.OFFSET + 2 + 2 * headerLength(false) + 8;
    }

    @Override
    public String toString() {
        return "Supremum{" +
                "desc='" + desc + '\'' +
                '}';
    }
}
