package com.jueggs.podcaster.ui.download;

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
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.data.PodcastContract;
import com.jueggs.podcaster.model.Episode;

import java.util.ArrayList;
import java.util.List;

import static com.jueggs.podcaster.utils.DateUtils.*;
import static com.jueggs.utils.Utils.*;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder>
{
    private Context context;
    private List<Episode> episodes = new ArrayList<>();
    private Callback callback;
    private int playingPosition = INVALID_POSITION;
    private Drawable playingDrawable;

    public DownloadAdapter(Context context, Callback callback)
    {
        this.context = context;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.list_episode_download, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        Episode episode = episodes.get(position);

        holder.title.setText(episode.getTitle());
        holder.subtitle.setText(episode.getDescription() != null ? episode.getDescription() : "");
        holder.rating.setText(episode.getRating());
        int count = Integer.parseInt(episode.getVotes());
        String text = context.getResources().getQuantityString(R.plurals.count_votes_format, count, count);
        holder.votes.setText(text);
        holder.play.setOnClickListener(holder);
        holder.stop.setOnClickListener(holder);
        if (position == playingPosition)
            holder.play.setImageDrawable(playingDrawable);
        else
            holder.play.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play_black));
        if (episode.getType().equals(PodcastContract.CHANNEL_TYPE_AUDIO_STRING))
            holder.type.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.audio));
        else if (episode.getType().equals(PodcastContract.CHANNEL_TYPE_VIDEO_STRING))
            holder.type.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.video));
    }

    public void setEpisodes(List<Episode> episodes)
    {
        this.episodes = episodes;
        notifyDataSetChanged();
    }

    public List<Episode> getEpisodes()
    {
        return episodes;
    }

    public void setPlayButtonPositionAndDrawable(int position, boolean playing)
    {
        this.playingPosition = position;
        this.playingDrawable = playing ? ContextCompat.getDrawable(context, R.drawable.ic_pause_black) :
                ContextCompat.getDrawable(context, R.drawable.ic_play_black);
        notifyItemChanged(position);
    }

    public void removeEpisode(int position)
    {
        episodes.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount()
    {
        int count = episodes.size();
        callback.showEmptyView(count == 0);
        return count;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        @Bind(R.id.title) TextView title;
        @Bind(R.id.description) TextView subtitle;
        @Bind(R.id.rating) TextView rating;
        @Bind(R.id.votes) TextView votes;
        @Bind(R.id.playpause) ImageButton play;
        @Bind(R.id.stop) ImageButton stop;
        @Bind(R.id.episodeType) ImageView type;

        public ViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v)
        {
            int position = getAdapterPosition();

            switch (v.getId())
            {
                case R.id.playpause:
                    callback.onPlayPause(play, position);
                    break;
                case R.id.stop:
                    callback.onStopEpisode();
                    break;
            }

        }
    }
}
