package com.jueggs.podcaster.ui.channeldetail;

import android.view.View;
import android.widget.ImageButton;
import com.jueggs.podcaster.model.Episode;

public interface Callback
{
    void onPlayPauseByImageButton(ImageButton button, int position);

    void onFavourize(View view);

    void onDownload(Episode episode);
}
