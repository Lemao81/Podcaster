package com.jueggs.podcaster.ui.channeldetail;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.model.Channel;

public class ChannelDetailActivity extends AppCompatActivity
{
    public static final String EXTRA_CHANNEL = "com.jueggs.podcaster.EXTRA_CHANNEL";

    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment == null)
        {
            Channel channel = (Channel) getIntent().getSerializableExtra(EXTRA_CHANNEL);
            if (channel == null)
                finish();
            else
                getSupportFragmentManager().beginTransaction().replace(R.id.container, ChannelDetailFragment.createInstance(channel)).commit();
        }

    }

}
