package com.jueggs.podcaster.ui.category;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jueggs.decorator.DividerDecoration;
import com.jueggs.podcaster.App;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.data.repo.CategoryRepository;
import com.jueggs.podcaster.data.repo.ChannelRepository;
import com.jueggs.podcaster.model.Category;
import com.jueggs.podcaster.model.Channel;
import com.jueggs.podcaster.ui.channeldetail.ChannelDetailActivity;
import com.jueggs.podcaster.utils.Utils;

import java.util.List;

import static com.jueggs.podcaster.utils.Utils.*;

public class CategoryFragment extends Fragment implements Callback
{
    @Bind(R.id.root) FrameLayout root;
    @Bind(R.id.recycler) RecyclerView recycler;
    @Bind(R.id.fab) FloatingActionButton fab;

    private CategoryRepository categoryRepository = CategoryRepository.getInstance();
    private ChannelRepository channelRepository = ChannelRepository.getInstance();
    private CategoryAdapter adapter;
    private Fade fadeIn;
    private Fade fadeOut;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        ButterKnife.bind(this, view);

        equipeRecycler(getContext(), recycler, adapter = new CategoryAdapter(getContext(), getActivity().getSupportFragmentManager(), this));

        fab.setOnClickListener(this::onNavigateBack);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            fadeIn = new Fade(Fade.IN);
            fadeOut = new Fade(Fade.OUT);
        }

        return view;
    }

    private void onNavigateBack(View view)
    {
        adapter.navigateBack();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        categoryRepository.loadCategories(App.LANGUAGE, this::onCategoriesLoaded);
    }

    @Override
    public void onNavigationLevelChanged(int level)
    {
        showFab(level != 0);
    }

    private void showFab(boolean show)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            TransitionManager.beginDelayedTransition(root, show ? fadeIn : fadeOut);
        fab.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onCategorySelected(Category category)
    {
        channelRepository.loadChannels(Integer.parseInt(category.getId()), App.LANGUAGE, this::onChannelsLoaded);
    }

    private void onCategoriesLoaded(List<Category> categories)
    {
        adapter.setCategories(categories);
    }

    private void onChannelsLoaded(List<Channel> channels)
    {
        adapter.setChannels(channels);
    }
}
