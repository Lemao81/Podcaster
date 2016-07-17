package com.jueggs.podcaster.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jueggs.podcaster.FlavorConfig;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.adapter.OnPageChangeListenerAdapter;

public class MainFragment extends Fragment
{
    public static final String STATE_CURRENT_PAGE = "STATE_CURRENT_PAGE";

    @Bind(R.id.viewPager) ViewPager viewPager;
    @Bind(R.id.tabs) TabLayout tabs;

    private ViewPagerAdapter pagerAdapter;
    private int currentPage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            currentPage = savedInstanceState.getInt(STATE_CURRENT_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        FlavorConfig.initializeMobileAds(getContext().getApplicationContext().getApplicationContext(), view);

        pagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), getContext());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(pageChangeListener);
        tabs.setupWithViewPager(viewPager);

        if (currentPage != 0)
            viewPager.setCurrentItem(currentPage);

        return view;
    }

    private OnPageChangeListenerAdapter pageChangeListener = new OnPageChangeListenerAdapter()
    {
        @Override
        public void onPageSelected(int position)
        {
            currentPage = position;
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putInt(STATE_CURRENT_PAGE, currentPage);
    }
}
