package com.example.ludovic.zikub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    ArrayList<Search> arrayList;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // intent
        final Intent intent = getIntent();
        final String numMusic = intent.getStringExtra(HomeActivity.EXTRA_MESSAGE);
        String message = "Search song " + numMusic;
        final TextView textView3 = (TextView)findViewById( R.id.textView3 );
        textView3.setText(message);

        arrayList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.listView);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Search listItem = (Search) lv.getItemAtPosition(position);
                Log.v("id video",listItem.getId());

                SharedPreferences sharedPref = getBaseContext().getSharedPreferences("Storage.Users", Context.MODE_PRIVATE);
                int id_user = sharedPref.getInt("idUser", 0);

                Intent i2 = new Intent();
                i2.putExtra("indice",numMusic);
                i2.putExtra("url",listItem.getId());
                i2.putExtra("id_user", id_user);
                SearchActivity.this.setResult(1, i2);
                SearchActivity.this.finish();
            }
        });
        final EditText search = (EditText) findViewById(R.id.search);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    arrayList.clear();
                    search.clearFocus();
                    InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(search.getWindowToken(), 0);
                    new YoutubeSearchAPI().execute(search.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }



    private class YoutubeSearchAPI extends AsyncTask<String, Void, Void> {



        /**
         * Define a global variable that identifies the name of a file that
         * contains the developer's API key.
         */
        private static final long NUMBER_OF_VIDEOS_RETURNED = 10;

        /**
         * Define a global instance of a Youtube object, which will be used
         * to make YouTube Data API requests.
         */
        private YouTube youtube;

        @Override
        protected Void doInBackground(String... params) {
            try {
                // This object is used to make YouTube Data API requests. The last
                // argument is required, but since we don't need anything
                // initialized when the HttpRequest is initialized, we override
                // the interface and provide a no-op function.
                youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer()  {
                    public void initialize(HttpRequest request) throws IOException {
                    }
                }).setApplicationName("youtube-cmdline-search-sample").build();

                // Prompt the user to enter a query term.
                String queryTerm = params[0];

                // Define the API request for retrieving search results.
                YouTube.Search.List search = youtube.search().list("id,snippet");

                // Set your developer key from the Google Developers Console for
                // non-authenticated requests. See:
                // https://console.developers.google.com/
                String apiKey = "AIzaSyA12RLKm9SR0s0heZhyBjiK5JxJDiHah6w";
                search.setKey(apiKey);
                search.setQ(queryTerm);

                // Restrict the search results to only include videos. See:
                // https://developers.google.com/youtube/v3/docs/search/list#type
                search.setType("video");

                // To increase efficiency, only retrieve the fields that the
                // application uses.
                search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
                search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

                // Call the API and print results.
                SearchListResponse searchResponse = search.execute();
                List<SearchResult> searchResultList = searchResponse.getItems();
                if (searchResultList != null) {
                    for (SearchResult singleVideo : searchResultList) {

                        ResourceId rId = singleVideo.getId();

                        // Confirm that the result represents a video. Otherwise, the
                        // item will not contain a video ID.
                        if (rId.getKind().equals("youtube#video")) {
                            Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();

                            Log.v(" Video Id", rId.getVideoId());
                            Log.v(" Title: ", singleVideo.getSnippet().getTitle());
                            Log.v(" Image: ",  thumbnail.getUrl());

                            arrayList.add(new Search(
                                    rId.getVideoId(),
                                    singleVideo.getSnippet().getTitle(),
                                    thumbnail.getUrl()
                            ));
                        }
                    }
                }
            } catch (GoogleJsonResponseException e) {
                System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                        + e.getDetails().getMessage());
            } catch (IOException e) {
                System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Do All UI Changes HERE

            CustomListAdapter adapter = new CustomListAdapter(
                    getApplicationContext(), R.layout.liste_video, arrayList
            );
            lv.setAdapter(adapter);

        }
    }
}

