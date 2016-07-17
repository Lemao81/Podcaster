package com.jueggs.podcaster.model;

import com.jueggs.utils.NetUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Category implements Serializable
{
    private String id;
    private String title;
    private List<Category> subCategories;
}
