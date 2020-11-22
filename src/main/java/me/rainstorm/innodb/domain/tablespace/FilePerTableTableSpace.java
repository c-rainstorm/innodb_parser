package me.rainstorm.innodb.domain.tablespace;

import java.nio.file.Path;

/**
 * @author traceless
 */
public class FilePerTableTableSpace extends TableSpace {
    public FilePerTableTableSpace(Path tableSpacePath) {
        super(tableSpacePath);
    }
}
