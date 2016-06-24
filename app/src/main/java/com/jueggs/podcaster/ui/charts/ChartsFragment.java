package com.jueggs.podcaster.ui.charts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jueggs.podcaster.R;

public class ChartsFragment extends Fragment
{
    @Bind(R.id.recyclerAudio) RecyclerView recyclerAudio;
    @Bind(R.id.recyclerVideo) RecyclerView recyclerVideo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_charts, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
