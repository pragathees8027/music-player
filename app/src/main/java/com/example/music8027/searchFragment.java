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
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

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
import java.util.concurrent.ExecutionException;

public class searchFragment extends Fragment implements FetchDataTask.OnDataFetchedListener, RecyclerAdapter.OnTaskStatusListener  {
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
    private ArrayList<JSONObject> songDetails;
    private String tmpString = null, tmpString2 = null;
    private JSONObject objectDetails = null;
    private Toast toast;

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

        mAdapter = new RecyclerAdapter(getContext(), viewItems, searchSpecifier);
        mRecyclerView.setAdapter(mAdapter);

        MaterialButton search = view.findViewById(R.id.search_button);
        MaterialButton topBtn = view.findViewById(R.id.topSearch);
        MaterialButton songBtn = view.findViewById(R.id.songSearch);
        MaterialButton albumBtn = view.findViewById(R.id.albumSearch);
        MaterialButton artistBtn = view.findViewById(R.id.artistSearch);
        MaterialButton playlistBtn = view.findViewById(R.id.playlistSearch);
        MaterialButton detAdd = view.findViewById(R.id.detailsAdd);
        MaterialButton detClose = view.findViewById(R.id.detailsClose);
        searchSong = view.findViewById(R.id.search_input);
        detImg = view.findViewById(R.id.detailsImg);
        detName = view.findViewById(R.id.detailsName);
        detType = view.findViewById(R.id.detailsType);
        detInfo = view.findViewById(R.id.detailsInfo);
        detCount = view.findViewById(R.id.detailsCount);
        loadingAnimation = view.findViewById(R.id.loading_animation);
        noResult = view.findViewById(R.id.no_result_animation);

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

        ((RecyclerAdapter) mAdapter).setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String apiUrl) throws ExecutionException, InterruptedException {
                new RecyclerAdapter.FetchSongDataTask().execute(apiUrl);
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

    @Override
    public void onDataFetched(ArrayList<JSONObject> songData) {
        songDetails = songData;
        //JSONObject objectDetails = null;
        try{
            objectDetails = (JSONObject) songDetails.get(0).getJSONArray("data").get(0);
            if (objectDetails.getString("type").equals("song")) {
                if (objectDetails.has("artists") && objectDetails.has("album")) {
                    if (objectDetails.getJSONObject("artists").has("primary"))
                        tmpString = objectDetails.getJSONObject("artists").getJSONArray("primary").getJSONObject(0).getString("name");
                    if (objectDetails.has("album")) {
                        if (objectDetails.getJSONObject("album").has("name"))
                            tmpString2 = objectDetails.getJSONObject("album").getString("name");
                    }
                    detInfo.setText(String.format("%s - %s", tmpString, tmpString2));
                } else if (objectDetails.has("artists")) {
                    if (objectDetails.getJSONObject("artists").has("primary"))
                        tmpString = objectDetails.getJSONObject("artists").getJSONArray("primary").getJSONObject(0).getString("name");
                    detInfo.setText(tmpString);
                } else if (objectDetails.has("album")) {
                    if (objectDetails.getJSONObject("album").has("name"))
                        tmpString2 = objectDetails.getJSONObject("album").getString("name");
                    detInfo.setText(tmpString2);
                } else
                    detInfo.setText("null");
                if (objectDetails.has("playCount"))
                    detCount.setText(objectDetails.getString("playCount"));
                else
                    detCount.setText("null");
            } else if (objectDetails.getString("type").equals("artist")) {
                if (objectDetails.has("bio")) {
                    if (objectDetails.has("bio")) {
                        if (objectDetails.getJSONObject("bio").has("title"))
                            tmpString = objectDetails.getJSONObject("bio").getString("title");
                    }
                    detInfo.setText(tmpString);
                } else
                    detInfo.setText("null");
                if (objectDetails.has("followerCount"))
                    detCount.setText(objectDetails.getString("followerCount"));
                else
                    detCount.setText("null");
            } else if (objectDetails.getString("type").equals("album")) {
                if (objectDetails.has("artists") && objectDetails.has("bio")) {
                    if (objectDetails.getJSONObject("artists").has("primary"))
                        tmpString = objectDetails.getJSONObject("artists").getJSONArray("primary").getJSONObject(0).getString("name");
                    if (objectDetails.has("bio")) {
                        if (objectDetails.getJSONObject("bio").has("title"))
                            tmpString2 = objectDetails.getJSONObject("bio").getString("title");
                    }
                    detInfo.setText(String.format("%s - %s", tmpString, tmpString2));
                } else if (objectDetails.has("artists")) {
                    if (objectDetails.getJSONObject("artists").has("primary"))
                        tmpString = objectDetails.getJSONObject("artists").getJSONArray("primary").getJSONObject(0).getString("name");
                    detInfo.setText(tmpString);
                } else if (objectDetails.has("bio")) {
                    if (objectDetails.getJSONObject("bio").has("title"))
                        tmpString2 = objectDetails.getJSONObject("bio").getString("title");
                    detInfo.setText(tmpString2);
                } else
                    detInfo.setText("null");
                if (!objectDetails.has("playCount"))
                    detCount.setText("null");
                else
                    detCount.setText(objectDetails.getString("playCount"));
            } else if (objectDetails.getString("type").equals("playlist")) {
                if (objectDetails.has("description"))
                    detInfo.setText(objectDetails.getString("description"));
                else
                    detInfo.setText("null");
                if (!objectDetails.has("playCount"))
                    detCount.setText("null");
                else
                    detCount.setText(objectDetails.getString("playCount"));
            }
            if (objectDetails.has("name"))
                detName.setText(objectDetails.getString("name"));
            else
                detName.setText("unknown");
            if (objectDetails.has("image")) {
                int len = objectDetails.getJSONArray("image").length();
                Picasso.get().load(objectDetails.getJSONArray("image").getJSONObject(len - 1).getString("url")).into(detImg);
            } else
                Picasso.get().load(R.drawable.vinyl).into(detImg);
            detType.setText(objectDetails.getString("type"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onTaskStart() {
        // Show a toast at the start of the task
        toast.setText("Fetching data...");
        toast.show();
    }

    @Override
    public void onTaskComplete() {
        // Show a toast at the end of the task
        toast.setText("Data fetched successfully.");
        toast.show();
    }
}
