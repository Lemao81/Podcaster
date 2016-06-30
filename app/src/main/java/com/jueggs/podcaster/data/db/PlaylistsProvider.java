package com.jueggs.podcaster.data.db;

import android.content.ContentResolver;
import android.net.Uri;
import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = PlaylistsProvider.AUTHORITY, database = PlaylistsDb.class)
public class PlaylistsProvider
{
    public static final String AUTHORITY = "com.jueggs.podcaster";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String SEP = "/";
    public static final String VAR_NUMBERS = "/#";
    public static final String VAR_LETTERS = "/*";

    interface Path
    {
        String CHANNEL = "channel";
        String EPISODE = "episode";
        String PLAYLIST = "playlist";
    }

    @TableEndpoint(table = PlaylistsDb.CHANNEL) public static class Channel
    {
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + SEP + AUTHORITY + SEP + Path.CHANNEL;
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + SEP + AUTHORITY + SEP + Path.CHANNEL;

        @ContentUri(path = Path.CHANNEL, type = CONTENT_DIR_TYPE)
        public static final Uri BASE_URI = BASE_CONTENT_URI.buildUpon().appendPath(Path.CHANNEL).build();

        @InexactContentUri(path = Path.CHANNEL + VAR_NUMBERS, type = CONTENT_ITEM_TYPE, name = ChannelColumns.CHANNEL_ID,
                whereColumn = ChannelColumns.CHANNEL_ID, pathSegment = 1)
        public static Uri withChannelId(String channelId)
        {
            return BASE_URI.buildUpon().appendPath(channelId).build();
        }

        @InexactContentUri(path = Path.CHANNEL + VAR_LETTERS, type = CONTENT_DIR_TYPE, name = ChannelColumns.PLAYLIST,
                whereColumn = ChannelColumns.PLAYLIST, pathSegment = 1)
        public static Uri withPlaylist(String playlist)
        {
            return BASE_URI.buildUpon().appendPath(playlist).build();
        }

        @InexactContentUri(path = Path.CHANNEL + VAR_NUMBERS + VAR_LETTERS, type = CONTENT_ITEM_TYPE, name = "channelId_playlist",
                whereColumn = ChannelColumns.CHANNEL_ID, pathSegment = 1)
        public static Uri withChannelIdAndPlaylist(String channelId, String playlist)
        {
            return BASE_URI.buildUpon().appendPath(channelId).appendPath(playlist).build();
        }
    }

    @TableEndpoint(table = PlaylistsDb.EPISODE) public static class Episode
    {
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + SEP + AUTHORITY + SEP + Path.EPISODE;
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + SEP + AUTHORITY + SEP + Path.EPISODE;

        @ContentUri(path = Path.EPISODE, type = CONTENT_DIR_TYPE)
        public static final Uri BASE_URI = BASE_CONTENT_URI.buildUpon().appendPath(Path.EPISODE).build();

        @InexactContentUri(path = Path.EPISODE + VAR_NUMBERS, type = CONTENT_ITEM_TYPE, name = EpisodeColumns.SHOW_ID,
                whereColumn = EpisodeColumns.SHOW_ID, pathSegment = 1)
        public static Uri withShowId(String showId)
        {
            return BASE_URI.buildUpon().appendPath(showId).build();
        }

        @InexactContentUri(path = Path.EPISODE + Path.CHANNEL + VAR_NUMBERS, type = CONTENT_DIR_TYPE, name = EpisodeColumns.CHANNEL_ID,
                whereColumn = EpisodeColumns.CHANNEL_ID, pathSegment = 1)
        public static Uri withChannelId(String channelId)
        {
            return BASE_URI.buildUpon().appendPath(Path.CHANNEL).appendPath(channelId).build();
        }
    }

    @TableEndpoint(table = PlaylistsDb.PLAYLIST) public static class Playlist
    {
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + SEP + AUTHORITY + SEP + Path.PLAYLIST;
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + SEP + AUTHORITY + SEP + Path.PLAYLIST;

        @ContentUri(path = Path.PLAYLIST, type = CONTENT_DIR_TYPE)
        public static final Uri BASE_URI = BASE_CONTENT_URI.buildUpon().appendPath(Path.PLAYLIST).build();

        @InexactContentUri(path = Path.PLAYLIST + VAR_LETTERS, type = CONTENT_ITEM_TYPE, name = PlaylistColumns.NAME,
                whereColumn = PlaylistColumns.NAME, pathSegment = 1)
        public static Uri withName(String name)
        {
            return BASE_URI.buildUpon().appendPath(name).build();
        }
    }
}
