package com.jueggs.podcaster.model;

import lombok.Data;

@Data
public class Head
{
    private String title;
    private int limit;
    private int offset;
    private int count;
    private String lang;
}
