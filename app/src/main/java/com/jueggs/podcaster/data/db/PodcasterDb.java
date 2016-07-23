package com.jueggs.podcaster.data.db;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(fileName = PodcasterDb.DB_NAME, version = PodcasterDb.DB_VERSION)
public class PodcasterDb
{
    public static final String DB_NAME = "podcaster.db";
    public static final int DB_VERSION = 1;

    @Table(ChannelColumns.class) public static final String CHANNEL = "channel";
    @Table(EpisodeColumns.class) public static final String PLAYLIST_EPISODE = "playlist_episode";
    @Table(PlaylistColumns.class) public static final String PLAYLIST = "playlist";
    @Table(EpisodeColumns.class) public static final String DOWNLOAD_EPISODE = "download_episode";
}
