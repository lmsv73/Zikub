package com.example.ludovic.zikub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class HomeActivity extends Activity {

    public final static String EXTRA_MESSAGE =
            "com.ltm.ltmactionbar.MESSAGE";

    private List<ImageButton> imgBtn;
    private static final int[] BUTTON_IDS = {
            R.id.imageButton1,
            R.id.imageButton2,
            R.id.imageButton3,
            R.id.imageButton4,
            R.id.imageButton5
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        imgBtn = new ArrayList<ImageButton>();
        int width = (getResources().getDisplayMetrics().widthPixels) / 2;
        int height = (int) ((getResources().getDisplayMetrics().heightPixels) /  3.1);

        for(int id : BUTTON_IDS) {
            ImageButton imButton = (ImageButton) findViewById(id);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) imButton.getLayoutParams();
            params.width = width;
            params.height = height;

            imButton.setLayoutParams(params);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        int width = (getResources().getDisplayMetrics().widthPixels) / 2;
        int height = (int) ((getResources().getDisplayMetrics().heightPixels) /  3.1);

        if( resultCode == 1 ) {
            String indice = data.getStringExtra("indice");
            String url = data.getStringExtra("url");
            int id_user = data.getIntExtra("id_user", 0);

            imgBtn = new ArrayList<ImageButton>();

            for(int id : BUTTON_IDS) {
                if(Integer.parseInt(indice) == id) {
                    ImageButton imButton = (ImageButton) findViewById(id);
                    Picasso.with(this).load("https://img.youtube.com/vi/"+url+"/mqdefault.jpg").resize(width, height).centerCrop().into(imButton);
                }
            }

            insertMusic(url, Integer.parseInt(indice), id_user);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /** Called when the user taps the first music button */
    public void music(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(EXTRA_MESSAGE, view.getTag().toString());
        startActivityForResult(intent, 0);
    }

    public void insertMusic(String url, int indice, int id_user)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.10.10/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ZikubService service = retrofit.create(ZikubService.class);
        Call<Result> result = service.addMusic(
                id_user,
                url,
                indice
        );

        result.enqueue(new retrofit2.Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                String result = new Gson().toJson(response.body().getSuccess());
                if (!response.isSuccessful() || result.equals("false")) {
                    // Relancer la recherche
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                // something went completely south (like no internet connection)
                Log.d("Error", t.getMessage());
            }
        });

    }

    @Override
    public void onBackPressed() {
    }
}