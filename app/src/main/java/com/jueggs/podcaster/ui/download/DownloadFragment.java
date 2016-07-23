package com.jueggs.podcaster.ui.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jueggs.helper.ItemTouchHelperCallback;
import com.jueggs.helper.SwipeCallback;
import com.jueggs.podcaster.App;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.data.PodcastContract;
import com.jueggs.podcaster.model.Episode;
import com.jueggs.podcaster.service.MediaService;
import com.jueggs.podcaster.ui.video.VideoActivity;
import com.jueggs.podcaster.utils.DaUtils;

import java.util.ArrayList;
import java.util.List;

import static com.jueggs.podcaster.service.MediaService.*;
import static com.jueggs.podcaster.utils.DaUtils.*;
import static com.jueggs.podcaster.utils.Util.*;
import static com.jueggs.utils.UIUtils.showViewWithFade;

public class DownloadFragment extends Fragment implements Callback, SwipeCallback
{
    public static final String STATE_PLAYING_POSITION = "state.playing.position";
    public static final String STATE_PLAYING = "state.playing";
    public static final String STATE_STARTED = "state.started";

    @Bind(R.id.recycler) RecyclerView recycler;
    @Bind(R.id.root) FrameLayout root;
    @Bind(R.id.empty) TextView empty;

    private DownloadAdapter adapter;
    private boolean started;
    private int playingPosition;
    private boolean playing;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
        {
            playingPosition = savedInstanceState.getInt(STATE_PLAYING_POSITION);
            playing = savedInstanceState.getBoolean(STATE_PLAYING);
            started = savedInstanceState.getBoolean(STATE_STARTED);
        }

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
        View view = inflater.inflate(R.layout.fragment_download, container, false);
        ButterKnife.bind(this, view);

        equipeRecycler(getContext(), recycler, adapter = new DownloadAdapter(getContext(), this));
        new ItemTouchHelper(new ItemTouchHelperCallback(ItemTouchHelperCallback.SWIPE_RIGHT, this, null)).attachToRecyclerView(recycler);

        if (savedInstanceState != null)
            recycler.smoothScrollToPosition(playingPosition);

        List<Episode> episodes = queryDownloadEpisodes(getContext());
        if (episodes != null)
            adapter.setEpisodes(episodes);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null)
            adapter.setPlayButtonPositionAndDrawable(playingPosition, playing);
    }

    @Override
    public void onPlayPause(ImageButton button, int position)
    {
        if (!started)
        {
            Episode episode = adapter.getEpisodes().get(position);
            String url = episode.getMediaLink();
            if (!TextUtils.isEmpty(url))
            {
                playingPosition = position;
                if (episode.getType().equals(PodcastContract.CHANNEL_TYPE_AUDIO_STRING))
                {
                    getActivity().startService(new Intent(getContext(), MediaService.class)
                            .putExtra(EXTRA_EPISODES, (ArrayList<Episode>) adapter.getEpisodes())
                            .putExtra(EXTRA_POSITION, position)
                            .putExtra(EXTRA_IMAGE, ""));
                }
                else if (episode.getType().equals(PodcastContract.CHANNEL_TYPE_VIDEO_STRING))
                    getContext().startActivity(new Intent(getContext(), VideoActivity.class).putExtra(VideoActivity.EXTRA_URI, url));
            }
        }
        else
            getContext().sendBroadcast(new Intent(MediaService.ACTION_PLAY_PAUSE));
    }

    @Override
    public void onStopEpisode()
    {
        getContext().sendBroadcast(new Intent(MediaService.ACTION_STOP));
    }

    @Override
    public void showEmptyView(boolean show)
    {
        showViewWithFade(root, empty, show);
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
                    playing = true;
                    break;
                case ACTION_PAUSED:
                    playing = false;
                    break;
                case ACTION_RESUMED:
                    playing = true;
                    break;
                case ACTION_STOPPED:
                    started = false;
                    playing = false;
                    break;
            }
            adapter.setPlayButtonPositionAndDrawable(playingPosition, playing);
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putInt(STATE_PLAYING_POSITION, playingPosition);
        outState.putBoolean(STATE_PLAYING, playing);
        outState.putBoolean(STATE_STARTED, started);
    }

    @Override
    public void onDestroy()
    {
        getContext().unregisterReceiver(actionReceiver);
        super.onDestroy();
    }

    @Override
    public void onSwiped(int position, RecyclerView.ViewHolder viewHolder, int direction)
    {
        deleteDownloadEpisode(getContext(), adapter.getEpisodes().get(position).getShowId());
        adapter.removeEpisode(position);
    }
}
