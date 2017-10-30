package com.example.ludovic.zikub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import com.google.gson.Gson;
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

    /**
     * Représente les ID des bouttons des musiques de la playlist
     */
    private static final int[] BUTTON_IDS = {
            R.id.imageButton1,
            R.id.imageButton2,
            R.id.imageButton3,
            R.id.imageButton4,
            R.id.imageButton5
    };

    /**
     * Représente un tableau pour les musiques de la playlist.
     * Chaque indice correspond à l'indice du boutton.
     * Si la valeur de l'indice est à 0, le boutton correspondant n'a aucune musique associée.
     */
    private static final int[] VOID_MUSIC = { 0, 0, 0, 0, 0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences prefs = this.getSharedPreferences("Storage.Users", Context.MODE_PRIVATE);
        int id_user = prefs.getInt("idUser", 0);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.10.10/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ZikubService service = retrofit.create(ZikubService.class);
        Call<ArrayList<Music>> result = service.getPlaylist(id_user);

        final ArrayList<Music> playlist = new ArrayList<Music>();

        result.enqueue(new retrofit2.Callback<ArrayList<Music>>() {
            @Override
            public void onResponse(Call<ArrayList<Music>> call, Response<ArrayList<Music>> response) {

                playlist.addAll(response.body());

                if (!response.isSuccessful()) {
                    // TODO Relancer la recherche

                } else {
                    // Charger les images
                    int width = (getResources().getDisplayMetrics().widthPixels) / 2;
                    int height = (int) ((getResources().getDisplayMetrics().heightPixels) /  3.1);

                    // Pour chaque musique de la playlist, on met à 1 la valeur du tableau VOID_MUSIC
                    for (Music m : playlist)
                        VOID_MUSIC[m.getIndice()-1] = 1;

                    int indice = 1;
                    for(int id : VOID_MUSIC ) {
                        ImageButton imButton = (ImageButton) findViewById(BUTTON_IDS[indice-1]);

                        // Affichage du carré de base
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) imButton.getLayoutParams();
                        params.width = width;
                        params.height = height;

                        imButton.setLayoutParams(params);

                        // Affichage de l'image chargée via l'api youtube
                        if(VOID_MUSIC[indice-1] == 1) {
                            for(Music m : playlist) {
                                if(m.getIndice() == indice)
                                    Picasso.with(HomeActivity.this).load("https://img.youtube.com/vi/"+m.getUrl()+"/mqdefault.jpg").resize(width, height).centerCrop().into(imButton);
                            }
                        }

                        indice ++;
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Music>> call, Throwable t) {
                // something went completely south (like no internet connection)
                Log.d("Error", t.getMessage());
            }
        });
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
                ImageButton imButton = (ImageButton) findViewById(id);

                if(indice.equals(imButton.getTag().toString()))
                    Picasso.with(this).load("https://img.youtube.com/vi/"+url+"/mqdefault.jpg").resize(width, height).centerCrop().into(imButton);
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
                    // TODO Relancer la recherche
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