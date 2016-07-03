package com.jueggs.podcaster.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.model.Episode;
import com.jueggs.podcaster.ui.main.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.jueggs.utils.UIUtils.*;

public class MediaService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        AudioManager.OnAudioFocusChangeListener
{
    public static final String TAG = MediaService.class.getSimpleName();
    public static final String TAG_WIFILOCK = "com.jueggs.podcaster.TAG_WIFILOCK";
    public static final int NOTIFICATION_ID = 1;

    public static final String EXTRA_TITLE = "com.jueggs.podcaster.EXTRA_TITLE";
    public static final String EXTRA_IMAGE = "com.jueggs.podcaster.EXTRA_IMAGE";
    public static final String EXTRA_POSITION = "com.jueggs.podcaster.EXTRA_POSITION";
    public static final String EXTRA_EPISODES = "com.jueggs.podcaster.EXTRA_EPISODES";

    public static final String ACTION_PLAY_PAUSE = "com.jueggs.podcaster.ACTION_PLAY_PAUSE";
    public static final String ACTION_STOP = "com.jueggs.podcaster.ACTION_STOP";
    public static final String ACTION_PREV = "com.jueggs.podcaster.ACTION_PREV";
    public static final String ACTION_NEXT = "com.jueggs.podcaster.ACTION_NEXT";

    public static final String ACTION_STARTED = "com.jueggs.podcaster.ACTION_STARTED";
    public static final String ACTION_PAUSED = "com.jueggs.podcaster.ACTION_PAUSED";
    public static final String ACTION_RESUMED = "com.jueggs.podcaster.ACTION_RESUMED";
    public static final String ACTION_STOPPED = "com.jueggs.podcaster.ACTION_STOPPED";

    private MediaPlayer player;
    private WifiManager.WifiLock wifiLock;
    private AudioManager am;
    private String image;
    private List<Episode> episodes;
    private int position;
    private Episode playing;

    @Override
    public void onCreate()
    {
        super.onCreate();

        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(ACTION_PLAY_PAUSE);
        filter.addAction(ACTION_STOP);
        filter.addAction(ACTION_PREV);
        filter.addAction(ACTION_NEXT);
        registerReceiver(actionReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        episodes = (ArrayList<Episode>) intent.getSerializableExtra(EXTRA_EPISODES);
        position = intent.getIntExtra(EXTRA_POSITION, 0);
        image = intent.getStringExtra(EXTRA_IMAGE);
        playing = episodes.get(position);

        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        int result = am.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
        {
            shortToast(this, R.string.error_audio_focus);
            stopSelf();
        }

        initPlayer();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initPlayer()
    {
        player = new MediaPlayer();
        try
        {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(playing.getMediaLink());
            player.setOnPreparedListener(this);
            player.prepareAsync();
        }
        catch (IOException e)
        {
            Log.e(TAG, e.getMessage());
            shortToast(this, R.string.error_streaming_failed);
            releasePlayer();
            stopSelf();
        }

        PendingIntent content = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent stop = PendingIntent.getBroadcast(this, 1, new Intent(ACTION_STOP), 0);

        Notification notification = new Notification.Builder(this)
                .setContentTitle(getTitleText())
                .setContentText(String.format(getString(R.string.notification_episode_format), position + 1))
                .setContentIntent(content)
                .setTicker(getTitleText())
                .setSmallIcon(R.drawable.ic_notification)
                .addAction(R.drawable.ic_stop_black, getString(R.string.notification_stop), stop)
                .build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public void onAudioFocusChange(int focusChange)
    {
        switch (focusChange)
        {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (player == null)
                    initPlayer();
                else if (!player.isPlaying())
                    player.start();
                player.setVolume(1f, 1f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (player.isPlaying())
                    player.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (player.isPlaying())
                    player.setVolume(0.1f, 0.1f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                stopForeground(true);
                releasePlayer();
                break;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp)
    {
        player.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        wifiLock = ((WifiManager) getSystemService(WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, TAG_WIFILOCK);
        player.start();
        sendBroadcast(new Intent(ACTION_STARTED).putExtra(EXTRA_TITLE, getTitleText()).putExtra(EXTRA_IMAGE, image));
    }

    private String getTitleText()
    {
        return TextUtils.isEmpty(playing.getTitle()) ? String.format("<%s>", getString(R.string.no_title)) : playing.getTitle();
    }

    private void playNext()
    {
        if (player != null)
        {
            if (player.isPlaying())
                player.stop();
            player.reset();
            try
            {
                player.setDataSource(playing.getMediaLink());
                player.prepareAsync();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void releasePlayer()
    {
        if (player != null)
        {
            if (player.isPlaying())
                player.stop();
            player.release();
            player = null;
            if (wifiLock != null && wifiLock.isHeld())
                wifiLock.release();
            if (am != null)
                am.abandonAudioFocus(this);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra)
    {
        shortToast(this, R.string.error_media_player);
        return false;
    }

    @Override
    public void onDestroy()
    {
        stopForeground(true);
        releasePlayer();
        sendBroadcast(new Intent(ACTION_STOPPED));
        unregisterReceiver(actionReceiver);
    }

    private BroadcastReceiver actionReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            switch (intent.getAction())
            {
                case ACTION_PLAY_PAUSE:
                    if (player != null)
                    {
                        if (player.isPlaying())
                        {
                            player.pause();
                            sendBroadcast(new Intent(ACTION_PAUSED));
                        }
                        else
                        {
                            player.start();
                            sendBroadcast(new Intent(ACTION_RESUMED));
                        }
                    }
                    break;
                case ACTION_STOP:
                    releasePlayer();
                    stopForeground(true);
                    stopSelf();
                    break;
                case ACTION_PREV:
                    if (position > 0)
                    {
                        playing = episodes.get(--position);
                        playNext();
                    }
                    break;
                case ACTION_NEXT:
                    if (position < episodes.size() - 1)
                    {
                        playing = episodes.get(++position);
                        playNext();
                    }
                    break;
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
