package com.jueggs.podcaster.model;

import lombok.Data;

@Data
public class Episode
{
    private String showId;
    private String title;
    private String description;
    private String mediaLink;
    private String podLink;
    private String author;
    private String rating;
    private String votes;
    private String copyright;
    private String type;
    private String mimeType;
    private String date;
    private String size;
}
