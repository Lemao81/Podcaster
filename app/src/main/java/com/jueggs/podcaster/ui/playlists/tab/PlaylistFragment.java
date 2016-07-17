package com.jueggs.podcaster.ui.playlists.tab;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.data.db.PlaylistColumns;
import com.jueggs.podcaster.data.db.PlaylistsProvider;
import com.jueggs.podcaster.model.Channel;

import java.util.ArrayList;
import java.util.List;

import static com.jueggs.podcaster.utils.DaUtils.*;
import static com.jueggs.podcaster.utils.Util.*;
import static com.jueggs.utils.UIUtils.*;
import static com.jueggs.utils.Utils.*;

public class PlaylistFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    public static final String STATE_CURRENT_CHANNELS = "state.current.channels";
    public static final String STATE_CHANNELS_SHOWN = "state.channels.shown";
    public static final int LOADER_ID = 1;

    @Bind(R.id.root) FrameLayout root;
    @Bind(R.id.list) ListView list;
    @Bind(R.id.recycler) RecyclerView recycler;
    @Bind(R.id.navBack) FloatingActionButton navBack;

    private SimpleCursorAdapter playlistAdapter;
    private PlaylistChannelAdapter channelAdapter;
    private List<Channel> currentChannels;
    private boolean channelsShown;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
        {
            currentChannels = (List<Channel>) savedInstanceState.getSerializable(STATE_CURRENT_CHANNELS);
            channelsShown = savedInstanceState.getBoolean(STATE_CHANNELS_SHOWN);
        }

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        ButterKnife.bind(this, view);

        list.setAdapter(playlistAdapter = new SimpleCursorAdapter(getContext(), android.R.layout.simple_list_item_1, null,
                new String[]{PlaylistColumns.NAME}, new int[]{android.R.id.text1}, 0));
        list.setOnItemClickListener(this::onItemClick);

        navBack.setOnClickListener(this::onNavigateBack);

        equipeRecycler(getContext(), recycler, channelAdapter = new PlaylistChannelAdapter(getActivity(), getActivity().getSupportFragmentManager()));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            channelAdapter.setChannels(currentChannels);
            if (channelsShown)
                showChannels();
        }
    }

    private void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Cursor cursor = playlistAdapter.getCursor();
        cursor.moveToPosition(position);
        String playlist = cursor.getString(PlaylistColumns.ProjectionCompleteIndices.NAME);
        List<Channel> channels = queryChannel(getContext(), playlist);
        if (hasElements(channels))
        {
            channelAdapter.setChannels(channels);
            currentChannels = channels;
            channelsShown = true;
            showChannels();
        }
        else
            shortToast(getContext(), R.string.playlist_no_channels);
    }

    private void showChannels()
    {
        showViewWithFade(root, list, false);
        showViewWithFade(root, navBack, true);
        showViewWithFade(root, recycler, true);
    }

    private void onNavigateBack(View view)
    {
        showViewWithFade(root, navBack, false);
        showViewWithFade(root, list, true);
        showViewWithFade(root, recycler, false);
        channelsShown = false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putSerializable(STATE_CURRENT_CHANNELS, (ArrayList) currentChannels);
        outState.putBoolean(STATE_CHANNELS_SHOWN, channelsShown);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        Uri uri = PlaylistsProvider.Playlist.BASE_URI;
        return new CursorLoader(getContext(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        playlistAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        playlistAdapter.swapCursor(null);
    }
}
