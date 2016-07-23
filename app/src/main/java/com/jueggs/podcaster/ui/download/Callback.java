package com.jueggs.podcaster.ui.download;

import android.view.View;
import android.widget.ImageButton;
import com.jueggs.podcaster.model.Episode;

public interface Callback
{
    void onPlayPause(ImageButton button, int position);

    void onStopEpisode();

    void showEmptyView(boolean show);
}
