package com.jueggs.podcaster.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Channel implements Serializable
{
    private String channelId;
    private String title;
    private String channelTitle;
    private String subtitle;
    private String feedLink;
    private String description;
    private String podLink;
    private String image;
    private String channelType;
    private String copyright;
    private String rating;
    private String votes;
    private String subscribers;
    private String date;
    private List<Episode> episodes;
}
