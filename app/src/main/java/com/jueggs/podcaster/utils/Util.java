package com.jueggs.podcaster.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.jueggs.decorator.DividerDecoration;
import com.jueggs.podcaster.App;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.data.db.ChannelColumns;
import com.jueggs.podcaster.data.db.PlaylistColumns;
import com.jueggs.podcaster.data.db.PlaylistsDb;
import com.jueggs.podcaster.helper.Result;
import com.jueggs.podcaster.model.Channel;
import com.jueggs.podcaster.ui.category.CategoryAdapter;
import com.jueggs.podcaster.ui.channeldetail.ChannelDetailActivity;
import com.jueggs.podcaster.ui.channeldetail.ChannelDetailFragment;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.References;
import net.simonvt.schematic.annotation.Unique;

import java.util.ArrayList;
import java.util.List;

import static com.jueggs.podcaster.data.db.ChannelColumns.ProjectionCompleteIndices.*;
import static net.simonvt.schematic.annotation.ConflictResolutionType.REPLACE;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public class Util
{
    public static void equipeRecycler(Context context, RecyclerView recycler, RecyclerView.Adapter<?> adapter)
    {
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(context));
        recycler.addItemDecoration(new DividerDecoration(context, R.drawable.divider));
    }

    public static void showChannelDetails(Activity context, FragmentManager fm, Channel channel, View sharedView)
    {
        if (App.getInstance().isTwoPane())
            fm.beginTransaction().replace(R.id.container, ChannelDetailFragment.createInstance(channel)).commit();
        else
        {
            Intent intent = new Intent(context, ChannelDetailActivity.class).putExtra(ChannelDetailActivity.EXTRA_CHANNEL, channel);
            ActivityOptionsCompat opts = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(context, sharedView, context.getString(R.string.transition_channel_img));
            context.startActivity(intent, opts.toBundle());
        }
    }

    public static List<String> transformCursorToPlaylists(Cursor cursor)
    {
        List<String> result = null;
        if (cursor.moveToFirst())
        {
            result = new ArrayList<>(cursor.getCount());
            do
            {
                result.add(cursor.getString(PlaylistColumns.ProjectionCompleteIndices.NAME));
            } while (cursor.moveToNext());
        }
        return result;
    }

    public static List<Channel> transformCursorToChannels(Cursor cursor)
    {
        List<Channel> channels = null;
        if (cursor.moveToFirst())
        {
            channels = new ArrayList<>();
            do
            {
                Channel channel = new Channel();
                channel.setChannelId(cursor.getString(CHANNEL_ID));
                channel.setTitle(cursor.getString(TITLE));
                channel.setChannelTitle(cursor.getString(CHANNEL_TITLE));
                channel.setSubtitle(cursor.getString(SUBTITLE));
                channel.setFeedLink(cursor.getString(FEED_LINK));
                channel.setDescription(cursor.getString(DESCRIPTION));
                channel.setPodLink(cursor.getString(POD_LINK));
                channel.setImage(cursor.getString(IMAGE));
                channel.setChannelType(cursor.getString(CHANNEL_TYPE));
                channel.setCopyright(cursor.getString(COPYRIGHT));
                channel.setRating(cursor.getString(RATING));
                channel.setVotes(cursor.getString(VOTES));
                channel.setSubscribers(cursor.getString(SUBSCRIBERS));
                channel.setDate(cursor.getString(DATE));
                channels.add(channel);
            } while (cursor.moveToNext());
        }
        return channels;
    }

    public static ContentValues createPlaylistValues(String name)
    {
        ContentValues values = new ContentValues(1);
        values.put(PlaylistColumns.NAME, name);
        return values;
    }

    public static ContentValues createChannelValues(Channel channel, String playlist)
    {
        ContentValues values = new ContentValues();
        values.put(ChannelColumns.CHANNEL_ID, channel.getChannelId());
        values.put(ChannelColumns.PLAYLIST, playlist);
        values.put(ChannelColumns.TITLE, channel.getTitle());
        values.put(ChannelColumns.CHANNEL_TITLE, channel.getChannelTitle());
        values.put(ChannelColumns.SUBTITLE, channel.getSubtitle());
        values.put(ChannelColumns.FEED_LINK, channel.getFeedLink());
        values.put(ChannelColumns.DESCRIPTION, channel.getDescription());
        values.put(ChannelColumns.POD_LINK, channel.getPodLink());
        values.put(ChannelColumns.IMAGE, channel.getImage());
        values.put(ChannelColumns.CHANNEL_TYPE, channel.getChannelType());
        values.put(ChannelColumns.COPYRIGHT, channel.getCopyright());
        values.put(ChannelColumns.RATING, channel.getRating());
        values.put(ChannelColumns.VOTES, channel.getVotes());
        values.put(ChannelColumns.SUBSCRIBERS, channel.getSubscribers());
        values.put(ChannelColumns.DATE, channel.getDate());
        return values;
    }

    public static void writeNetworkState(Context context, int state)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putInt(context.getString(R.string.pref_network_state), state).commit();
    }

    public static int readNetworkState(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(context.getString(R.string.pref_network_state), Result.UNKNOWN);
    }

    public static void showEmptyView(Context context, View container, int textId, int imageId)
    {
        ((TextView) container.findViewById(R.id.emptyText)).setText(textId);
        ((ImageView) container.findViewById(R.id.emptyImage)).setImageDrawable(ContextCompat.getDrawable(context, imageId));
        container.setVisibility(View.VISIBLE);
    }
}