package me.rainstorm.innodb.domain.tablespace;

import java.nio.file.Path;

/**
 * @author traceless
 */
public class SystemTableSpace extends TableSpace {
    public SystemTableSpace(Path tableSpacePath) {
        super(tableSpacePath);
    }


}
