package me.rainstorm.innodb.domain.page.xdes;

import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.core.PageBody;
import me.rainstorm.innodb.domain.page.fsp.FileSpaceHeader;

import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 * @author traceless
 */
public class ExtentDescriptorPageBody<Header> extends PageBody {
    private static final int FIRST_XDES_ENTRY_OFFSET = FileSpaceHeader.OFFSET + FileSpaceHeader.LENGTH;
    public static final int EXTENT_GROUP_SIZE = 256;

    private final Header fileSpaceHeader;
    private final ExtentDescriptorEntry[] extentDescriptorEntries = new ExtentDescriptorEntry[EXTENT_GROUP_SIZE];

    public ExtentDescriptorPageBody(PhysicalPage physicalPage, Header header) {
        fileSpaceHeader = header;
        ByteBuffer buffer = physicalPage.getData(FIRST_XDES_ENTRY_OFFSET);
        for (int i = 0; i < EXTENT_GROUP_SIZE; i++) {
            extentDescriptorEntries[i] = new ExtentDescriptorEntry(buffer);
            if (extentDescriptorEntries[i].getExtentType() == null) {
                extentDescriptorEntries[i] = null;
            }
        }
    }

    public Header getFileSpaceHeader() {
        return fileSpaceHeader;
    }

    public Iterator<ExtentDescriptorEntry> extents() {
        return new ExtentDescriptorEntryIterator();
    }

    class ExtentDescriptorEntryIterator implements Iterator<ExtentDescriptorEntry> {
        public int index = -1;

        public ExtentDescriptorEntryIterator() {
        }

        @Override
        public boolean hasNext() {
            return index < extentDescriptorEntries.length - 1 &&
                    extentDescriptorEntries[index + 1] != null;
        }

        @Override
        public ExtentDescriptorEntry next() {
            return extentDescriptorEntries[++index];
        }
    }
}
