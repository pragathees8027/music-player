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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class searchFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<Object> viewItems = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
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
            try {
                URL url = new URL("https://musicapi.x007.workers.dev/search?q=" + searchName + "&searchEngine=gaama");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                StringBuilder response = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONArray songArray = jsonObject.getJSONArray("response");

                for (int i = 0; i < songArray.length(); ++i) {
                    JSONObject itemObj = songArray.getJSONObject(i);
                    String id = itemObj.getString("id");
                    String name = itemObj.getString("title");
                    String art = itemObj.getString("img");
                    songUnits.add(new songUnit(name, art, id));
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
