package com.jueggs.podcaster.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.jueggs.podcaster.data.db.ChannelColumns;
import com.jueggs.podcaster.data.db.EpisodeColumns;
import com.jueggs.podcaster.data.db.PodcasterProvider;
import com.jueggs.podcaster.model.Channel;
import com.jueggs.podcaster.model.Episode;

import java.util.List;

import static com.jueggs.podcaster.utils.Util.*;

public class DaUtils
{
    public static List<String> queryPlaylists(Context context)
    {
        Cursor cursor = context.getContentResolver().query(PodcasterProvider.Playlist.BASE_URI, null, null, null, null);
        List<String> result = transformCursorToPlaylists(cursor);
        if (cursor != null) cursor.close();
        return result;
    }

    public static int countChannel(Context context, Channel channel)
    {
        Cursor cursor = context.getContentResolver().query(PodcasterProvider.Channel.withChannelId(channel.getChannelId()),
                new String[]{ChannelColumns._ID}, null, null, null);
        int count = cursor == null ? 0 : cursor.getCount();
        if (cursor != null) cursor.close();
        return count;
    }

    public static int deletePlaylist(Context context, String playlist)
    {
        return context.getContentResolver().delete(PodcasterProvider.Playlist.withName(playlist), null, null);
    }

    public static int updatePlaylist(Context context, String playlist, String newName)
    {
        return context.getContentResolver().update(PodcasterProvider.Playlist.withName(playlist), createPlaylistValues(newName), null, null);
    }

    public static Uri insertPlaylist(Context context, String playlist)
    {
        return context.getContentResolver().insert(PodcasterProvider.Playlist.BASE_URI, createPlaylistValues(playlist));
    }

    public static Uri insertChannel(Context context, Channel channel, String playlist)
    {
        return context.getContentResolver().insert(PodcasterProvider.Channel.withChannelIdAndPlaylist(channel.getChannelId(), playlist),
                createChannelValues(channel, playlist));
    }

    public static int deleteChannel(Context context, Channel channel)
    {
        return context.getContentResolver().delete(PodcasterProvider.Channel.withChannelId(channel.getChannelId()), null, null);
    }

    public static List<Channel> queryChannel(Context context, String playlist)
    {
        Cursor cursor = context.getContentResolver().query(PodcasterProvider.Channel.withPlaylist(playlist), ChannelColumns.PROJECTION_COMPLETE,
                null, null, null);
        List<Channel> result = transformCursorToChannels(cursor);
        if (cursor != null) cursor.close();
        return result;
    }

    public static List<Episode> queryDownloadEpisodes(Context context)
    {
        Cursor cursor = context.getContentResolver().query(PodcasterProvider.DownloadEpisode.BASE_URI, EpisodeColumns.PROJECTION_COMPLETE,
                null, null, null);
        List<Episode> result = transformCursorToEpisode(cursor);
        if (cursor != null) cursor.close();
        return result;
    }

    public static int deleteDownloadEpisode(Context context, String showId)
    {
        return context.getContentResolver().delete(PodcasterProvider.DownloadEpisode.withShowId(showId), null, null);
    }

    public static Uri insertDownloadEpisode(Context context, Episode episode)
    {
        return context.getContentResolver().insert(PodcasterProvider.DownloadEpisode.BASE_URI, createEpisodeValues(episode));
    }
}
