package com.jueggs.podcaster.data.db;

import android.content.ContentResolver;
import android.net.Uri;
import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = PodcasterProvider.AUTHORITY, database = PodcasterDb.class)
public class PodcasterProvider
{
    public static final String AUTHORITY = "com.jueggs.podcaster";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String SEP = "/";
    public static final String VAR_NUMBERS = "/#";
    public static final String VAR_LETTERS = "/*";

    interface Path
    {
        String CHANNEL = "channel";
        String PLAYLIST_EPISODE = "playlist_episode";
        String PLAYLIST = "playlist";
        String DOWNLOAD_EPISODE = "download_episode";
    }

    @TableEndpoint(table = PodcasterDb.CHANNEL) public static class Channel
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

        @InexactContentUri(path = Path.CHANNEL + VAR_NUMBERS + VAR_LETTERS, type = CONTENT_ITEM_TYPE,
                name = ChannelColumns.CHANNEL_ID + "_" + ChannelColumns.PLAYLIST, whereColumn = ChannelColumns.CHANNEL_ID, pathSegment = 1)
        public static Uri withChannelIdAndPlaylist(String channelId, String playlist)
        {
            return BASE_URI.buildUpon().appendPath(channelId).appendPath(playlist).build();
        }
    }

    @TableEndpoint(table = PodcasterDb.PLAYLIST_EPISODE) public static class PlaylistEpisode
    {
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + SEP + AUTHORITY + SEP + Path.PLAYLIST_EPISODE;
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + SEP + AUTHORITY + SEP + Path.PLAYLIST_EPISODE;

        @ContentUri(path = Path.PLAYLIST_EPISODE, type = CONTENT_DIR_TYPE)
        public static final Uri BASE_URI = BASE_CONTENT_URI.buildUpon().appendPath(Path.PLAYLIST_EPISODE).build();

        @InexactContentUri(path = Path.PLAYLIST_EPISODE + VAR_NUMBERS, type = CONTENT_ITEM_TYPE, name = EpisodeColumns.SHOW_ID,
                whereColumn = EpisodeColumns.SHOW_ID, pathSegment = 1)
        public static Uri withShowId(String showId)
        {
            return BASE_URI.buildUpon().appendPath(showId).build();
        }

        @InexactContentUri(path = Path.PLAYLIST_EPISODE + Path.CHANNEL + VAR_NUMBERS, type = CONTENT_DIR_TYPE, name = EpisodeColumns.CHANNEL_ID,
                whereColumn = EpisodeColumns.CHANNEL_ID, pathSegment = 1)
        public static Uri withChannelId(String channelId)
        {
            return BASE_URI.buildUpon().appendPath(Path.CHANNEL).appendPath(channelId).build();
        }
    }

    @TableEndpoint(table = PodcasterDb.DOWNLOAD_EPISODE) public static class DownloadEpisode
    {
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + SEP + AUTHORITY + SEP + Path.DOWNLOAD_EPISODE;
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + SEP + AUTHORITY + SEP + Path.DOWNLOAD_EPISODE;

        @ContentUri(path = Path.DOWNLOAD_EPISODE, type = CONTENT_DIR_TYPE)
        public static final Uri BASE_URI = BASE_CONTENT_URI.buildUpon().appendPath(Path.DOWNLOAD_EPISODE).build();

        @InexactContentUri(path = Path.DOWNLOAD_EPISODE + VAR_NUMBERS, type = CONTENT_ITEM_TYPE, name = EpisodeColumns.SHOW_ID,
                whereColumn = EpisodeColumns.SHOW_ID, pathSegment = 1)
        public static Uri withShowId(String showId)
        {
            return BASE_URI.buildUpon().appendPath(showId).build();
        }
    }

    @TableEndpoint(table = PodcasterDb.PLAYLIST) public static class Playlist
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
