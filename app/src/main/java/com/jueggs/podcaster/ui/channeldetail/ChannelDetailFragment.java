package com.jueggs.podcaster.ui.channeldetail;

import android.content.*;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.android.gms.analytics.HitBuilders;
import com.jueggs.podcaster.App;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.data.repo.EpisodeRepository;
import com.jueggs.podcaster.helper.Result;
import com.jueggs.podcaster.model.Channel;
import com.jueggs.podcaster.model.Episode;
import com.jueggs.podcaster.service.MediaService;

import java.util.ArrayList;
import java.util.List;

import static com.jueggs.podcaster.service.MediaService.*;
import static com.jueggs.podcaster.utils.DaUtils.*;
import static com.jueggs.podcaster.utils.Util.*;
import static com.jueggs.utils.UIUtils.*;
import static com.jueggs.utils.Utils.*;

public class ChannelDetailFragment extends Fragment implements Callback
{
    @Bind(R.id.recycler) RecyclerView recycler;
    @Bind(R.id.fabPlayPause) FloatingActionButton fabPlayPause;
    @Bind(R.id.fabStop) FloatingActionButton fabStop;
    @Bind(R.id.console) LinearLayout console;
    @Bind(R.id.root) FrameLayout root;
    @Bind(R.id.empty) LinearLayout empty;

    private Channel channel;
    private ChannelDetailAdapter adapter;
    private EpisodeRepository repository;
    private boolean started;
    private ImageButton playButton;
    private boolean favourized;
    private int playingPosition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        channel = (Channel) getArguments().getSerializable(ChannelDetailActivity.EXTRA_CHANNEL);
        favourized = countChannel(getContext(), channel) > 0;

        repository = EpisodeRepository.getInstance(getContext());

        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(ACTION_STARTED);
        filter.addAction(ACTION_PAUSED);
        filter.addAction(ACTION_RESUMED);
        filter.addAction(ACTION_STOPPED);
        getContext().registerReceiver(actionReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_channel_detail, container, false);
        ButterKnife.bind(this, view);

        equipeRecycler(getContext(), recycler, adapter = new ChannelDetailAdapter(getContext(), channel, this, favourized));

        fabPlayPause.setOnClickListener(this::onPlayPauseByFab);
        fabStop.setOnClickListener(this::onStopEpisode);

        repository.loadEpisodes(Integer.parseInt(channel.getChannelId()), App.LANGUAGE, this::onEpisodesLoaded);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        recycler.post(() -> getActivity().supportStartPostponedEnterTransition());
    }

    @Override
    public void onPlayPauseByImageButton(ImageButton button, int position)
    {
        if (!started)
        {
            String url = adapter.getEpisodes().get(position).getMediaLink();
            if (!TextUtils.isEmpty(url))
            {
                playButton = button;
                playingPosition = position;
                getActivity().startService(new Intent(getContext(), MediaService.class)
                        .putExtra(EXTRA_EPISODES, (ArrayList<Episode>) channel.getEpisodes())
                        .putExtra(EXTRA_POSITION, position)
                        .putExtra(EXTRA_IMAGE, channel.getImage()));
                App.getInstance().getTracker().send(new HitBuilders.EventBuilder()
                        .setCategory(App.TRACK_CAT_CHANNEL)
                        .setAction(App.TRACK_ACTION_PLAYED)
                        .setLabel(channel.getChannelId()).build());
            }
        }
        else
            getContext().sendBroadcast(new Intent(MediaService.ACTION_PLAY_PAUSE));
    }

    private void onPlayPauseByFab(View view)
    {
        getContext().sendBroadcast(new Intent(MediaService.ACTION_PLAY_PAUSE));
    }

    private void onEpisodesLoaded(List<Episode> episodes)
    {
        int state = readNetworkState(getContext());
        switch (state)
        {
            case Result.SUCCESS:
                empty.setVisibility(View.GONE);
                channel.setEpisodes(episodes);
                adapter.setEpisodes(episodes);
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
    public void onFavourize(View view)
    {
        if (favourized)
        {
            int deleted = deleteChannel(getContext(), channel);
            if (deleted > 0)
            {
                adapter.setFavourized(favourized = false);
                adapter.notifyItemChanged(0);
                shortToast(getContext(), R.string.msg_channel_deleted);
            }
        }
        else
        {
            List<String> playlists = queryPlaylists(getContext());
            if (hasElements(playlists))
                showSelectionListDialog(getContext(), root, R.string.playlist_selection_title, R.string.playlist_input_cancel, playlists,
                        this::onPlaylistSelected);
            else
                shortToast(getContext(), R.string.playlist_no_playlists);
        }
    }

    private void onPlaylistSelected(String playlist)
    {
        Uri result = insertChannel(getContext(), channel, playlist);
        if (result != null)
        {
            adapter.setFavourized(favourized = true);
            adapter.notifyItemChanged(0);
            shortToast(getContext(), R.string.msg_channel_added);
        }
    }

    private void onStopEpisode(View view)
    {
        getContext().sendBroadcast(new Intent(MediaService.ACTION_STOP));
    }

    @Override
    public void onDestroy()
    {
        getContext().unregisterReceiver(actionReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver actionReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            switch (intent.getAction())
            {
                case ACTION_STARTED:
                    started = true;
                    showViewWithFade(root, console, true);
                    showPlaySymbol(false);
                    break;
                case ACTION_PAUSED:
                    showPlaySymbol(true);
                    break;
                case ACTION_RESUMED:
                    showPlaySymbol(false);
                    break;
                case ACTION_STOPPED:
                    started = false;
                    showViewWithFade(root, console, false);
                    showPlaySymbol(true);
                    break;
            }
        }
    };

    private void showPlaySymbol(boolean play)
    {
        Drawable drawable = play ? ContextCompat.getDrawable(getContext(), R.drawable.ic_play_black) :
                ContextCompat.getDrawable(getContext(), R.drawable.ic_pause_black);
        adapter.setPlayButtonDrawable(playingPosition,drawable);
        fabPlayPause.setImageDrawable(play ? ContextCompat.getDrawable(getContext(), R.drawable.ic_play_white) :
                ContextCompat.getDrawable(getContext(), R.drawable.ic_pause_white));
    }

    public static ChannelDetailFragment createInstance(Channel channel)
    {
        ChannelDetailFragment fragment = new ChannelDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ChannelDetailActivity.EXTRA_CHANNEL, channel);
        fragment.setArguments(args);
        return fragment;
    }
}
