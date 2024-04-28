package com.example.music8027;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

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
    private static final String TAG = "searchFragment";
    private String songName = null;
    private String searchSpecifier = "";
    private LottieAnimationView loadingAnimation;

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

        layoutManager = gridLayoutManager;
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new RecyclerAdapter(getContext(), viewItems);
        mRecyclerView.setAdapter(mAdapter);

        MaterialButton search = view.findViewById(R.id.search_button);
        MaterialButtonToggleGroup searchSpec = view.findViewById(R.id.search_specifier);
        MaterialButton songBtn = view.findViewById(R.id.songSearch);
        MaterialButton albumBtn = view.findViewById(R.id.albumSearch);
        MaterialButton artistBtn = view.findViewById(R.id.artistSearch);
        MaterialButton playlistBtn = view.findViewById(R.id.playlistSearch);
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

        searchSpec.addOnButtonCheckedListener((materialButtonToggleGroup, checkedID, isChecked) -> {
            if (isChecked) {
                if (checkedID == R.id.songSearch) {
                    searchSpecifier = "/songs";
                    songBtn.setIconTintResource(R.color.red);
                    albumBtn.setIconTintResource(R.color.teal);
                    artistBtn.setIconTintResource(R.color.teal);
                    playlistBtn.setIconTintResource(R.color.teal);
                } else if (checkedID == R.id.albumSearch) {
                    searchSpecifier = "/albums";
                    albumBtn.setIconTintResource(R.color.red);
                    songBtn.setIconTintResource(R.color.teal);
                    artistBtn.setIconTintResource(R.color.teal);
                    playlistBtn.setIconTintResource(R.color.teal);
                } else if (checkedID == R.id.artistSearch) {
                    searchSpecifier = "/artists";
                    artistBtn.setIconTintResource(R.color.red);
                    songBtn.setIconTintResource(R.color.teal);
                    albumBtn.setIconTintResource(R.color.teal);
                    playlistBtn.setIconTintResource(R.color.teal);
                } else if (checkedID == R.id.playlistSearch) {
                    searchSpecifier = "/playlists";
                    playlistBtn.setIconTintResource(R.color.red);
                    songBtn.setIconTintResource(R.color.teal);
                    albumBtn.setIconTintResource(R.color.teal);
                    artistBtn.setIconTintResource(R.color.teal);
                }
            } else {
                searchSpecifier = "";
                songBtn.setIconTintResource(R.color.teal);
                albumBtn.setIconTintResource(R.color.teal);
                artistBtn.setIconTintResource(R.color.teal);
                playlistBtn.setIconTintResource(R.color.teal);
            }
        });

        return view;
    }

    private class FetchDataTask extends AsyncTask<String, Void, ArrayList<JSONObject>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingAnimation.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
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
                result.add(jsonObject.getJSONObject("topQuery").getJSONArray("results"));
                result.add(jsonObject.getJSONObject("albums").getJSONArray("results"));
                result.add(jsonObject.getJSONObject("artists").getJSONArray("results"));
                result.add(jsonObject.getJSONObject("playlists").getJSONArray("results"));
                result.add(jsonObject.getJSONObject("songs").getJSONArray("results"));

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
        protected void onPostExecute(ArrayList<JSONObject> songUnits) {
            viewItems.clear();
            viewItems.addAll(searchResult);
            mAdapter.notifyDataSetChanged();
            loadingAnimation.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
