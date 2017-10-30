package com.example.ludovic.zikub;

import com.google.gson.annotations.SerializedName;

public class Music {

    @SerializedName("indice")
    private int indice;

    @SerializedName("url")
    private String url;

    Music(int indice, String url) {
        this.indice = indice;
        this.url = url;
    }

    public int getIndice() {
        return indice;
    }

    public String getUrl() {
        return url;
    }

}
