package com.example.ludovic.zikub;

import com.google.gson.annotations.SerializedName;

/**
 * Classe représentant une Music
 * - Un indice (1,2,3,4,5)
 * - Un id video (2f2ze651fez)
 */
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
