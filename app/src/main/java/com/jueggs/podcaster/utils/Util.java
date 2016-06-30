package com.jueggs.podcaster.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.jueggs.decorator.DividerDecoration;
import com.jueggs.podcaster.App;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.data.db.PlaylistColumns;
import com.jueggs.podcaster.model.Channel;
import com.jueggs.podcaster.ui.category.CategoryAdapter;
import com.jueggs.podcaster.ui.channeldetail.ChannelDetailActivity;
import com.jueggs.podcaster.ui.channeldetail.ChannelDetailFragment;

import java.util.ArrayList;
import java.util.List;

public class Util
{
    public static void equipeRecycler(Context context, RecyclerView recycler, RecyclerView.Adapter<?> adapter)
    {
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(context));
        recycler.addItemDecoration(new DividerDecoration(context, R.drawable.divider));
    }

    public static void showChannelDetails(Context context, FragmentManager fm, Channel channel)
    {
        if (App.getInstance().isTwoPane())
            fm.beginTransaction().replace(R.id.container, ChannelDetailFragment.createInstance(channel)).commit();
        else
        {
            Intent intent = new Intent(context, ChannelDetailActivity.class).putExtra(ChannelDetailActivity.EXTRA_CHANNEL, channel);
            context.startActivity(intent);
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

    public static ContentValues createPlaylistValues(String name)
    {
        ContentValues values = new ContentValues(1);
        values.put(PlaylistColumns.NAME, name);
        return values;
    }
}
