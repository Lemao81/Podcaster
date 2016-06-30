package com.jueggs.podcaster.ui.newcomer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.ToggleButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jueggs.podcaster.App;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.data.repo.NewcomerRepository;

import static com.jueggs.podcaster.data.PodcastContract.*;
import static com.jueggs.podcaster.utils.Util.*;

public class NewcomerFragment extends Fragment
{
    @Bind(R.id.toggleAudio) ToggleButton toggleAudio;
    @Bind(R.id.toggleVideo) ToggleButton toggleVideo;
    @Bind(R.id.recycler) RecyclerView recycler;
    @Bind(R.id.radio) RadioGroup radio;

    private NewcomerRepository repository = NewcomerRepository.getInstance();
    private NewcomerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_newcomer, container, false);
        ButterKnife.bind(this, view);

        equipeRecycler(getContext(), recycler, adapter = new NewcomerAdapter(getContext(), getActivity().getSupportFragmentManager()));

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

        if (view.getId() == R.id.toggleAudio)
            repository.loadNewcomer(App.LANGUAGE, CHANNEL_TYPE_AUDIO, adapter::onNewcomerLoaded);
        else
            repository.loadNewcomer(App.LANGUAGE, CHANNEL_TYPE_VIDEO, adapter::onNewcomerLoaded);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        repository.loadNewcomer(App.LANGUAGE, CHANNEL_TYPE_AUDIO, adapter::onNewcomerLoaded);
        radio.check(toggleAudio.getId());
    }
}
