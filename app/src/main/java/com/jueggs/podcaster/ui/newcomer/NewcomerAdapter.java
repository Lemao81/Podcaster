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
import com.jueggs.podcaster.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class NewcomerAdapter extends RecyclerView.Adapter<NewcomerAdapter.ViewHolder>
{
    private List<Channel> channels = new ArrayList<>();
    private Context context;

    public NewcomerAdapter(Context context)
    {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.list_channel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        Channel channel = channels.get(position);
        holder.title.setText(channel.getTitle());
        holder.description.setText(channel.getDescription());
        holder.rating.setText(channel.getRating());

        int subscriberCount = Integer.parseInt(channel.getSubscribers());
        String text = context.getResources().getQuantityString(R.plurals.count_subscribers_format, subscriberCount, subscriberCount);
        holder.subscribers.setText(text);

        holder.date.setText(DateUtils.createDateString(context, channel.getDate()));

        Glide.with(context).load(channel.getImage()).placeholder(R.drawable.glide_placeholder).error(R.drawable.glide_error).into(holder.image);
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

    class ViewHolder extends RecyclerView.ViewHolder
    {
        @Bind(R.id.title) TextView title;
        @Bind(R.id.image) ImageView image;
        @Bind(R.id.rating) TextView rating;
        @Bind(R.id.date) TextView date;
        @Bind(R.id.subscribers) TextView subscribers;
        @Bind(R.id.description) TextView description;

        public ViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
