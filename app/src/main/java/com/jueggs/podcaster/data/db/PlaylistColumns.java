package com.jueggs.podcaster.data.db;

import net.simonvt.schematic.annotation.*;

import static net.simonvt.schematic.annotation.ConflictResolutionType.*;
import static net.simonvt.schematic.annotation.DataType.Type.*;

public interface PlaylistColumns
{
    @DataType(INTEGER) @NotNull @PrimaryKey @AutoIncrement String _ID = "_id";
    @DataType(TEXT) @NotNull @Unique(onConflict = REPLACE) String NAME = "name";

    String[] PROJECTION_COMPLETE = {
            _ID,
            NAME
    };

    interface ProjectionCompleteIndices
    {
        int _ID = 0;
        int NAME = 1;
    }
}
