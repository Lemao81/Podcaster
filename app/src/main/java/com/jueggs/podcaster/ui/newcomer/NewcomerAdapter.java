package com.jueggs.podcaster.ui.newcomer;

import android.content.Context;
import android.support.v4.app.FragmentManager;
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

import static com.jueggs.podcaster.utils.Util.showChannelDetails;

public class NewcomerAdapter extends RecyclerView.Adapter<NewcomerAdapter.ChannelViewHolder>
{
    private List<Channel> channels = new ArrayList<>();
    private Context context;
    private FragmentManager fragmentManager;

    public NewcomerAdapter(Context context, FragmentManager fragmentManager)
    {
        this.context = context;
        this.fragmentManager = fragmentManager;
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
        holder.bindView(context, channel);
        holder.itemView.setOnClickListener(holder);
    }

    @Override
    public int getItemCount()
    {
        return channels.size();
    }

    public void onNewcomerLoaded(List<Channel> channels)
    {
        this.channels = channels;
        notifyDataSetChanged();
    }

    class ChannelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        @Bind(R.id.title) public TextView title;
        @Bind(R.id.image) public ImageView image;
        @Bind(R.id.rating) public TextView rating;
        @Bind(R.id.date) public TextView date;
        @Bind(R.id.subscribers) public TextView subscribers;
        @Bind(R.id.description) public TextView description;

        public ChannelViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindView(Context context, Channel channel)
        {
            title.setText(channel.getTitle());
            description.setText(channel.getDescription());
            rating.setText(channel.getRating());

            int subscriberCount = Integer.parseInt(channel.getSubscribers());
            String text = context.getResources().getQuantityString(R.plurals.count_subscribers_format, subscriberCount, subscriberCount);
            subscribers.setText(text);

            date.setText(DateUtils.createDateString(context, channel.getDate()));

            Glide.with(context).load(channel.getImage()).placeholder(R.drawable.glide_placeholder).error(R.drawable.glide_error).into(image);
        }

        @Override
        public void onClick(View v)
        {
            showChannelDetails(context, fragmentManager, channels.get(getAdapterPosition()));
        }
    }
}
