package com.jueggs.podcaster.ui.channeldetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.jueggs.podcaster.App;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.data.repo.EpisodeRepository;
import com.jueggs.podcaster.model.Channel;
import com.jueggs.podcaster.model.Episode;

import java.util.List;

import static com.jueggs.podcaster.utils.Utils.*;

public class ChannelDetailFragment extends Fragment
{
    @Bind(R.id.recycler) RecyclerView recycler;

    private Channel channel;
    private ChannelDetailAdapter adapter;
    private EpisodeRepository repository = EpisodeRepository.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        channel = (Channel) getArguments().getSerializable(ChannelDetailActivity.EXTRA_CHANNEL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_channel_detail, container, false);
        ButterKnife.bind(this, view);

        equipeRecycler(getContext(), recycler, adapter = new ChannelDetailAdapter(getContext(), channel));

        repository.loadEpisodes(Integer.parseInt(channel.getChannelId()), App.LANGUAGE, this::onEpisodesLoaded);

        return view;
    }

    private void onEpisodesLoaded(List<Episode> episodes)
    {
        adapter.setEpisodes(episodes);
    }

    public static ChannelDetailFragment createInstance(Channel channel)
    {
        ChannelDetailFragment fragment = new ChannelDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ChannelDetailActivity.EXTRA_CHANNEL, channel);
        fragment.setArguments(args);
        return fragment;
    }
}
