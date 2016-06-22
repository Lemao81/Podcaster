package com.jueggs.podcaster;


import com.jueggs.podcaster.data.PodcastContract;
import org.junit.Test;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class PodcastContractTest
{
    @Test
    public void createCategoriesUri()
    {
        //Act
        String actual = PodcastContract.createCategoriesUri().toString();

        //Assert
        assertThat(actual, is(equalTo("https://api.podcast.de/categories.json")));
    }
}
