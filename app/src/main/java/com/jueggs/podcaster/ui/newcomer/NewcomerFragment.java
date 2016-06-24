package com.jueggs.podcaster.ui.newcomer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.jueggs.podcaster.R;

public class NewcomerFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_newcomer, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
