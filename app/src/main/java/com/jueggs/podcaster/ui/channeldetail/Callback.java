package com.jueggs.podcaster.ui.channeldetail;

import android.view.View;
import android.widget.ImageButton;

public interface Callback
{
    void onPlayPauseByImageButton(ImageButton button, int position);

    void onFavourize(View view);
}
