package com.jueggs.podcaster.ui.playlists.tab;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.model.Channel;
import com.jueggs.podcaster.utils.DaUtils;
import com.jueggs.podcaster.utils.Util;
import com.jueggs.utils.UIUtils;
import com.jueggs.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.jueggs.podcaster.utils.DaUtils.*;
import static com.jueggs.podcaster.utils.Util.*;
import static com.jueggs.utils.UIUtils.*;
import static com.jueggs.utils.Utils.*;

public class PlaylistFragment extends Fragment
{
    @Bind(R.id.root) FrameLayout root;
    @Bind(R.id.list) ListView list;
    @Bind(R.id.recycler) RecyclerView recycler;
    @Bind(R.id.navBack) FloatingActionButton navBack;

    private List<String> playlists;
    private PlaylistAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        ButterKnife.bind(this, view);

        playlists = queryPlaylists(getContext());
        list.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,
                playlists != null ? playlists : new ArrayList()));
        list.setOnItemClickListener(this::onItemClick);

        navBack.setOnClickListener(this::onNavigateBack);

        equipeRecycler(getContext(), recycler, adapter = new PlaylistAdapter(getContext(), getActivity().getSupportFragmentManager()));

        return view;
    }

    private void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        String playlist = playlists.get(position);
        List<Channel> channels = queryChannel(getContext(), playlist);
        if (hasElements(channels))
        {
            adapter.setChannels(channels);
            showViewWithFade(root, navBack, true);
            showViewWithFade(root, list, false);
            showViewWithFade(root, recycler, true);
        }
        else
            shortToast(getContext(), R.string.playlist_no_channels);
    }

    private void onNavigateBack(View view)
    {
        showViewWithFade(root, navBack, false);
        showViewWithFade(root, list, true);
        showViewWithFade(root, recycler, false);
    }
}
