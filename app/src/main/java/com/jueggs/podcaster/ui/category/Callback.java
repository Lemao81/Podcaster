package com.jueggs.podcaster.ui.category;

import com.jueggs.podcaster.model.Category;

public interface Callback
{
    interface NavigationLevelChanged
    {
        void onNavigationLevelChanged(int level);

        void onCategorySelected(Category category);
    }
}
