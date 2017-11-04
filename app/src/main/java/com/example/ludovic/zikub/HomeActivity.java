package com.example.ludovic.zikub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.ludovic.zikub.JsonReader.readJsonFromUrl;

public class HomeActivity extends Activity {

    public final static String EXTRA_MESSAGE =
            "com.ltm.ltmactionbar.MESSAGE";

    private List<ImageButton> imgBtn;

    private MediaPlayer player;
    private long currentSongLength;

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

        final TextView title_music = (TextView)findViewById(R.id.title_music);
        final ImageButton playpause = (ImageButton) findViewById(R.id.playpause);

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

                ResponseBody body = response.raw().body();

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
                            for(final Music m : playlist) {
                                if(m.getIndice() == indice) {
                                    Picasso.with(HomeActivity.this).load("https://img.youtube.com/vi/" + m.getUrl() + "/mqdefault.jpg").resize(width, height).centerCrop().into(imButton);
                                    imButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (player != null && player.isPlaying()) {
                                                player.pause();
                                            }
                                            new YoutubeExtractor().execute(m.getUrl());
                                            playpause.setBackgroundResource(R.drawable.ic_play);
                                            title_music.setText(getTitleQuietly(m.getUrl()));
                                    }
                                    });
                                    imButton.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {
                                            music(v);
                                            return true;
                                        }
                                    });
                                }
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

        //Gestion de la Seekbar
        handleSeekbar();

    }

    private void handleSeekbar(){
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (player != null && fromUser) {
                    player.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        int width = (getResources().getDisplayMetrics().widthPixels) / 2;
        int height = (int) ((getResources().getDisplayMetrics().heightPixels) /  3.1);
        final TextView title_music = (TextView)findViewById(R.id.title_music);
        final ImageButton playpause = (ImageButton) findViewById(R.id.playpause);

        if( resultCode == 1 ) {
            String indice = data.getStringExtra("indice");
            final String url = data.getStringExtra("url");
            int id_user = data.getIntExtra("id_user", 0);

            imgBtn = new ArrayList<ImageButton>();

            for(int id : BUTTON_IDS) {
                ImageButton imButton = (ImageButton) findViewById(id);

                if(indice.equals(imButton.getTag().toString())) {
                    Picasso.with(this).load("https://img.youtube.com/vi/" + url + "/mqdefault.jpg").resize(width, height).centerCrop().into(imButton);
                    imButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (player != null && player.isPlaying()) {
                                player.pause();
                            }
                            new YoutubeExtractor().execute(url);
                            playpause.setBackgroundResource(R.drawable.ic_play);
                            title_music.setText(getTitleQuietly(url));
                        }
                    });
                    imButton.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            music(v);
                            return true;
                        }
                    });
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

    /** Called when the user taps the share button */
    public void share(View view) {
        //TODO Activity ou autre chose....
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


    public void PlayStream(String URL) {
        releaseMP();

        try {
            player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(this, Uri.parse(URL));
            player.prepare();

            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    currentSongLength =  mp.getDuration();
                    //Lancer la chanson
                    togglePlay(mp);
                }
            });
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    ImageButton playpause = (ImageButton) findViewById(R.id.playpause);
                    playpause.setBackgroundResource(R.drawable.ic_play);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void releaseMP() {
        if (player != null) {
            try {
                player.release();
                player = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void togglePlay(MediaPlayer mp) {
        ImageButton playpause = (ImageButton) findViewById(R.id.playpause);

        if(mp.isPlaying()){
            mp.stop();
            mp.reset();
        }else {
            final SeekBar seekBar = (SeekBar) findViewById(R.id.seekbar);
            final TextView time = (TextView) findViewById(R.id.time);
            mp.start();
            playpause.setBackgroundResource(R.drawable.ic_pause);
            final Handler mHandler = new Handler();
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    seekBar.setMax((int) currentSongLength / 1000);
                    int mCurrentPosition = player.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    time.setText(convertDuration((long) player.getCurrentPosition()));
                    mHandler.postDelayed(this, 1000);
                }
            });
        }
    }

    public static String convertDuration(long duration){

        long minutes = (duration / 1000 ) / 60;
        long seconds = (duration / 1000 ) % 60;

        String converted = String.format("%d:%02d", minutes, seconds);
        return converted;


    }



    /** Called when the user taps the play button */
    public void PlayPause(View view) {
        if(player != null) {
            ImageButton playpause = (ImageButton) findViewById(R.id.playpause);
            if (player.isPlaying()) {
                player.pause();
                playpause.setBackgroundResource(R.drawable.ic_play);
            } else {
                player.start();
                playpause.setBackgroundResource(R.drawable.ic_pause);
            }
        }
    }

    public static String getTitleQuietly(String ID) {
        JSONObject json = null;
        String title = null;
        try {
            json = readJsonFromUrl("http://www.youtube.com/oembed?url=https://www.youtube.com/watch?v=" + ID + "&format=json");
            title = json.get("title").toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return title;
    }

    private class YoutubeExtractor extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String youtubeLink = "http://youtube.com/watch?v="+params[0];
            new YouTubeExtractor(getApplicationContext()) {
                @Override
                public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                    if (ytFiles != null) {
                        int itag = 140;
                        String downloadUrl = ytFiles.get(itag).getUrl();
                        PlayStream(downloadUrl);
                    }
                }
            }.extract(youtubeLink, true, true);
            return null;
        }
    }

    }
