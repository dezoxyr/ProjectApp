package com.example.projectapp;

import java.util.List;

public class Restanimereponse {
    private String image_url;
    private String title;
    private String type;
    private Integer episodes;
    private String score;
    private List<Genres> genres;


    public String getImage_url() {
        return image_url;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public Integer getEpisodes() {
        return episodes;
    }

    public String getScore() {
        return score;
    }

    public List<Genres> getGenres() {
        return genres;
    }
}
