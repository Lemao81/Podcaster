package com.jueggs.podcaster.ui.channeldetail;

import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
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
import static com.jueggs.podcaster.utils.Util.*;
import static com.jueggs.utils.UIUtils.*;

public class ChannelDetailFragment extends Fragment implements Playback
{
    @Bind(R.id.recycler) RecyclerView recycler;
    @Bind(R.id.fabPlayPause) FloatingActionButton fabPlayPause;
    @Bind(R.id.fabStop) FloatingActionButton fabStop;
    @Bind(R.id.console) LinearLayout console;
    @Bind(R.id.root) FrameLayout root;

    private Channel channel;
    private ChannelDetailAdapter adapter;
    private EpisodeRepository repository = EpisodeRepository.getInstance();
    private boolean bound;
    private MediaService mediaService;
    private boolean started;
    private boolean playing;
    private View currentEpisodePlayButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        channel = (Channel) getArguments().getSerializable(ChannelDetailActivity.EXTRA_CHANNEL);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        getActivity().bindService(new Intent(getContext(), MediaService.class), connection, Context.BIND_AUTO_CREATE);

        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(ACTION_STOP);
        getContext().registerReceiver(actionReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_channel_detail, container, false);
        ButterKnife.bind(this, view);

        equipeRecycler(getContext(), recycler, adapter = new ChannelDetailAdapter(getContext(), channel, this));

        fabPlayPause.setOnClickListener(this::onPlayPauseEpisode);
        fabStop.setOnClickListener(this::onStopEpisode);

        repository.loadEpisodes(Integer.parseInt(channel.getChannelId()), App.LANGUAGE, this::onEpisodesLoaded);

        return view;
    }

    private void onEpisodesLoaded(List<Episode> episodes)
    {
        adapter.setEpisodes(episodes);
    }

    @Override
    public void onPlayPauseEpisode(View view)
    {
        Bundle data = (Bundle) view.getTag();
        String url = null, title = null;
        int position = 0;
        if (data != null)
        {
            url = data.getString(EXTRA_URL);
            title = data.getString(EXTRA_TITLE);
            position = data.getInt(EXTRA_POSITION);
        }
        if (!started && !TextUtils.isEmpty(url))
        {
            startEpisode(url, title, position);
            showPlaySymbol(view, false);
            currentEpisodePlayButton = view;
        }
        else if (started && playing)
        {
            pauseEpisode();
            showPlaySymbol(view, true);
        }
        else if (started && !playing)
        {
            resumeEpisode();
            showPlaySymbol(view, false);
        }
    }

    private void onStopEpisode(View view)
    {
        stopEpisode();
    }

    private void startEpisode(String url, String title, int position)
    {
        getActivity().startService(new Intent(getContext(), MediaService.class).putExtra(EXTRA_URL, url)
                .putExtra(EXTRA_TITLE, title).putExtra(EXTRA_POSITION, position));
        started = playing = true;
        showViewWithFade(root, console, true);
    }

    private void pauseEpisode()
    {
        if (mediaService != null)
            mediaService.pause();
        playing = false;
    }

    private void resumeEpisode()
    {
        if (mediaService != null)
            mediaService.resume();
        playing = true;
    }

    private void stopEpisode()
    {
        if (mediaService != null)
            mediaService.stop();
        started = playing = false;
        showViewWithFade(root, console, false);
        showPlaySymbol(currentEpisodePlayButton, true);
    }

    private void showPlaySymbol(View view, boolean play)
    {
        adapter.showPlaySymbol((ImageButton) view, play);
        fabPlayPause.setImageDrawable(play ? ContextCompat.getDrawable(getContext(), R.drawable.ic_play_white) :
                ContextCompat.getDrawable(getContext(), R.drawable.ic_pause_white));
    }

    private ServiceConnection connection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            mediaService = ((MediaService.LocalBinder) service).getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            mediaService = null;
            bound = false;
        }
    };

    @Override
    public void onStop()
    {
        super.onStop();
        if (bound)
            getActivity().unbindService(connection);
        bound = false;

        getContext().unregisterReceiver(actionReceiver);
    }

    private BroadcastReceiver actionReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().equals(ACTION_STOP))
            {
                stopEpisode();
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
