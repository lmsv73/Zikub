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

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class HomeActivity extends Activity {

    public final static String EXTRA_MESSAGE =
            "com.ltm.ltmactionbar.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final ImageButton imageButton1 = (ImageButton) findViewById(R.id.imageButton1);
        final ImageButton imageButton2 = (ImageButton) findViewById(R.id.imageButton2);
        final ImageButton imageButton3 = (ImageButton) findViewById(R.id.imageButton3);
        final ImageButton imageButton4 = (ImageButton) findViewById(R.id.imageButton4);
        final ImageButton imageButton5 = (ImageButton) findViewById(R.id.imageButton5);

        int width = (getResources().getDisplayMetrics().widthPixels) / 2;
        int height = (int) ((getResources().getDisplayMetrics().heightPixels) /  3.1);

        FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) imageButton1.getLayoutParams();
        params1.width = width;
        params1.height = height;

        FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) imageButton2.getLayoutParams();
        params2.width = width;
        params2.height = height;

        FrameLayout.LayoutParams params3 = (FrameLayout.LayoutParams) imageButton3.getLayoutParams();
        params3.width = width;
        params3.height = height;

        FrameLayout.LayoutParams params4 = (FrameLayout.LayoutParams) imageButton4.getLayoutParams();
        params4.width = width;
        params4.height = height;

        FrameLayout.LayoutParams params5 = (FrameLayout.LayoutParams) imageButton5.getLayoutParams();
        params5.width = width;
        params5.height = height;

        imageButton1.setLayoutParams(params1);
        imageButton2.setLayoutParams(params2);
        imageButton3.setLayoutParams(params3);
        imageButton4.setLayoutParams(params4);
        imageButton5.setLayoutParams(params5);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        int width = (getResources().getDisplayMetrics().widthPixels) / 2;
        int height = (int) ((getResources().getDisplayMetrics().heightPixels) /  3.1);

        if( resultCode==1 ) {
            String indice = data.getStringExtra("indice");
            String url = data.getStringExtra("url");
            int id_user = data.getIntExtra("id_user", 0);

            switch (indice)
            {
                case "1":
                    final ImageButton imageButton1 = (ImageButton) findViewById(R.id.imageButton1);
                    Picasso.with(this).load("https://img.youtube.com/vi/"+url+"/mqdefault.jpg").resize(width, height).centerCrop().into(imageButton1);
                    break;
                case "2":
                    final ImageButton imageButton2 = (ImageButton) findViewById(R.id.imageButton2);
                    Picasso.with(this).load("https://img.youtube.com/vi/"+url+"/mqdefault.jpg").resize(width, height).centerCrop().into(imageButton2);
                    break;
                case "3":
                    final ImageButton imageButton3 = (ImageButton) findViewById(R.id.imageButton3);
                    Picasso.with(this).load("https://img.youtube.com/vi/"+url+"/mqdefault.jpg").resize(width, height).centerCrop().into(imageButton3);
                    break;
                case "4":
                    final ImageButton imageButton4 = (ImageButton) findViewById(R.id.imageButton4);
                    Picasso.with(this).load("https://img.youtube.com/vi/"+url+"/mqdefault.jpg").resize(width, height).centerCrop().into(imageButton4);
                    break;
                case "5":
                    final ImageButton imageButton5 = (ImageButton) findViewById(R.id.imageButton5);
                    Picasso.with(this).load("https://img.youtube.com/vi/"+url+"/mqdefault.jpg").resize(width, height).centerCrop().into(imageButton5);
                    break;
                default:
                    Log.v("erreur","indice faux");
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