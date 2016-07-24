package com.jueggs.podcaster.ui.video;

import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.service.MediaService;

public class VideoActivity extends AppCompatActivity
{
    public static final String EXTRA_URI = "com.jueggs.podcaster.EXTRA_URI";
    public static final String STATE_POSITION = "com.jueggs.podcaster.STATE_POSITION";
    public static final String FILE_URI_BASE = "file://";
    public static final String SEP = "/";

    @Bind(R.id.video) VideoView video;

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);

        MediaController controller = new MediaController(this);
        controller.setAnchorView(video);
        video.setMediaController(controller);

        String filePath = null;
        String url = null;
        if (getIntent().getExtras().containsKey(MediaService.EXTRA_FILE_PATH))
            filePath = FILE_URI_BASE + getIntent().getStringExtra(MediaService.EXTRA_FILE_PATH);
        else
            url = getIntent().getStringExtra(EXTRA_URI);
        if (TextUtils.isEmpty(url) && TextUtils.isEmpty(filePath))
            finish();

        Uri uri = Uri.parse(filePath != null ? filePath : url);
        video.setVideoURI(uri);
        video.start();

        if (savedInstanceState != null)
        {
            position = savedInstanceState.getInt(STATE_POSITION);
            video.seekTo(position);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
            else
            {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putInt(STATE_POSITION, video.getCurrentPosition());
    }
}
