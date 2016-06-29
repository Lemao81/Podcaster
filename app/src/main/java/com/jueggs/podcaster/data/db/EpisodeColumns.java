package com.jueggs.podcaster.data.db;

import net.simonvt.schematic.annotation.*;

import static com.jueggs.podcaster.data.db.PlaylistsDb.*;
import static net.simonvt.schematic.annotation.ConflictResolutionType.*;
import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface EpisodeColumns
{
    @DataType(INTEGER) @NotNull @PrimaryKey @AutoIncrement String _ID = "_id";
    @DataType(TEXT) @NotNull @Unique(onConflict = REPLACE) String SHOW_ID = "show_id";
    @DataType(TEXT) @NotNull @References(table = PlaylistsDb.CHANNEL, column = ChannelColumns.CHANNEL_ID) String CHANNEL_ID = "channel_id";
    @DataType(TEXT) String TITLE = "title";
    @DataType(TEXT) String DESCRIPTION = "description";
    @DataType(TEXT) String MEDIA_LINK = "media_link";
    @DataType(TEXT) String POD_LINK = "pod_link";
    @DataType(TEXT) String AUTHOR = "author";
    @DataType(TEXT) String RATING = "rating";
    @DataType(TEXT) String VOTES = "votes";
    @DataType(TEXT) String COPYRIGHT = "copyright";
    @DataType(TEXT) String TYPE = "type";
    @DataType(TEXT) String MIME_TYPE = "mime_type";
    @DataType(TEXT) String DATE = "date";
    @DataType(TEXT) String SIZE = "size";
}