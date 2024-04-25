package com.example.music8027;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class searchFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView nRecyclerView;
    private List<Object> viewItems = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter nAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.LayoutManager nlayoutManager;
    private EditText searchSong;
    private static final String TAG = "searchFragment";
    private String songName = null;
    private LottieAnimationView loadingAnimation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mRecyclerView = view.findViewById(R.id.song_recycle_view);
        mRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new RecyclerAdapter(getContext(), viewItems);
        mRecyclerView.setAdapter(mAdapter);

        MaterialButton search = view.findViewById(R.id.search_button);
        searchSong = view.findViewById(R.id.search_input);
        loadingAnimation = view.findViewById(R.id.loading_animation);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songName = searchSong.getText().toString();
                songName = songName.replaceAll("\\s+", "+");
                new FetchDataTask().execute(songName);
            }
        });

        return view;
    }

    private class FetchDataTask extends AsyncTask<String, Void, List<songUnit>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingAnimation.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

        @Override
        protected List<songUnit> doInBackground(String... params) {
            String searchName = params[0];
            List<songUnit> songUnits = new ArrayList<>();
            //List<songUnit> nonSongs = new ArrayList<>();
            try {
                URL url = new URL("https://saavn.dev/api/search?query=" + searchName);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                StringBuilder response = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(response.toString());
                JSONObject jsonObject = json.getJSONObject("data");

                JSONArray topQuery = jsonObject.getJSONObject("topQuery").getJSONArray("results");
                JSONArray albums = jsonObject.getJSONObject("albums").getJSONArray("results");
                JSONArray artists = jsonObject.getJSONObject("artists").getJSONArray("results");
                JSONArray playlists = jsonObject.getJSONObject("playlists").getJSONArray("results");
                JSONArray songs = jsonObject.getJSONObject("songs").getJSONArray("results");

                /*JSONObject topObject = jsonObject.getJSONObject("topQuery").getJSONObject("results");
                JSONObject albumObject = jsonObject.getJSONObject("albums").getJSONObject("results");
                JSONObject artistObject = jsonObject.getJSONObject("artists").getJSONObject("results");
                JSONObject playlistObject = jsonObject.getJSONObject("playlists").getJSONObject("results");
                JSONObject songObject = jsonObject.getJSONObject("songs").getJSONObject("results");*/

                for (int i = 0; i < topQuery.length(); ++i) {
                    JSONObject itemObj = topQuery.getJSONObject(i);
                    String id = itemObj.getString("id");
                    String type = itemObj.getString("type");
                    String title = itemObj.getString("title");
                    String image = itemObj.getJSONArray("image").getJSONObject(2).getString("url");
                    String album = null;
                    String itemUrl = null;
                    String singers = null;
                    String language = null;
                    String songIDs = null;

                    switch (type){
                        case "song":
                            album = itemObj.getString("album");
                            singers = itemObj.getString("singers");
                            language = itemObj.getString("language");
                            break;

                        case "album":
                            singers = itemObj.getString("artist");
                            language = itemObj.getString("language");
                            itemUrl = itemObj.getString("url");
                            songIDs = itemObj.getString("songIds");
                            break;

                        case "playlist":
                            language = itemObj.getString("language");
                            itemUrl = itemObj.getString("url");
                            break;
                    }
                    songUnits.add(new songUnit(id, title, image, album, itemUrl, type, singers, language, songIDs));
                }

                for (int i = 0; i < albums.length(); ++i) {
                    JSONObject itemObj = albums.getJSONObject(i);
                    String id = itemObj.getString("id");
                    String type = itemObj.getString("type");
                    String title = itemObj.getString("title");
                    String image = itemObj.getJSONArray("image").getJSONObject(2).getString("url");
                    String album = null;
                    String itemUrl = null;
                    String singers = null;
                    String language = null;
                    String songIDs = null;
                    switch (type){
                        case "song":
                            album = itemObj.getString("album");
                            singers = itemObj.getString("singers");
                            language = itemObj.getString("language");
                            break;

                        case "album":
                            singers = itemObj.getString("artist");
                            language = itemObj.getString("language");
                            itemUrl = itemObj.getString("url");
                            songIDs = itemObj.getString("songIds");
                            break;

                        case "playlist":
                            language = itemObj.getString("language");
                            itemUrl = itemObj.getString("url");
                            break;
                    }
                    songUnits.add(new songUnit(id, title, image, album, itemUrl, type, singers, language, songIDs));
                }

                for (int i = 0; i < artists.length(); ++i) {
                    JSONObject itemObj = artists.getJSONObject(i);
                    String id = itemObj.getString("id");
                    String type = itemObj.getString("type");
                    String title = itemObj.getString("title");
                    String image = itemObj.getJSONArray("image").getJSONObject(2).getString("url");
                    String album = null;
                    String itemUrl = null;
                    String singers = null;
                    String language = null;
                    String songIDs = null;
                    switch (type){
                        case "song":
                            album = itemObj.getString("album");
                            singers = itemObj.getString("singers");
                            language = itemObj.getString("language");
                            break;

                        case "album":
                            singers = itemObj.getString("artist");
                            language = itemObj.getString("language");
                            itemUrl = itemObj.getString("url");
                            songIDs = itemObj.getString("songIds");
                            break;

                        case "playlist":
                            language = itemObj.getString("language");
                            itemUrl = itemObj.getString("url");
                            break;
                    }
                    songUnits.add(new songUnit(id, title, image, album, itemUrl, type, singers, language, songIDs));
                }

                for (int i = 0; i < playlists.length(); ++i) {
                    JSONObject itemObj = playlists.getJSONObject(i);
                    String id = itemObj.getString("id");
                    String type = itemObj.getString("type");
                    String title = itemObj.getString("title");
                    String image = itemObj.getJSONArray("image").getJSONObject(2).getString("url");
                    String album = null;
                    String itemUrl = null;
                    String singers = null;
                    String language = null;
                    String songIDs = null;
                    switch (type){
                        case "song":
                            album = itemObj.getString("album");
                            singers = itemObj.getString("singers");
                            language = itemObj.getString("language");
                            break;

                        case "album":
                            singers = itemObj.getString("artist");
                            language = itemObj.getString("language");
                            itemUrl = itemObj.getString("url");
                            songIDs = itemObj.getString("songIds");
                            break;

                        case "playlist":
                            language = itemObj.getString("language");
                            itemUrl = itemObj.getString("url");
                            break;
                    }
                    songUnits.add(new songUnit(id, title, image, album, itemUrl, type, singers, language, songIDs));
                }

                for (int i = 0; i < songs.length(); ++i) {
                    JSONObject itemObj = songs.getJSONObject(i);
                    String id = itemObj.getString("id");
                    String type = itemObj.getString("type");
                    String title = itemObj.getString("title");
                    String image = itemObj.getJSONArray("image").getJSONObject(2).getString("url");
                    String album = null;
                    String itemUrl = null;
                    String singers = null;
                    String language = null;
                    String songIDs = null;
                    switch (type){
                        case "song":
                            album = itemObj.getString("album");
                            singers = itemObj.getString("singers");
                            language = itemObj.getString("language");
                            break;

                        case "album":
                            singers = itemObj.getString("artist");
                            language = itemObj.getString("language");
                            itemUrl = itemObj.getString("url");
                            songIDs = itemObj.getString("songIds");
                            break;

                        case "playlist":
                            language = itemObj.getString("language");
                            itemUrl = itemObj.getString("url");
                            break;
                    }
                    songUnits.add(new songUnit(id, title, image, album, itemUrl, type, singers, language, songIDs));
                }

                connection.disconnect();
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error fetching data", e);
            }
            return songUnits;
        }

        @Override
        protected void onPostExecute(List<songUnit> songUnits) {
            viewItems.clear();
            viewItems.addAll(songUnits);
            mAdapter.notifyDataSetChanged();
            loadingAnimation.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
