package me.rainstorm.innodb.domain.page.index.record;

import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.index.IndexPageBody;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author traceless
 */
public class Infimum extends Record {
    public static final int OFFSET = IndexPageBody.OFFSET;
    public static final int LENGTH = RecordHeader.LENGTH + 8;

    private final String desc;

    public Infimum(PhysicalPage physicalPage) {
        super(physicalPage, (short) OFFSET);
        ByteBuffer buffer = physicalPage.getData(OFFSET + RecordHeader.LENGTH);
        byte[] bytes = new byte[8];
        buffer.get(bytes);
        desc = new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        return "Infimum{" +
                "desc='" + desc + '\'' +
                '}';
    }
}
