package com.example.ludovic.zikub;

/**
 * Created by Victor on 12/10/2017.
 */

public class Search {
    private String title;
    private String image;

    public Search(String title, String image) {
        this.title = title;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
