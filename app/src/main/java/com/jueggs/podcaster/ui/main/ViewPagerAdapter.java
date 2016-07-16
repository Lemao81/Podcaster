package com.jueggs.podcaster.ui.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.ui.category.CategoryFragment;
import com.jueggs.podcaster.ui.charts.ChartsFragment;
import com.jueggs.podcaster.ui.newcomer.NewcomerFragment;
import com.jueggs.podcaster.ui.playlists.tab.PlaylistFragment;

import java.util.ArrayList;
import java.util.List;

import static com.jueggs.podcaster.ui.main.MainActivity.*;

public class ViewPagerAdapter extends FragmentPagerAdapter
{
    public List<String> titles = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm, Context context)
    {
        super(fm);
        titles.add(context.getString(R.string.title_categories));
        titles.add(context.getString(R.string.title_charts));
        titles.add(context.getString(R.string.title_new));
        titles.add(context.getString(R.string.title_playlists));
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case TAB_CATEGORY:
                return new CategoryFragment();
            case TAB_CHARTS:
                return new ChartsFragment();
            case TAB_NEWCOMER:
                return new NewcomerFragment();
            case TAB_PLAYLISTS:
                return new PlaylistFragment();
            default:
                return new CategoryFragment();
        }
    }

    @Override
    public int getCount()
    {
        return titles.size();
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return titles.get(position);
    }
}
