package com.example.music8027;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class searchFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<Object> viewItems = new ArrayList<>();
    private ArrayList<JSONObject> searchResult;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private GridLayoutManager gridLayoutManager;
    private EditText searchSong;
    private TextView detName, detType, detInfo, detCount;
    private ImageView detImg;
    private static final String TAG = "searchFragment";
    private String songName = null;
    private String searchSpecifier = "";
    private LottieAnimationView loadingAnimation, noResult;
    private MaterialCardView detCard;
    private MaterialButton search, topBtn, songBtn, albumBtn, artistBtn, playlistBtn, detAdd, detClose;
    MaterialButtonToggleGroup searchToggle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mRecyclerView = view.findViewById(R.id.song_recycle_view);
        mRecyclerView.setHasFixedSize(true);

        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                try {
                    JSONObject searchItem = searchResult.get(position);
                    if (searchItem.getString("type").equals("song"))
                        return 2;
                    return 1;
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        mAdapter = new RecyclerAdapter(getContext(), viewItems, searchSpecifier, new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) throws JSONException {
                String objectID = searchResult.get(position).getString("id");
                String type = searchResult.get(position).getString("type");;
                String searchSpec = null;
                switch (type) {
                    case "song":
                        searchSpec = "songs/";
                        break;

                    case "album":
                        searchSpec = "albums?id=";
                        break;

                    case "artist":
                        searchSpec = "artists/";
                        break;

                    case "playlist":
                        searchSpec = "playlists?id=";
                        break;
                }
                String objectUrl = "https://saavn.dev/api/" + searchSpec + objectID;
                Log.e(TAG, objectUrl);
                new FetchSongDataTask().execute(objectUrl);
            }
        });

        layoutManager = gridLayoutManager;
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        searchToggle = view.findViewById(R.id.search_specifier);
        search = view.findViewById(R.id.search_button);
        topBtn = view.findViewById(R.id.topSearch);
        songBtn = view.findViewById(R.id.songSearch);
        albumBtn = view.findViewById(R.id.albumSearch);
        artistBtn = view.findViewById(R.id.artistSearch);
        playlistBtn = view.findViewById(R.id.playlistSearch);
        detAdd = view.findViewById(R.id.detailsAdd);
        detClose = view.findViewById(R.id.detailsClose);
        searchSong = view.findViewById(R.id.search_input);
        detImg = view.findViewById(R.id.detailsImg);
        detName = view.findViewById(R.id.detailsName);
        detType = view.findViewById(R.id.detailsType);
        detInfo = view.findViewById(R.id.detailsInfo);
        detCount = view.findViewById(R.id.detailsCount);
        loadingAnimation = view.findViewById(R.id.loading_animation);
        noResult = view.findViewById(R.id.no_result_animation);
        detCard = view.findViewById(R.id.detailsCard);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerSearch();
            }
        });

        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchSpecifier = "";
                topBtn.setIconTintResource(R.color.red);
                songBtn.setIconTintResource(R.color.teal);
                albumBtn.setIconTintResource(R.color.teal);
                artistBtn.setIconTintResource(R.color.teal);
                playlistBtn.setIconTintResource(R.color.teal);
                triggerSearch();
            }
        });

        songBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchSpecifier = "/songs";
                topBtn.setIconTintResource(R.color.teal);
                songBtn.setIconTintResource(R.color.red);
                albumBtn.setIconTintResource(R.color.teal);
                artistBtn.setIconTintResource(R.color.teal);
                playlistBtn.setIconTintResource(R.color.teal);
                triggerSearch();
            }
        });

        albumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchSpecifier = "/albums";
                topBtn.setIconTintResource(R.color.teal);
                songBtn.setIconTintResource(R.color.teal);
                albumBtn.setIconTintResource(R.color.red);
                artistBtn.setIconTintResource(R.color.teal);
                playlistBtn.setIconTintResource(R.color.teal);
                triggerSearch();
            }
        });

        artistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchSpecifier = "/artists";
                topBtn.setIconTintResource(R.color.teal);
                songBtn.setIconTintResource(R.color.teal);
                albumBtn.setIconTintResource(R.color.teal);
                artistBtn.setIconTintResource(R.color.red);
                playlistBtn.setIconTintResource(R.color.teal);
                triggerSearch();
            }
        });

        playlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchSpecifier = "/playlists";
                topBtn.setIconTintResource(R.color.teal);
                songBtn.setIconTintResource(R.color.teal);
                albumBtn.setIconTintResource(R.color.teal);
                artistBtn.setIconTintResource(R.color.teal);
                playlistBtn.setIconTintResource(R.color.red);
                triggerSearch();
            }
        });

        return view;
    }

    private class FetchDataTask extends AsyncTask<String, Void, ArrayList<JSONObject>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            noResult.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            loadingAnimation.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<JSONObject> doInBackground(String... params) {
            String searchName = params[0];
            searchResult = new ArrayList<JSONObject>();
            try {
                URL url = new URL("https://saavn.dev/api/search"+ searchSpecifier +"?query=" + searchName);
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

                List<JSONArray> result = new ArrayList<>();
                if (searchSpecifier.equals("")) {
                    result.add(jsonObject.getJSONObject("topQuery").getJSONArray("results"));
                    result.add(jsonObject.getJSONObject("albums").getJSONArray("results"));
                    result.add(jsonObject.getJSONObject("artists").getJSONArray("results"));
                    result.add(jsonObject.getJSONObject("playlists").getJSONArray("results"));
                    result.add(jsonObject.getJSONObject("songs").getJSONArray("results"));
                } else
                    result.add(jsonObject.getJSONArray("results"));

                for (JSONArray array : result) {
                    for (int i = 0; i < array.length(); ++i) {
                        JSONObject itemObj = array.getJSONObject(i);
                        searchResult.add(itemObj);
                    }
                }
                connection.disconnect();
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error fetching data", e);
            }
            return searchResult;
        }

        @Override
        protected void onPostExecute(ArrayList<JSONObject> searchResult) {
            viewItems.clear();
            viewItems.addAll(searchResult);
            mAdapter.notifyDataSetChanged();
            loadingAnimation.setVisibility(View.GONE);
            if (searchResult.isEmpty())
                noResult.setVisibility(View.VISIBLE);
            else
                mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void triggerSearch() {
        songName = searchSong.getText().toString();
        songName = songName.replaceAll("\\s+", "+");
        new FetchDataTask().execute(songName);
    }

    public class FetchSongDataTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            noResult.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            loadingAnimation.setVisibility(View.VISIBLE);

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject songData = null;
            try {
                String objectUrlString = params[0];
                URL objectUrl = new URL(objectUrlString);
                HttpURLConnection connection = (HttpURLConnection) objectUrl.openConnection();
                connection.setRequestMethod("GET");

                StringBuilder response = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                songData = new JSONObject(response.toString());

                connection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return songData;
        }

        @Override
        protected void onPostExecute(JSONObject songData) {
            search.setVisibility(View.GONE);
            searchSong.setVisibility(View.GONE);
            searchToggle.setVisibility(View.GONE);
            loadingAnimation.setVisibility(View.GONE);
        }
    }
}
