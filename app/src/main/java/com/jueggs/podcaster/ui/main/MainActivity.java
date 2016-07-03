package com.jueggs.podcaster.ui.main;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jueggs.podcaster.App;
import com.jueggs.podcaster.FlavorConfig;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.ui.playlists.manage.ManagePlaylistsActivity;

public class MainActivity extends AppCompatActivity
{
    private ViewPagerAdapter pagerAdapter;

    @Bind(R.id.viewPager) ViewPager viewPager;
    @Bind(R.id.tabs) TabLayout tabs;
    @Nullable @Bind(R.id.toolbar) Toolbar toolbar;
    @Nullable @Bind(R.id.container) FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState == null)
            App.getInstance().setTwoPane(container != null);

        if (App.getInstance().isTwoPane())
        {
            if (savedInstanceState == null)
            {
            }
        }
        else
        {

        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(pagerAdapter);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.menu_settings)
            return true;
        if (id == R.id.menu_playlists)
        {
            if (App.getInstance().isTwoPane())
            {

            }
            else
            {
                startActivity(new Intent(this, ManagePlaylistsActivity.class));
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
