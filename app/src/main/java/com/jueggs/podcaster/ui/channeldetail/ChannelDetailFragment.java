package com.jueggs.podcaster.ui.channeldetail;

import android.content.*;
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
import com.jueggs.podcaster.App;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.data.repo.EpisodeRepository;
import com.jueggs.podcaster.model.Channel;
import com.jueggs.podcaster.model.Episode;
import com.jueggs.podcaster.service.MediaService;

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

    private Channel channel;
    private ChannelDetailAdapter adapter;
    private EpisodeRepository repository = EpisodeRepository.getInstance();
    private boolean started;
    private View playButton;
    private boolean favourized;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        channel = (Channel) getArguments().getSerializable(ChannelDetailActivity.EXTRA_CHANNEL);
        favourized = countChannel(getContext(), channel) > 0;
    }

    @Override
    public void onStart()
    {
        super.onStart();

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

        fabPlayPause.setOnClickListener(this::onPlayPauseEpisode);
        fabStop.setOnClickListener(this::onStopEpisode);

        repository.loadEpisodes(Integer.parseInt(channel.getChannelId()), App.LANGUAGE, adapter::onEpisodesLoaded);

        return view;
    }

    @Override
    public void onPlayPauseEpisode(View view)
    {
        if (view.getId() == R.id.playpause && !started)
        {
            int position = recycler.getChildAdapterPosition((View) view.getParent().getParent().getParent());
            Episode episode = adapter.getEpisodes().get(position);
            String url = episode.getMediaLink();

            if (!TextUtils.isEmpty(url))
            {
                playButton = view;
                getActivity().startService(new Intent(getContext(), MediaService.class)
                        .putExtra(EXTRA_URL, url)
                        .putExtra(EXTRA_IMAGE, channel.getImage())
                        .putExtra(EXTRA_TITLE, episode.getTitle())
                        .putExtra(EXTRA_POSITION, position));
            }
        }
        else
            getContext().sendBroadcast(new Intent(MediaService.ACTION_PLAY_PAUSE));
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

    private void showPlaySymbol(View view, boolean play)
    {
        adapter.showPlaySymbol((ImageButton) view, play);
        fabPlayPause.setImageDrawable(play ? ContextCompat.getDrawable(getContext(), R.drawable.ic_play_white) :
                ContextCompat.getDrawable(getContext(), R.drawable.ic_pause_white));
    }

    @Override
    public void onStop()
    {
        super.onStop();
        getContext().unregisterReceiver(actionReceiver);
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
                    showPlaySymbol(playButton, false);
                    break;
                case ACTION_PAUSED:
                    showPlaySymbol(playButton, true);
                    break;
                case ACTION_RESUMED:
                    showPlaySymbol(playButton, false);
                    break;
                case ACTION_STOPPED:
                    started = false;
                    showViewWithFade(root, console, false);
                    showPlaySymbol(playButton, true);
                    break;
            }
        }
    };

    public static ChannelDetailFragment createInstance(Channel channel)
    {
        ChannelDetailFragment fragment = new ChannelDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ChannelDetailActivity.EXTRA_CHANNEL, channel);
        fragment.setArguments(args);
        return fragment;
    }
}
