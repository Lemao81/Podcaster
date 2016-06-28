package com.jueggs.podcaster.ui.channeldetail;

public interface Playback
{
    void onStartEpisode(String url);

    void onPauseEpisode();

    void onResumeEpisode();

    void onStopEpisode();
}
