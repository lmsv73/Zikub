package com.example.ludovic.zikub;

import java.util.ArrayList;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.Call;

/**
 * Interface pour Retrofit qui contient les routes pour le service REST Web.
 */
public interface ZikubService {
    @GET("/signup/{mail}/{username}/{pwd}")
    Call<Result> signUp(
        @Path("mail") String mail,
        @Path("username") String username,
        @Path("pwd") String pwd
    );

    @GET("/login/{login}/{pwd}")
    Call<Result> login(
        @Path("login") String login,
        @Path("pwd") String pwd
    );

    @GET("/addMusic/{id_user}/{url}/{indice}")
    Call<Result> addMusic(
        @Path("id_user") int id_user,
        @Path("url") String url,
        @Path("indice") int indice
    );

    @GET("/getPlaylist/{id_user}")
    Call<ArrayList<Music>> getPlaylist(
        @Path("id_user") int id_user
    );
}
