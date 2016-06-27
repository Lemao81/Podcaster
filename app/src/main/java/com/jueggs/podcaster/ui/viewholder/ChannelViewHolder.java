package com.jueggs.podcaster.ui.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.model.Channel;
import com.jueggs.podcaster.utils.DateUtils;

public class ChannelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
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

    }
}
