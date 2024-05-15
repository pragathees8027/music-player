package com.example.music8027;

import android.os.AsyncTask;
import android.util.Log;

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

public class FetchDataTask extends AsyncTask<String, Void, ArrayList<JSONObject>> {
    private static final String TAG = "FetchDataTask";

    interface OnDataFetchedListener {
        void onDataFetched(ArrayList<JSONObject> songData);
    }

    private OnDataFetchedListener mListener;

    public FetchDataTask(OnDataFetchedListener listener) {
        this.mListener = listener;
    }

    @Override
    protected ArrayList<JSONObject> doInBackground(String... params) {
        String searchName = params[0];
        ArrayList<JSONObject> searchResult = new ArrayList<>();
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
    protected void onPostExecute(ArrayList<JSONObject> searchResult) {
        if (mListener != null) {
            // Here, you might want to perform further processing of the fetched data before passing it to the listener
            mListener.onDataFetched(searchResult);
        }
    }
}
