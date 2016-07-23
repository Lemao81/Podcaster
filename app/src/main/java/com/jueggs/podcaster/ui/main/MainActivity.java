package com.jueggs.podcaster.ui.main;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.jueggs.podcaster.App;
import com.jueggs.podcaster.FlavorConfig;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.sync.SyncAdapter;
import com.jueggs.podcaster.ui.download.DownloadActivity;
import com.jueggs.podcaster.ui.download.DownloadFragment;
import com.jueggs.podcaster.ui.playlists.manage.ManagePlaylistsActivity;
import com.jueggs.podcaster.ui.playlists.manage.ManagePlaylistsFragment;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String EXTRA_TAB = "com.jueggs.podcaster.EXTRA_TAB";
    public static final int TAB_CATEGORY = 0;
    public static final int TAB_CHARTS = 1;
    public static final int TAB_NEWCOMER = 2;
    public static final int TAB_PLAYLISTS = 3;

    private Account account;

    @Nullable @Bind(R.id.toolbar) Toolbar toolbar;
    @Nullable @Bind(R.id.container) FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        App.getInstance().startTracking();

        if (savedInstanceState == null)
        {
            App.getInstance().setTwoPane(container != null);
            createAccount();
            getContentResolver().addPeriodicSync(account, getString(R.string.package_name), Bundle.EMPTY, SyncAdapter.SYNC_INTERVAL);
        }

        if (App.getInstance().isTwoPane() && savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new PlaceholderFragment()).commit();

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(EXTRA_TAB))
        {
            MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.mainFragment);
            fragment.setTab(getIntent().getIntExtra(EXTRA_TAB, TAB_CATEGORY));
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void createAccount()
    {
        account = new Account(getString(R.string.account_name), getString(R.string.package_name));
        AccountManager am = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        if (!am.addAccountExplicitly(account, null, null))
            Log.e(TAG, "could not add account for sync adapter");
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
        switch (item.getItemId())
        {
            case R.id.menu_settings:
                return true;
            case R.id.menu_playlists:
                if (App.getInstance().isTwoPane())
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new ManagePlaylistsFragment()).commit();
                else
                    startActivity(new Intent(this, ManagePlaylistsActivity.class),
                            ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
                return true;
            case R.id.menu_downloads:
                if (App.getInstance().isTwoPane())
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new DownloadFragment()).commit();
                else
                    startActivity(new Intent(this, DownloadActivity.class),
                            ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
