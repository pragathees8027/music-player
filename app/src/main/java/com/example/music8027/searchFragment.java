package com.example.music8027;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.json.JSONTokener;

import java.net.URL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class searchFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<Object> viewItems = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private static final String TAG = "searchFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.song_list_view);
        mRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new RecyclerAdapter(getContext(), viewItems);
        mRecyclerView.setAdapter(mAdapter);
        addItemsFromJSON();
        return view;
    }

    private void addItemsFromJSON() {
        try {

            URL url = new URL("https://musicapi.x007.workers.dev/search?q=Pathaan&searchEngine=gaama");
            String json = IOUtils.toString(url, "UTF-8");
            JSONObject jsonObject = new JSONObject(new JSONTokener(json));

            JSONArray songArray = jsonObject.getJSONArray("response");

            for (int i=0; i<songArray.length(); ++i) {

                JSONObject itemObj = songArray.getJSONObject(i);

                String id = itemObj.getString("id");
                String name = itemObj.getString("title");
                String art = itemObj.getString("img");

                songUnit song_unit = new songUnit(name, art, id);
                viewItems.add(song_unit);
            }

        } catch (JSONException | IOException e) {
            Log.d(TAG, "addItemsFromJSON: ", e);
        }
    }
}