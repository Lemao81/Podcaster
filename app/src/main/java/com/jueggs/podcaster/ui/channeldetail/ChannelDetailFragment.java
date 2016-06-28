package com.jueggs.podcaster.ui.channeldetail;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jueggs.podcaster.App;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.data.repo.EpisodeRepository;
import com.jueggs.podcaster.model.Channel;
import com.jueggs.podcaster.model.Episode;
import com.jueggs.podcaster.service.MediaService;

import java.util.List;

import static com.jueggs.podcaster.utils.Util.*;

public class ChannelDetailFragment extends Fragment implements Playback
{
    @Bind(R.id.recycler) RecyclerView recycler;

    private Channel channel;
    private ChannelDetailAdapter adapter;
    private EpisodeRepository repository = EpisodeRepository.getInstance();
    private boolean bound;
    private MediaService mediaService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        channel = (Channel) getArguments().getSerializable(ChannelDetailActivity.EXTRA_CHANNEL);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        getActivity().bindService(new Intent(getContext(), MediaService.class), connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_channel_detail, container, false);
        ButterKnife.bind(this, view);

        equipeRecycler(getContext(), recycler, adapter = new ChannelDetailAdapter(getContext(), channel, this));

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

    @Override
    public void onStartEpisode(String url)
    {
//        boolean result = getActivity().bindService(new Intent(getContext(), MediaService.class)
//                .putExtra(MediaService.EXTRA_URL, url), connection, Context.BIND_AUTO_CREATE);
        getActivity().startService(new Intent(getContext(), MediaService.class).putExtra(MediaService.EXTRA_URL, url));
    }

    @Override
    public void onPauseEpisode()
    {
        if (mediaService != null)
            mediaService.pause();
    }

    @Override
    public void onResumeEpisode()
    {
        if (mediaService != null)
            mediaService.resume();
    }

    @Override
    public void onStopEpisode()
    {
        if (mediaService != null)
            mediaService.stop();
    }

    private ServiceConnection connection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            mediaService = ((MediaService.LocalBinder) service).getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            mediaService = null;
            bound = false;
        }
    };

    @Override
    public void onStop()
    {
        super.onStop();
        if (bound)
            getActivity().unbindService(connection);
        bound = false;
    }
}
