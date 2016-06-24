package com.jueggs.podcaster.ui.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.ui.category.CategoryFragment;
import com.jueggs.podcaster.ui.charts.ChartsFragment;
import com.jueggs.podcaster.ui.newcomer.NewcomerFragment;
import com.jueggs.podcaster.ui.playlist.PlaylistFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter
{
    public List<String> titles=new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm,Context context)
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
            case 0:
                return new CategoryFragment();
            case 1:
                return new ChartsFragment();
            case 2:
                return new NewcomerFragment();
            case 3:
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

//        switch (position)
//        {
//            case 0:
//                return "SECTION 1";
//            case 1:
//                return "SECTION 2";
//            case 2:
//                return "SECTION 3";
//        }
//        return null;
    }
}
