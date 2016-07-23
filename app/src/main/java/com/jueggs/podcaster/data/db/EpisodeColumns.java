package com.jueggs.podcaster.data.db;

import net.simonvt.schematic.annotation.*;

import static net.simonvt.schematic.annotation.ConflictResolutionType.*;
import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface EpisodeColumns
{
    @DataType(INTEGER) @NotNull @PrimaryKey @AutoIncrement String _ID = "_id";
    @DataType(TEXT) @NotNull @Unique(onConflict = REPLACE) String SHOW_ID = "show_id";
    @DataType(TEXT) @References(table = PodcasterDb.CHANNEL, column = ChannelColumns.CHANNEL_ID) String CHANNEL_ID = "channel_id";
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

    String[] PROJECTION_COMPLETE = new String[]{
            _ID,
            SHOW_ID,
            CHANNEL_ID,
            TITLE,
            DESCRIPTION,
            MEDIA_LINK,
            POD_LINK,
            AUTHOR,
            RATING,
            VOTES,
            COPYRIGHT,
            TYPE,
            MIME_TYPE,
            DATE,
            SIZE
    };

    interface ProjectionCompleteIndices
    {
        int _ID = 0;
        int SHOW_ID = 1;
        int CHANNEL_ID = 2;
        int TITLE = 3;
        int DESCRIPTION = 4;
        int MEDIA_LINK = 5;
        int POD_LINK = 6;
        int AUTHOR = 7;
        int RATING = 8;
        int VOTES = 9;
        int COPYRIGHT = 10;
        int TYPE = 11;
        int MIME_TYPE = 12;
        int DATE = 13;
        int SIZE = 14;
    }
}