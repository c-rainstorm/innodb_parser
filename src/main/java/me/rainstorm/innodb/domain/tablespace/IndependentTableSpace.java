package me.rainstorm.innodb.domain.tablespace;

import java.nio.file.Path;

/**
 * @author traceless
 */
public class IndependentTableSpace extends TableSpace {
    public IndependentTableSpace(Path tableSpacePath) {
        super(tableSpacePath);
    }
}
