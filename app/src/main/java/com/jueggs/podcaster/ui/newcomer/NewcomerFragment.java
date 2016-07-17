package com.jueggs.podcaster.ui.newcomer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ToggleButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jueggs.podcaster.App;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.data.repo.NewcomerRepository;
import com.jueggs.podcaster.helper.Result;
import com.jueggs.podcaster.model.Channel;

import java.util.ArrayList;
import java.util.List;

import static com.jueggs.podcaster.data.PodcastContract.*;
import static com.jueggs.podcaster.utils.Util.*;

public class NewcomerFragment extends Fragment
{
    public static final String STATE_ID_CHECKED = "state.id.checked";
    public static final String STATE_CURRENT_CHANNELS = "state.current.channels";

    @Bind(R.id.toggleAudio) ToggleButton toggleAudio;
    @Bind(R.id.toggleVideo) ToggleButton toggleVideo;
    @Bind(R.id.recycler) RecyclerView recycler;
    @Bind(R.id.radio) RadioGroup radio;
    @Bind(R.id.empty) LinearLayout empty;

    private NewcomerRepository repository;
    private NewcomerAdapter adapter;
    private int idChecked;
    private List<Channel> currentChannels;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
        {
            idChecked = savedInstanceState.getInt(STATE_ID_CHECKED);
            currentChannels = (List<Channel>) savedInstanceState.getSerializable(STATE_CURRENT_CHANNELS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_newcomer, container, false);
        ButterKnife.bind(this, view);

        equipeRecycler(getContext(), recycler, adapter = new NewcomerAdapter(getActivity(), getActivity().getSupportFragmentManager()));
        repository = NewcomerRepository.getInstance(getContext());

        toggleAudio.setOnClickListener(this::onToggleType);
        toggleVideo.setOnClickListener(this::onToggleType);

        radio.setOnCheckedChangeListener(this::onRadioCheckedChanged);

        return view;
    }

    private void onRadioCheckedChanged(RadioGroup radio, int viewId)
    {
        for (int i = 0; i < radio.getChildCount(); i++)
        {
            ToggleButton button = (ToggleButton) radio.getChildAt(i);
            button.setChecked(button.getId() == viewId);
        }
    }

    private void onToggleType(View view)
    {
        radio.check(view.getId());
        idChecked = view.getId();

        if (view.getId() == R.id.toggleAudio)
            repository.loadNewcomer(App.LANGUAGE, CHANNEL_TYPE_AUDIO, this::onNewcomerLoaded);
        else
            repository.loadNewcomer(App.LANGUAGE, CHANNEL_TYPE_VIDEO, this::onNewcomerLoaded);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null)
        {
            repository.loadNewcomer(App.LANGUAGE, CHANNEL_TYPE_AUDIO, this::onNewcomerLoaded);
            radio.check(toggleAudio.getId());
            idChecked = toggleAudio.getId();
        }
        else
        {
            radio.check(idChecked);
            adapter.setChannels(currentChannels);
        }
    }

    public void onNewcomerLoaded(List<Channel> channels)
    {
        int state = readNetworkState(getContext());
        switch (state)
        {
            case Result.SUCCESS:
                empty.setVisibility(View.GONE);
                adapter.setChannels(channels);
                currentChannels = channels;
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

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putInt(STATE_ID_CHECKED, idChecked);
        outState.putSerializable(STATE_CURRENT_CHANNELS, (ArrayList) currentChannels);
    }
}
