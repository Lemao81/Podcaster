package com.jueggs.podcaster.ui.category;

import com.jueggs.podcaster.model.Category;
import com.jueggs.podcaster.model.Channel;

public interface Callback
{
    void onNavigationLevelChanged(int level);

    void onCategorySelected(Category category);
}
