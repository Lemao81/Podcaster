package com.jueggs.podcaster.ui.download;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.ui.playlists.manage.ManagePlaylistsFragment;

public class DownloadActivity extends AppCompatActivity
{
    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_playlists);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new DownloadFragment()).commit();
    }

}