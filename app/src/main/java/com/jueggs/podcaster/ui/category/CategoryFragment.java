package com.jueggs.podcaster.ui.category;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.data.PodcastContract;
import com.jueggs.podcaster.data.repo.CategoryRepository;
import com.jueggs.podcaster.model.Category;

import java.util.List;

public class CategoryFragment extends Fragment
{
    @Bind(R.id.recycler) RecyclerView recycler;

    private CategoryRepository repository = CategoryRepository.getInstance();
    private CategoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        ButterKnife.bind(this, view);

        recycler.setAdapter(adapter = new CategoryAdapter(getContext()));
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        //TODO language from settings
        repository.loadCategories(PodcastContract.LANG_EN, this::onCategoriesLoaded);
    }

    private void onCategoriesLoaded(List<Category> categories)
    {
        adapter.setCategories(categories);
    }
}
