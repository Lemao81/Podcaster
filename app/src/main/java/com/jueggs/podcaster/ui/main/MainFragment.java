package com.jueggs.podcaster.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jueggs.podcaster.FlavorConfig;
import com.jueggs.podcaster.R;

public class MainFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        FlavorConfig.initializeMobileAds(getContext().getApplicationContext().getApplicationContext(), view);
        return view;
    }
}
