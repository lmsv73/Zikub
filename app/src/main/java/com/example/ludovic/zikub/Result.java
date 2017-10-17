package com.example.ludovic.zikub;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("existLogin")
    @Expose
    private boolean existLogin;

    @SerializedName("existMail")
    @Expose
    private boolean existMail;

    public boolean getSuccess() {
        return success;
    }

    public boolean getExistLogin() {
        return existLogin;
    }

    public boolean getExistMail() {
        return existMail;
    }
}
