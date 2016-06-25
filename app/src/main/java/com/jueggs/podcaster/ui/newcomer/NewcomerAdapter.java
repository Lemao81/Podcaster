package com.jueggs.podcaster.ui.newcomer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.model.Channel;
import com.jueggs.podcaster.ui.viewholder.ChannelViewHolder;
import com.jueggs.podcaster.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class NewcomerAdapter extends RecyclerView.Adapter<ChannelViewHolder>
{
    private List<Channel> channels = new ArrayList<>();
    private Context context;

    public NewcomerAdapter(Context context)
    {
        this.context = context;
    }

    @Override
    public ChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.list_channel, parent, false);
        return new ChannelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChannelViewHolder holder, int position)
    {
        Channel channel = channels.get(position);
        holder.bindView(context,channel);
    }

    @Override
    public int getItemCount()
    {
        return channels.size();
    }

    public void setChannels(List<Channel> channels)
    {
        this.channels = channels;
        notifyDataSetChanged();
    }
}
