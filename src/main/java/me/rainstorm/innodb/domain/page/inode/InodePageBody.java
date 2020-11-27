package me.rainstorm.innodb.domain.page.inode;

import lombok.Getter;
import me.rainstorm.innodb.domain.InnodbConstants;
import me.rainstorm.innodb.domain.page.PhysicalPage;
import me.rainstorm.innodb.domain.page.core.FileHeader;
import me.rainstorm.innodb.domain.page.core.FileTrailer;
import me.rainstorm.innodb.domain.page.core.PageBody;
import me.rainstorm.innodb.domain.page.core.list.ListNode;
import me.rainstorm.innodb.domain.page.fsp.FileSpaceHeader;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author traceless
 */
@Getter
public class InodePageBody extends PageBody {
    private static final int OFFSET = FileHeader.OFFSET + FileHeader.LENGTH;
    private static final int INODE_ENTRY_PER_PAGE = (InnodbConstants.PAGE_SIZE - FileHeader.LENGTH - FileTrailer.LENGTH - ListNode.LENGTH) / SegmentEntry.LENGTH;

    /**
     * Inode Page 间的链表，表空间内有两个INode Page 链表
     *
     * @see FileSpaceHeader#getSegmentInodesFreeList()
     * @see FileSpaceHeader#getSegmentInodesFullList()
     */
    private final ListNode inodeListNode;

    private final SegmentEntry[] inodeEntries = new SegmentEntry[INODE_ENTRY_PER_PAGE];

    public InodePageBody(PhysicalPage physicalPage) {
        ByteBuffer buffer = physicalPage.getData(OFFSET);
        inodeListNode = new ListNode(buffer);

        for (int i = 0; i < INODE_ENTRY_PER_PAGE; i++) {
            inodeEntries[i] = new SegmentEntry(buffer);
        }
    }

    public boolean hasPrevious() {
        return inodeListNode.getPreInodePageNumber() != -1;
    }

    public boolean hasNext() {
        return inodeListNode.getNextInodePageNumber() != -1;
    }

    /**
     * [1,getNextUnusedSegmentId)，之间的段号都有效
     *
     * @see FileSpaceHeader#getNextUnusedSegmentId()
     */
    public Stream<SegmentEntry> segments() {
        return Arrays.stream(inodeEntries).filter(SegmentEntry::valid);
    }
}
