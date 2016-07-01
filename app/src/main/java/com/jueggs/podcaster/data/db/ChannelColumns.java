package com.jueggs.podcaster.data.db;

import net.simonvt.schematic.annotation.*;

import static net.simonvt.schematic.annotation.ConflictResolutionType.*;
import static net.simonvt.schematic.annotation.DataType.Type.*;

public interface ChannelColumns
{
    @DataType(INTEGER) @NotNull @PrimaryKey @AutoIncrement String _ID = "_id";
    @DataType(TEXT) @NotNull @Unique(onConflict = REPLACE) String CHANNEL_ID = "channel_id";
    @DataType(TEXT) @NotNull @References(table = PlaylistsDb.PLAYLIST, column = PlaylistColumns.NAME) String PLAYLIST = "playlist";
    @DataType(TEXT) String TITLE = "title";
    @DataType(TEXT) String CHANNEL_TITLE = "channel_title";
    @DataType(TEXT) String SUBTITLE = "subtitle";
    @DataType(TEXT) String FEED_LINK = "feed_link";
    @DataType(TEXT) String DESCRIPTION = "description";
    @DataType(TEXT) String POD_LINK = "pod_link";
    @DataType(TEXT) String IMAGE = "image";
    @DataType(TEXT) String CHANNEL_TYPE = "channel_type";
    @DataType(TEXT) String COPYRIGHT = "copyright";
    @DataType(TEXT) String RATING = "rating";
    @DataType(TEXT) String VOTES = "votes";
    @DataType(TEXT) String SUBSCRIBERS = "subscribers";
    @DataType(TEXT) String DATE = "date";

    String[] PROJECTION_COMPLETE = {
            _ID,
            CHANNEL_ID,
            PLAYLIST,
            TITLE,
            CHANNEL_TITLE,
            SUBTITLE,
            FEED_LINK,
            DESCRIPTION,
            POD_LINK,
            IMAGE,
            CHANNEL_TYPE,
            COPYRIGHT,
            RATING,
            VOTES,
            SUBSCRIBERS,
            DATE,
    };

    interface ProjectionCompleteIndices
    {
        int _ID = 0;
        int CHANNEL_ID = 1;
        int PLAYLIST = 2;
        int TITLE = 3;
        int CHANNEL_TITLE = 4;
        int SUBTITLE = 5;
        int FEED_LINK = 6;
        int DESCRIPTION = 7;
        int POD_LINK = 8;
        int IMAGE = 9;
        int CHANNEL_TYPE = 10;
        int COPYRIGHT = 11;
        int RATING = 12;
        int VOTES = 13;
        int SUBSCRIBERS = 14;
        int DATE = 15;
    }
}