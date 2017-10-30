package com.example.ludovic.zikub;

import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("success")
    private boolean success;

    @SerializedName("existLogin")
    private boolean existLogin;

    @SerializedName("existMail")
    private boolean existMail;

    @SerializedName("id_user")
    private int id_user;




    public boolean getSuccess() {
        return success;
    }

    public boolean getExistLogin() {
        return existLogin;
    }

    public boolean getExistMail() {
        return existMail;
    }

    public int getIdUser() {return id_user; }

}
