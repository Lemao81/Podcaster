package com.jueggs.podcaster.model;

import lombok.Data;

import java.util.List;

@Data
public class ChannelRoot
{
    List<Head> head;
    Channel channel;
}
