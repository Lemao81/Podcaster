package com.jueggs.podcaster.ui.charts;

import android.app.Activity;
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
import com.jueggs.podcaster.utils.Util;

import java.util.ArrayList;
import java.util.List;

import static com.jueggs.podcaster.utils.Util.*;

public class ChartsAdapter extends RecyclerView.Adapter<ChartsAdapter.ViewHolder>
{
    private List<Channel> channels = new ArrayList<>();
    private Context context;
    private FragmentManager fragmentManager;

    public ChartsAdapter(Context context, FragmentManager fragmentManager)
    {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.list_charts, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        Channel channel = channels.get(position);
        holder.ranking.setText(String.valueOf(position + 1));
        holder.title.setText(channel.getTitle());
        holder.itemView.setOnClickListener(holder);

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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        @Bind(R.id.ranking) TextView ranking;
        @Bind(R.id.title) TextView title;
        @Bind(R.id.image) ImageView image;

        public ViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v)
        {
            showChannelDetails((Activity) context, fragmentManager, channels.get(getAdapterPosition()), image);
        }
    }
}
