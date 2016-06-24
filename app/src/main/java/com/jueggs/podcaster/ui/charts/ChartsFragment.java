package com.jueggs.podcaster.ui.charts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jueggs.decorator.DividerDecoration;
import com.jueggs.podcaster.App;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.data.PodcastContract;
import com.jueggs.podcaster.data.repo.ChartRepository;
import com.jueggs.podcaster.model.Channel;

import java.util.ArrayList;
import java.util.List;

import static com.jueggs.podcaster.data.PodcastContract.*;

public class ChartsFragment extends Fragment
{
    @Bind(R.id.recyclerAudio) RecyclerView recyclerAudio;
    @Bind(R.id.recyclerVideo) RecyclerView recyclerVideo;

    private ChartRepository repository = ChartRepository.getInstance();
    private ChartsAdapter audioAdapter;
    private ChartsAdapter videoAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_charts, container, false);
        ButterKnife.bind(this, view);

        recyclerAudio.setAdapter(audioAdapter = new ChartsAdapter(getContext()));
        recyclerAudio.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerAudio.addItemDecoration(new DividerDecoration(getContext(), R.drawable.divider));

        recyclerVideo.setAdapter(videoAdapter = new ChartsAdapter(getContext()));
        recyclerVideo.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerVideo.addItemDecoration(new DividerDecoration(getContext(), R.drawable.divider));

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
        if (channels != null)
        {
            List<Channel> audios = new ArrayList<>();
            List<Channel> videos = new ArrayList<>();

            for (Channel channel : channels)
            {
                if (channel.getChannelType().equals(CHANNEL_TYPE_AUDIO))
                    audios.add(channel);
                else if (channel.getChannelType().equals(CHANNEL_TYPE_VIDEO))
                    videos.add(channel);
            }
            audioAdapter.setChannels(audios);
            videoAdapter.setChannels(videos);
        }
    }
}
