package com.jueggs.podcaster.ui.category;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jueggs.podcaster.App;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.data.repo.category.CategoryRepository;
import com.jueggs.podcaster.data.repo.channel.ChannelRepository;
import com.jueggs.podcaster.helper.Result;
import com.jueggs.podcaster.model.Category;
import com.jueggs.podcaster.model.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static com.jueggs.podcaster.utils.Util.*;
import static com.jueggs.utils.UIUtils.*;
import static com.jueggs.utils.Utils.*;

public class CategoryFragment extends Fragment implements Callback
{
    public static final String STATE_CATEGORY_STACK = "state.category.stack";
    public static final String STATE_CHANNEL_STACK = "state.channel.stack";
    public static final String STATE_CURRENT_CHANNELS = "state.current.channels";
    public static final String STATE_CURRENT_CATEOGRIES = "state.current.categories";
    public static final String STATE_SELECTED_POSITION = "state.selected.channel";
    public static final String STATE_LEVEL = "state.level";

    @Bind(R.id.root) FrameLayout root;
    @Bind(R.id.recycler) RecyclerView recycler;
    @Bind(R.id.navBack) FloatingActionButton navBack;
    @Bind(R.id.scroll) FloatingActionButton scroll;
    @Bind(R.id.empty) LinearLayout empty;

    private CategoryRepository categoryRepository;
    private ChannelRepository channelRepository;
    private CategoryAdapter adapter;
    private Stack<List<Category>> categoryStack = new Stack<>();
    private Stack<List<Channel>> channelStack = new Stack<>();
    private List<Category> currentCategories;
    private List<Channel> currentChannels;
    private int level;
    private int selectedPosition = INVALID_POSITION;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            categoryStack = (Stack<List<Category>>) savedInstanceState.getSerializable(STATE_CATEGORY_STACK);
            channelStack = (Stack<List<Channel>>) savedInstanceState.getSerializable(STATE_CHANNEL_STACK);
            currentChannels = (List<Channel>) savedInstanceState.getSerializable(STATE_CURRENT_CHANNELS);
            currentCategories = (List<Category>) savedInstanceState.getSerializable(STATE_CURRENT_CATEOGRIES);
            selectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION, INVALID_POSITION);
            level = savedInstanceState.getInt(STATE_LEVEL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        ButterKnife.bind(this, view);

        equipeRecycler(getContext(), recycler, adapter = new CategoryAdapter(getActivity(), getActivity().getSupportFragmentManager(), this, selectedPosition));
        categoryRepository = CategoryRepository.getInstance(getContext());
        channelRepository = ChannelRepository.getInstance(getContext());

        navBack.setOnClickListener(this::onNavigateBack);
        scroll.setOnClickListener(this::onScrollToFirstChannel);

        return view;
    }

    private void onNavigateBack(View view)
    {
        List<Category> popCategories = categoryStack.pop();
        adapter.setCategories(popCategories);
        currentCategories = popCategories;
        List<Channel> popChannels = channelStack.pop();
        adapter.setChannels(popChannels);
        currentChannels = popChannels;
        onNavigationLevelChanged(--level);
    }

    private void onScrollToFirstChannel(View view)
    {
        if (hasElements(adapter.getChannels()))
        {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recycler.getLayoutManager();
            boolean scrollDown = layoutManager.findFirstVisibleItemPosition() < adapter.getCategories().size();
            if (scrollDown)
            {
                int span = layoutManager.findLastVisibleItemPosition() - layoutManager.findFirstVisibleItemPosition();
                int diff = span / 2;
                recycler.smoothScrollToPosition(adapter.getCategories().size() + findOffset(diff));
            }
            else
                recycler.smoothScrollToPosition(adapter.getCategories().size());
        }
    }

    private int findOffset(int diff)
    {
        int size = adapter.getChannels().size();
        while (diff >= size)
            diff--;
        return diff;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            if (currentCategories != null)
                adapter.setCategories(currentCategories);
            if (currentChannels != null)
                adapter.setChannels(currentChannels);
            if (selectedPosition != INVALID_POSITION) recycler.smoothScrollToPosition(selectedPosition);
            showViewWithFade(root, navBack, level != 0);
            showViewWithFade(root, scroll, hasElements(adapter.getChannels()));
        }
        else
            categoryRepository.loadCategories(App.LANGUAGE, this::onCategoriesLoaded);
    }

    public void onCategoriesLoaded(List<Category> categories)
    {
        int state = readNetworkState(getContext());
        switch (state)
        {
            case Result.SUCCESS:
                empty.setVisibility(View.GONE);
                adapter.setCategories(categories);
                currentCategories = categories;
                break;
            case Result.NO_NETWORK:
                showEmptyView(getContext(), empty, R.string.empty_no_network, R.drawable.ic_wifi_off);
                break;
            case Result.SERVER_DOWN:
                showEmptyView(getContext(), empty, R.string.empty_server_down, R.drawable.ic_server_down);
                break;
            case Result.INVALID_DATA:
                showEmptyView(getContext(), empty, R.string.empty_invalid_data, R.drawable.ic_invalid_data);
                break;
            case Result.UNKNOWN:
                showEmptyView(getContext(), empty, R.string.empty_unknown, R.drawable.ic_unknown_problem);
                break;
        }
    }

    private void onNavigationLevelChanged(int level)
    {
        showViewWithFade(root, navBack, level != 0);
        showViewWithFade(root, scroll, hasElements(adapter.getChannels()));
        selectedPosition = INVALID_POSITION;
    }

    @Override
    public void onCategorySelected(Category category)
    {
        categoryStack.push(adapter.getCategories());
        channelStack.push(adapter.getChannels());

        channelRepository.loadChannels(Integer.parseInt(category.getId()), App.LANGUAGE, this::onChannelsLoaded);
        onNavigationLevelChanged(++level);

        if (hasElements(category.getSubCategories()))
        {
            adapter.setCategories(category.getSubCategories());
            currentCategories = category.getSubCategories();
        }
        else
        {
            adapter.setCategories(new ArrayList<>());
            currentCategories = new ArrayList<>();
        }

    }

    @Override
    public void onChannelSelected(int position)
    {
        selectedPosition = position;
    }

    private void onChannelsLoaded(List<Channel> channels)
    {
        int state = readNetworkState(getContext());
        switch (state)
        {
            case Result.SUCCESS:
                empty.setVisibility(View.GONE);
                adapter.setChannels(channels);
                currentChannels = channels;
                showViewWithFade(root, scroll, hasElements(adapter.getChannels()));
                break;
            case Result.NO_NETWORK:
                showEmptyView(getContext(), empty, R.string.empty_no_network, R.drawable.ic_wifi_off);
                break;
            case Result.SERVER_DOWN:
                showEmptyView(getContext(), empty, R.string.empty_server_down, R.drawable.ic_server_down);
                break;
            case Result.INVALID_DATA:
                showEmptyView(getContext(), empty, R.string.empty_invalid_data, R.drawable.ic_invalid_data);
                break;
            case Result.UNKNOWN:
                showEmptyView(getContext(), empty, R.string.empty_unknown, R.drawable.ic_unknown_problem);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putSerializable(STATE_CATEGORY_STACK, categoryStack);
        outState.putSerializable(STATE_CHANNEL_STACK, channelStack);
        outState.putSerializable(STATE_CURRENT_CHANNELS, (ArrayList) currentChannels);
        outState.putSerializable(STATE_CURRENT_CATEOGRIES, (ArrayList) currentCategories);
        outState.putInt(STATE_SELECTED_POSITION, selectedPosition);
        outState.putInt(STATE_LEVEL, level);
    }
}
