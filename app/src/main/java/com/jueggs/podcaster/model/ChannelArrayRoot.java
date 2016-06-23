package com.jueggs.podcaster.model;

import lombok.Data;

import java.util.List;

@Data
public class ChannelArrayRoot
{
    private List<Head> head;
    private List<Channel> channels;
}
