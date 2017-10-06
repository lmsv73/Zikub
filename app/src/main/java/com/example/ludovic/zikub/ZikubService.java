package com.example.ludovic.zikub;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.Call;

public interface ZikubService {
    @GET("/signup/{mail}/{username}/{pwd}")
    Call<ResponseBody> signUp(
        @Path("mail") String mail,
        @Path("username") String username,
        @Path("pwd") String pwd
    );

    @GET("/login/{login}/{pwd}")
    Call<ResponseBody> login(
        @Path("login") String login,
        @Path("pwd") String pwd
    );
}
