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
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.ui.main.MainActivity;

import java.io.IOException;

public class MediaService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        AudioManager.OnAudioFocusChangeListener
{
    public static final String TAG = MediaService.class.getSimpleName();
    public static final String TAG_WIFILOCK = "com.jueggs.podcaster.TAG_WIFILOCK";
    public static final int NOTIFICATION_ID = 1;

    public static final String EXTRA_URL = "com.jueggs.podcaster.EXTRA_URL";
    public static final String EXTRA_TITLE = "com.jueggs.podcaster.EXTRA_TITLE";
    public static final String EXTRA_POSITION = "com.jueggs.podcaster.EXTRA_POSITION";

    public static final String ACTION_STOP = "com.jueggs.podcaster.ACTION_STOP";

    private MediaPlayer player;
    private WifiManager.WifiLock wifiLock;
    private AudioManager am;
    private Intent intent;
    private LocalBinder binder = new LocalBinder();

    @Override
    public void onCreate()
    {
        super.onCreate();

        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(ACTION_STOP);
        registerReceiver(actionReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        this.intent = intent;

        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        int result = am.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        initPlayer();

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
        {
            showMessage(R.string.error_audio_focus);
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
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

    private void initPlayer()
    {
        player = new MediaPlayer();
        String url = intent.getStringExtra(EXTRA_URL);
        try
        {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(url);
            player.setOnPreparedListener(this);
            player.prepareAsync();
        }
        catch (IOException e)
        {
            Log.e(TAG, e.getMessage());
            showMessage(R.string.error_streaming_failed);
            releasePlayer();
            stopSelf();
        }

        PendingIntent content = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent stop = PendingIntent.getBroadcast(this, 1, new Intent(ACTION_STOP), 0);

        String title = intent.getStringExtra(EXTRA_TITLE);
        String titleText = TextUtils.isEmpty(title) ? String.format("<%s>", getString(R.string.notification_no_title)) : title;
        Notification notification = new Notification.Builder(this)
                .setContentTitle(titleText)
                .setContentText(String.format(getString(R.string.notification_episode_format), intent.getIntExtra(EXTRA_POSITION, 0) + 1))
                .setContentIntent(content)
                .setTicker(titleText)
                .setSmallIcon(R.drawable.ic_notification)
                .addAction(R.drawable.ic_stop_black, getString(R.string.notification_stop), stop)
                .build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public void onPrepared(MediaPlayer mp)
    {
        player.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        wifiLock = ((WifiManager) getSystemService(WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, TAG_WIFILOCK);
        player.start();
    }

    private void releasePlayer()
    {
        if (player != null)
        {
            if (player.isPlaying())
                player.stop();
            player.release();
            player = null;
            if (wifiLock.isHeld())
                wifiLock.release();
            am.abandonAudioFocus(this);
        }
    }

    public void pause()
    {
        if (player != null && player.isPlaying())
            player.pause();
    }

    public void resume()
    {
        if (player != null && !player.isPlaying())
            player.start();
    }

    public void stop()
    {
        releasePlayer();
        stopForeground(true);
        stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra)
    {
        showMessage(R.string.error_media_player);
        return false;
    }

    private void showMessage(int stringId)
    {
        Toast.makeText(this, getString(stringId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy()
    {
        stopForeground(true);
        releasePlayer();
        unregisterReceiver(actionReceiver);
    }

    private BroadcastReceiver actionReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().equals(ACTION_STOP))
            {
                stop();
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent)
    {
        return binder;
    }

    public class LocalBinder extends Binder
    {
        public MediaService getService()
        {
            return MediaService.this;
        }
    }
}
