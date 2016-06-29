package com.jueggs.podcaster.data.db;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(fileName = PlaylistsDb.DB_NAME, version = PlaylistsDb.DB_VERSION)
public class PlaylistsDb
{
    public static final String DB_NAME = "playlists.db";
    public static final int DB_VERSION = 1;

    @Table(ChannelColumns.class) public static final String CHANNEL = "channel";
    @Table(EpisodeColumns.class) public static final String EPISODE = "episode";
    @Table(PlaylistColumns.class) public static final String PLAYLIST = "playlist";

}
