package com.jueggs.podcaster.model;

import lombok.Data;

@Data
public class Channel
{
    private String channelId;
    private String title;
    private String channelTitle;
    private String description;
    private String podLink;
    private String image;
    private String channelType;
    private String copyright;
    private String rating;
    private String votes;
    private String subscribers;
    private String date;
}
