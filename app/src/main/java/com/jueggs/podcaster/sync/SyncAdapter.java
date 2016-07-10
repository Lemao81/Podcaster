package com.jueggs.podcaster.sync;

import android.accounts.Account;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.*;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.jueggs.podcaster.App;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.data.PodcastContract;
import com.jueggs.podcaster.data.PodcastService;
import com.jueggs.podcaster.model.Channel;
import com.jueggs.podcaster.model.ChannelArrayRoot;
import com.jueggs.podcaster.ui.main.MainActivity;
import com.jueggs.utils.GraphicUtils;
import com.jueggs.utils.UIUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.jueggs.podcaster.data.PodcastContract.createPodcastService;
import static com.jueggs.utils.Utils.*;

public class SyncAdapter extends AbstractThreadedSyncAdapter
{
    public static final String TAG = SyncAdapter.class.getSimpleName();
    public static final long SYNC_INTERVAL = 24L * 60L * 60L;  //seconds per day
    public static final long DAY_IN_MILLIS = 24L * 60L * 60L * 1000L;
    public static final int NOTIFICATION_ID = 1;

    public SyncAdapter(Context context, boolean autoInitialize)
    {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean showNotification = Boolean.parseBoolean(prefs.getString(getContext().getString(R.string.pref_enable_notification), "true"));
        long lastNotification = prefs.getLong(getContext().getString(R.string.pref_last_notification), 0);

        if (showNotification && lastNotification + DAY_IN_MILLIS < System.currentTimeMillis())
        {
            PodcastService service = createPodcastService();

            try
            {
                ChannelArrayRoot root = service.loadNewcomer(App.LANGUAGE).execute().body();

                String dateNew;
                if (hasElements(root.getChannels()))
                    dateNew = root.getChannels().get(0).getDate().substring(0, 10);
                else
                    return;

                List<Channel> audios = new ArrayList<>();
                List<Channel> videos = new ArrayList<>();

                for (Channel channel : root.getChannels())
                {
                    if (!channel.getDate().substring(0, 10).equals(dateNew))
                        continue;
                    if (channel.getChannelType().equals(PodcastContract.CHANNEL_TYPE_AUDIO_STRING))
                        audios.add(channel);
                    else if (channel.getChannelType().equals(PodcastContract.CHANNEL_TYPE_VIDEO_STRING))
                        videos.add(channel);
                }

                if (!hasElements(audios) && !hasElements(videos))
                    return;

                String title = getContext().getString(R.string.notification_title);
                String content = getContext().getString(R.string.notification_content);

                NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
                style.setBigContentTitle(content);
                if (hasElements(audios))
                {
                    style.addLine(getContext().getString(R.string.title_audio) + ":");
                    for (Channel channel : audios)
                        style.addLine(channel.getChannelTitle());
                }
                if (hasElements(videos))
                {
                    style.addLine(getContext().getString(R.string.title_video) + ":");
                    for (Channel channel : videos)
                        style.addLine(channel.getChannelTitle());
                }

                Intent goTo = new Intent(getContext(), MainActivity.class).putExtra(MainActivity.EXTRA_TAB, MainActivity.TAB_NEWCOMER);
                PendingIntent contentPi = PendingIntent.getActivity(getContext(), 0, goTo, 0);

                Bitmap largeIcon = GraphicUtils.convertDrawableToBitmap(ContextCompat.getDrawable(getContext(), R.mipmap.logo));
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext())
                        .setSmallIcon(R.drawable.ic_radio)
                        .setLargeIcon(largeIcon)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setTicker(getContext().getString(R.string.notification_ticker))
                        .setContentIntent(contentPi)
                        .setAutoCancel(true)
                        .setStyle(style);

                NotificationManager nm = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                nm.notify(NOTIFICATION_ID, builder.build());

                prefs.edit().putLong(getContext().getString(R.string.pref_last_notification), System.currentTimeMillis()).commit();
            }
            catch (IOException e)
            {
                Log.e(TAG, e.getMessage());
            }
        }
    }
}
