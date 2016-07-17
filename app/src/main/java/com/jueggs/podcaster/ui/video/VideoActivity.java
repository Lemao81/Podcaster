package com.jueggs.podcaster.ui.video;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.MediaController;
import android.widget.VideoView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jueggs.podcaster.R;

public class VideoActivity extends AppCompatActivity
{
    public static final String EXTRA_URI = "com.jueggs.podcaster.EXTRA_URI";

    @Bind(R.id.video) VideoView video;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);

        MediaController controller = new MediaController(this);
        controller.setAnchorView(video);
        video.setMediaController(controller);

        String url = getIntent().getStringExtra(EXTRA_URI);
        if(TextUtils.isEmpty(url))
            finish();

        Uri uri = Uri.parse(url);
        video.setVideoURI(uri);
        video.start();
    }
}
