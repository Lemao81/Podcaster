package com.jueggs.podcaster.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Episode implements Serializable
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
