package com.jueggs.podcaster.data.repo;

import com.jueggs.podcaster.model.Category;
import com.jueggs.podcaster.model.Channel;

import java.util.List;

public interface Callback
{
    interface CategoriesLoaded
    {
        void onCategoriesLoaded(List<Category> categories);
    }

    interface ChannelsLoaded
    {
        void onChannelsLoaded(List<Channel> channels);
    }
}
