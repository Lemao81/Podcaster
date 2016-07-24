package com.jueggs.podcaster.ui.charts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jueggs.podcaster.App;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.data.PodcastContract;
import com.jueggs.podcaster.data.repo.chart.ChartRepository;
import com.jueggs.podcaster.helper.Result;
import com.jueggs.podcaster.model.Channel;
import com.jueggs.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.jueggs.podcaster.data.PodcastContract.*;
import static com.jueggs.podcaster.utils.Util.*;
import static com.jueggs.utils.Utils.*;

public class ChartsFragment extends Fragment
{
    @Bind(R.id.recyclerAudio) RecyclerView recyclerAudio;
    @Bind(R.id.recyclerVideo) RecyclerView recyclerVideo;
    @Bind(R.id.empty) LinearLayout empty;

    private ChartRepository repository;
    private ChartsAdapter audioAdapter;
    private ChartsAdapter videoAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_charts, container, false);
        ButterKnife.bind(this, view);

        equipeRecycler(getContext(), recyclerAudio, audioAdapter = new ChartsAdapter(getActivity(), getActivity().getSupportFragmentManager()));
        equipeRecycler(getContext(), recyclerVideo, videoAdapter = new ChartsAdapter(getActivity(), getActivity().getSupportFragmentManager()));

        repository = ChartRepository.getInstance(getContext());

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        repository.loadCharts(App.LANGUAGE, this::onChartsLoaded);
    }

    private void onChartsLoaded(List<Channel> channels)
    {
        int state = readNetworkState(getContext());
        switch (state)
        {
            case Result.SUCCESS:
                empty.setVisibility(View.GONE);
                allocateChannels(channels);
                break;
            case Result.NO_NETWORK:
                showEmptyView(getContext(), empty, R.string.empty_no_network, R.drawable.ic_wifi_off);
                break;
            case Result.SERVER_DOWN:
                showEmptyView(getContext(), empty, R.string.empty_server_down, R.drawable.ic_server_down);
                break;
            case Result.INVALID_DATA:
                showEmptyView(getContext(), empty, R.string.empty_invalid_data, R.drawable.ic_invalid_data);
                break;
            case Result.UNKNOWN:
                showEmptyView(getContext(), empty, R.string.empty_unknown, R.drawable.ic_unknown_problem);
                break;
        }
    }

    private void allocateChannels(List<Channel> channels)
    {
        if (channels != null)
        {
            List<Channel> audios = new ArrayList<>();
            List<Channel> videos = new ArrayList<>();

            for (Channel channel : channels)
            {
                if (channel.getChannelType().equals(CHANNEL_TYPE_AUDIO_STRING))
                    audios.add(channel);
                else if (channel.getChannelType().equals(CHANNEL_TYPE_VIDEO_STRING))
                    videos.add(channel);
            }
            audioAdapter.setChannels(audios);
            videoAdapter.setChannels(videos);
        }
    }
}
