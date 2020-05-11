package com.example.projectapp;

import java.io.Serializable;
import java.util.List;

public class Anime implements Serializable {
    private Integer mal_id;
    private String image_url;
    private String title;
    private String type;

    public String getTitle() {
        return title;
    }

    public String getImage_url() {
        return image_url;
    }

    public Integer getMal_id() {
        return mal_id;
    }

    public String getType() {
        return type;
    }
}
