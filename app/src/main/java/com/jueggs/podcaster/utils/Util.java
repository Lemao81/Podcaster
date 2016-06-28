package com.jueggs.podcaster.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.jueggs.decorator.DividerDecoration;
import com.jueggs.podcaster.App;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.model.Channel;
import com.jueggs.podcaster.ui.category.CategoryAdapter;
import com.jueggs.podcaster.ui.channeldetail.ChannelDetailActivity;
import com.jueggs.podcaster.ui.channeldetail.ChannelDetailFragment;

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
}
