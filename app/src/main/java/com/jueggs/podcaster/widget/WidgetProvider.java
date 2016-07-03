package com.jueggs.podcaster.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.RemoteViews;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.service.MediaService;

import static com.jueggs.podcaster.service.MediaService.*;
import static com.jueggs.utils.GraphicUtils.*;

public class WidgetProvider extends AppWidgetProvider
{
    public static final String WIDGET_TITLE = "widget_title";
    public static final String ENABLE_METH = "setEnabled";

    private String image;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        updateAppWidget(context, false, false);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);

        if (intent.hasExtra(MediaService.EXTRA_TITLE))
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            prefs.edit().putString(WIDGET_TITLE, intent.getStringExtra(MediaService.EXTRA_TITLE)).commit();
            image = intent.getStringExtra(MediaService.EXTRA_IMAGE);
        }

        switch (intent.getAction())
        {
            case ACTION_STARTED:
                updateAppWidget(context, true, true);
                break;
            case ACTION_PAUSED:
                updateAppWidget(context, true, false);
                break;
            case ACTION_RESUMED:
                updateAppWidget(context, true, true);
                break;
            case ACTION_STOPPED:
                updateAppWidget(context, false, false);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                prefs.edit().remove(WIDGET_TITLE);
                break;
        }
    }

    private void updateAppWidget(Context context, boolean started, boolean playing)
    {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] widgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class));

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

        if (started)
        {
            setEnabled(views, true, R.id.playpause, R.id.stop, R.id.previous, R.id.next);

            PendingIntent playPause = PendingIntent.getBroadcast(context, 0, new Intent(MediaService.ACTION_PLAY_PAUSE), 0);
            views.setOnClickPendingIntent(R.id.playpause, playPause);
            views.setImageViewResource(R.id.playpause, playing ? R.drawable.ic_widget_pause : R.drawable.ic_widget_play);

            PendingIntent stop = PendingIntent.getBroadcast(context, 0, new Intent(MediaService.ACTION_STOP), 0);
            views.setOnClickPendingIntent(R.id.stop, stop);

            PendingIntent prev = PendingIntent.getBroadcast(context, 0, new Intent(MediaService.ACTION_PREV), 0);
            views.setOnClickPendingIntent(R.id.previous, prev);

            PendingIntent next = PendingIntent.getBroadcast(context, 0, new Intent(MediaService.ACTION_NEXT), 0);
            views.setOnClickPendingIntent(R.id.next, next);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String defaultText = String.format("<%s>", context.getString(R.string.no_title));
            views.setTextViewText(R.id.title, prefs.getString(WIDGET_TITLE, defaultText));

            if (!TextUtils.isEmpty(image))
                Glide.with(context).load(image).placeholder(R.drawable.glide_placeholder).error(R.drawable.glide_error)
                        .into(new SimpleTarget<GlideDrawable>()
                        {
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation)
                            {
                                Bitmap bitmap = convertDrawableToBitmap(resource);
                                views.setImageViewBitmap(R.id.image, bitmap);

                                for (int widgetId : widgetIds)
                                    appWidgetManager.updateAppWidget(widgetId, views);
                            }
                        });
        }
        else
        {
            views.setTextViewText(R.id.title, "");
            setEnabled(views, false, R.id.playpause, R.id.stop, R.id.previous, R.id.next);
            views.setImageViewResource(R.id.image, R.drawable.glide_placeholder);
        }

        for (int widgetId : widgetIds)
            appWidgetManager.updateAppWidget(widgetId, views);
    }

    private void setEnabled(RemoteViews rv, boolean enable, int... views)
    {
        for (int view : views)
            rv.setBoolean(view, ENABLE_METH, enable);
    }
}

