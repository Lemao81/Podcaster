package com.jueggs.podcaster.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.jueggs.podcaster.data.db.PlaylistsProvider;

import java.util.List;

import static com.jueggs.podcaster.utils.Util.createPlaylistValues;
import static com.jueggs.podcaster.utils.Util.transformCursorToPlaylists;

public class DaUtils
{
    public static List<String> queryPlaylists(Context context)
    {
        Cursor cursor = context.getContentResolver().query(PlaylistsProvider.Playlist.BASE_URI, null, null, null, null);
        return transformCursorToPlaylists(cursor);
    }

    public static int deletePlaylist(Context context, String playlist)
    {
        return context.getContentResolver().delete(PlaylistsProvider.Playlist.withName(playlist), null, null);
    }

    public static int updatePlaylist(Context context, String playlist, String newName)
    {
        return context.getContentResolver().update(PlaylistsProvider.Playlist.withName(playlist), createPlaylistValues(newName), null, null);
    }

    public static Uri insertPlaylist(Context context, String playlist)
    {
        return context.getContentResolver().insert(PlaylistsProvider.Playlist.BASE_URI, createPlaylistValues(playlist));
    }
}
