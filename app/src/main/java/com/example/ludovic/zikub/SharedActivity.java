package com.example.ludovic.zikub;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.ludovic.zikub.JsonReader.readJsonFromUrl;

public class SharedActivity extends AppCompatActivity {

    /**
     * Player utilisé dans notre application
     */
    private MediaPlayer player = new MediaPlayer();
    final Handler mHandler = new Handler();
    public Runnable mRunnable  = new Runnable() {
        @Override
        public void run() {
            final SeekBar seekBar = (SeekBar) findViewById(R.id.seekbar);
            final TextView time = (TextView) findViewById(R.id.time);
            seekBar.setMax((int) currentSongLength / 1000);
            int mCurrentPosition = player.getCurrentPosition() / 1000;
            seekBar.setProgress(mCurrentPosition);
            time.setText(convertDuration((long) player.getCurrentPosition()));
            mHandler.postDelayed(this, 1000);
        }
    };
    //Variable mise à jour toutes les secondes pour garder en mémoire où on en dans la musique
    private long currentSongLength;
    /**
     * Représente les ID des boutons des musiques de la playlist
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
     * Chaque indice correspond à l'indice du bouton.
     * Si la valeur de l'indice est à 0, le bouton correspondant n'a aucune musique associée.
     */
    public static final int[] VOID_MUSIC = { 0, 0, 0, 0, 0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared);
        int id_user = 0;

        if(getIntent().getData()!=null){//check if intent is not null
            Uri data = getIntent().getData();//set a variable for the Intent
            String fullPath = data.getEncodedSchemeSpecificPart();//get the full path -scheme - fragments
            id_user = Integer.parseInt(fullPath.substring(fullPath.lastIndexOf("/") + 1));
            final TextView username = (TextView) findViewById(R.id.username);
            username.setText((username.getText() + " " + getUsernameQuietly(fullPath.substring(fullPath.lastIndexOf("/") + 1))).toUpperCase());
        }

        final TextView title_music = (TextView)findViewById(R.id.title_music);
        final ImageButton playpause = (ImageButton) findViewById(R.id.playpause);

        //Utilisation de la bibliothèque Retrofit pour
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://54.37.68.6/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ZikubService service = retrofit.create(ZikubService.class);
        Call<ArrayList<Music>> result = service.getPlaylist(id_user);

        final ArrayList<Music> playlist = new ArrayList<>();

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
                                    Picasso.with(SharedActivity.this).load("https://img.youtube.com/vi/" + m.getUrl() + "/mqdefault.jpg").resize(width, height).centerCrop().into(imButton);
                                    imButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (player != null && player.isPlaying()) {
                                                player.pause();
                                            }
                                            new SharedActivity.YoutubeExtractor().execute(m.getUrl());
                                            playpause.setBackgroundResource(R.drawable.ic_play);
                                            title_music.setText(getTitleQuietly(m.getUrl()));
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

    /**
     * Méthode qui gère la progression de la seekbar toutes les secondes
     */
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

    /**
     * Méthode pour empécher le retour sur la page login de l'application lorsque l'utilisateur s'est connecté.
     * TODO : Bouton déconnexion
     */
    @Override
    public void onPause() {
        super.onPause();
        if(player.isPlaying()){
            player.stop();
            player.reset();
        }
        mHandler.removeCallbacks(mRunnable);
        releaseMP();
        finish();
    }

    /**
     * Méthode qui prend un id vidéo en paramètre et lance la musique.
     *
     * @param URL String
     */
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

    /**
     * Méthode qui détruit le media player pour eviter des conflits avec le stream audio.
     */
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

    /**
     * Méthode qui prend un mediaPlayer et qui lance la musique en mettant à jour la seekbar et le temps.
     * @param mp Mediaplayer
     */
    private void togglePlay(MediaPlayer mp) {
        ImageButton playpause = (ImageButton) findViewById(R.id.playpause);

        if(mp.isPlaying()){
            mp.stop();
            mp.reset();
        }else {

            mp.start();
            playpause.setBackgroundResource(R.drawable.ic_pause);
            mHandler.postDelayed(mRunnable, 1000);
        }
    }

    /**
     * Fonction qui retourne la durée en minutes et secondes à partir des millisecondes
     * @param duration long
     * @return
     */
    public static String convertDuration(long duration){

        long minutes = (duration / 1000 ) / 60;
        long seconds = (duration / 1000 ) % 60;

        String converted = String.format("%d:%02d", minutes, seconds);
        return converted;


    }

    /**
     * Fonction qui retourne le titre de la vidéo facilement en utilisant la classe JsonReader à partir de son ID.
     *
     * @param ID Id de la vidéo
     * @return Titre de la vidéo
     */
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
    /**
     *
     *
     * @param ID Id de la vidéo
     * @return Username
     */
    public static String getUsernameQuietly(String ID) {
        JSONObject json = null;
        String title = null;
        try {
            json = readJsonFromUrl("http://www.victor-basset.fr/getUsername/" + ID);
            title = json.get("name").toString();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return title;
    }
    /**
     * Bibliothèque qui extrait la musique .AAC d'une vidéo Youtube à partir de son ID.
     */
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
