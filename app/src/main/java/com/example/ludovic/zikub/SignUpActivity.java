package com.example.ludovic.zikub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    /** Called when the user taps the Sign up button */
    public void signup(View view) throws IOException, JSONException {

        final Intent intent = new Intent(this, HomeActivity.class);

        TextView usernameExist = (TextView) findViewById(R.id.usernameExist);
        TextView mailExist = (TextView) findViewById(R.id.emailExist);
        usernameExist.setVisibility(View.INVISIBLE);
        mailExist.setVisibility(View.INVISIBLE);

        final TextView username = (TextView) findViewById(R.id.username);
        TextView mail = (TextView) findViewById(R.id.email);
        TextView pwd = (TextView) findViewById(R.id.password);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.10.10/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ZikubService service = retrofit.create(ZikubService.class);
        Call<Result> result = service.signUp(
                mail.getText().toString(),
                username.getText().toString(),
                pwd.getText().toString()
        );

        result.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                String result = new Gson().toJson(response.body().getSuccess());
                String wrongLogin = new Gson().toJson(response.body().getExistLogin());
                String wrongEmail = new Gson().toJson(response.body().getExistMail());

                if (response.isSuccessful()) {
                    if(result.equals("true")) {
                        int id_user = response.body().getIdUser();

                        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("Storage.Users", Context.MODE_PRIVATE);

                        sharedPreferences
                                .edit()
                                .putInt("idUser", id_user)
                                .apply();

                        startActivity(intent);
                    }

                    else {
                        if(wrongLogin.equals("true")) {
                            TextView usernameExist = (TextView) findViewById(R.id.usernameExist);
                            usernameExist.setVisibility(View.VISIBLE);
                        }

                        if(wrongEmail.equals("true")) {
                            TextView mailExist = (TextView) findViewById(R.id.emailExist);
                            mailExist.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    // error response, no access to resource?
                    Log.v("fail", response.toString());
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                // something went completely south (like no internet connection)
                Log.d("Error", t.getMessage());
            }
        });
    }
}
