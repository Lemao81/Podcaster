package com.jueggs.podcaster.ui.category;

import com.jueggs.podcaster.model.Category;
import com.jueggs.podcaster.model.Channel;

import java.util.List;

public interface Callback
{
    void onCategorySelected(Category category);

    void onChannelSelected(int position);
}
