package com.jueggs.podcaster.ui.channeldetail;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.model.Channel;
import com.jueggs.podcaster.model.Episode;
import com.jueggs.podcaster.utils.DateUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ChannelDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    public static final int VIEWTYPE_DETAILS = 1;
    public static final int VIEWTYPE_EPISODE = 2;

    @Getter
    private List<Episode> episodes = new ArrayList<>();
    private Channel channel;
    private Context context;
    private Callback callback;
    private boolean favourized;

    public ChannelDetailAdapter(Context context, Channel channel, Callback callback, boolean favourized)
    {
        this.context = context;
        this.channel = channel;
        this.callback = callback;
        this.favourized = favourized;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view;

        switch (viewType)
        {
            case VIEWTYPE_DETAILS:
                view = LayoutInflater.from(context).inflate(R.layout.list_channel_detail, parent, false);
                return new DetailsViewHolder(view);
            default:
                view = LayoutInflater.from(context).inflate(R.layout.list_episode, parent, false);
                return new EpisodeViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position)
    {
        Episode episode = episodes.get(position);

        if (vh instanceof DetailsViewHolder)
        {
            DetailsViewHolder holder = (DetailsViewHolder) vh;

            holder.title.setText(channel.getTitle());
            holder.rating.setText(channel.getRating());
            holder.description.setText(channel.getDescription());
            holder.favourize.setOnClickListener(callback::onFavourize);
            Drawable icon = ContextCompat.getDrawable(context, favourized ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
            holder.favourize.setImageDrawable(icon);
            Glide.with(context).load(channel.getImage()).placeholder(R.drawable.glide_placeholder)
                    .error(R.drawable.glide_error).into(holder.image);
        }
        else if (vh instanceof EpisodeViewHolder)
        {
            EpisodeViewHolder holder = (EpisodeViewHolder) vh;

            holder.title.setText(episode.getTitle());
            holder.subtitle.setText(episode.getDescription() != null ? episode.getDescription() : "");
            holder.rating.setText(episode.getRating());
            int count = Integer.parseInt(episode.getVotes());
            String text = context.getResources().getQuantityString(R.plurals.count_votes_format, count, count);
            holder.votes.setText(text);
            holder.date.setText(DateUtils.createDateString(context, episode.getDate()));
            holder.play.setOnClickListener(callback::onPlayPauseEpisode);
        }
    }

    public void showPlaySymbol(ImageButton button, boolean play)
    {
        button.setImageDrawable(play ? ContextCompat.getDrawable(context, R.drawable.ic_play_black) :
                ContextCompat.getDrawable(context, R.drawable.ic_pause_black));
    }

    @Override
    public int getItemCount()
    {
        return episodes.size();
    }

    public void onEpisodesLoaded(List<Episode> episodes)
    {
        this.episodes = episodes;
        notifyDataSetChanged();
    }

    public void setFavourized(boolean favourized)
    {
        this.favourized = favourized;
    }

    @Override
    public int getItemViewType(int position)
    {
        switch (position)
        {
            case 0:
                return VIEWTYPE_DETAILS;
            default:
                return VIEWTYPE_EPISODE;
        }
    }

    class DetailsViewHolder extends RecyclerView.ViewHolder
    {
        @Bind(R.id.play) ImageButton play;
        @Bind(R.id.favourize) ImageButton favourize;
        @Bind(R.id.share) ImageButton share;
        @Bind(R.id.image) ImageView image;
        @Bind(R.id.title) TextView title;
        @Bind(R.id.rating) TextView rating;
        @Bind(R.id.description) TextView description;

        public DetailsViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class EpisodeViewHolder extends RecyclerView.ViewHolder
    {
        @Bind(R.id.title) TextView title;
        @Bind(R.id.description) TextView subtitle;
        @Bind(R.id.rating) TextView rating;
        @Bind(R.id.votes) TextView votes;
        @Bind(R.id.date) TextView date;
        @Bind(R.id.play) ImageButton play;

        public EpisodeViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
