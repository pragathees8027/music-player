package com.example.music8027;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    private final Context context;
    private final List<Object> listRecyclerItem;
    private String searchType;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(String apiUrl);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public RecyclerAdapter(Context context, List<Object> listRecyclerItem, String searchType) {
        this.context = context;
        this.listRecyclerItem = listRecyclerItem;
        if (!searchType.equals(""))
            searchType = searchType.substring(1);
        this.searchType = searchType;
    }

    @Override
    public int getItemViewType(int position)
    {
        JSONObject currentUnit = (JSONObject) listRecyclerItem.get(position);
        try {
            if (currentUnit.getString("type").equals("song"))
                return 1;
            return 0;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView song_name;
        private final TextView song_info;
        private final ImageView song_art;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            song_name = itemView.findViewById(R.id.song_name);
            song_info = itemView.findViewById(R.id.song_info);
            song_art = itemView.findViewById(R.id.album_art);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        View layoutView;
        if (type == 1) {
            layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.list_item, viewGroup, false);
        } else {
            layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.list_item_square, viewGroup, false);
        }
        return new ItemViewHolder(layoutView);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder viewHolder, int i) {
        JSONObject songUnit = (JSONObject) listRecyclerItem.get(i);

        try {
            if (songUnit.getString("type").equals("song")) {
                if (songUnit.has("singers"))
                    viewHolder.song_info.setText(String.format("%s - %s", songUnit.getString("singers"), songUnit.getString("album")));
                else if(songUnit.has("primary"))
                    viewHolder.song_info.setText(String.format("%s - %s", songUnit.getJSONObject("artists").getJSONArray("primary").getJSONObject(0).getString("name"), songUnit.getJSONObject("album").getString("name")));
                else
                    viewHolder.song_info.setText(songUnit.getJSONObject("album").getString("name"));
            } else if (songUnit.getString("type").equals("album")) {
                if (songUnit.has("artist"))
                    viewHolder.song_info.setText(String.format("%s ( %s )", songUnit.getString("type"), songUnit.getString("artist")));
                else
                    viewHolder.song_info.setText(String.format("%s", songUnit.getString("type")));
            } else if (songUnit.getString("type").equals("artist")) {
                viewHolder.song_info.setText(String.format(songUnit.getString("type")));
            } else if (songUnit.getString("type").equals("playlist")) {
                viewHolder.song_info.setText(String.format("%s\n( %s )", songUnit.getString("type"), songUnit.getString("language")));
            }
            if (songUnit.has("title"))
                viewHolder.song_name.setText(songUnit.getString("title"));
            else
                viewHolder.song_name.setText(songUnit.getString("name"));
            if (songUnit.has("image")) {
                int len = songUnit.getJSONArray("image").length();
                Picasso.get().load(songUnit.getJSONArray("image").getJSONObject(len - 1).getString("url")).into(viewHolder.song_art);
            } else
                Picasso.get().load(R.drawable.vinyl).into(viewHolder.song_art);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject item = (JSONObject) listRecyclerItem.get(i);
                String id = null;
                String type = null;
                try {
                    id = item.getString("id");
                    type = item.getString("type");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String objectUrl = "https://saavn.dev/api/" + type + "s/" + id;
                new FetchSongDataTask().execute(objectUrl);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listRecyclerItem.size();
    }

    private class FetchSongDataTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            String objectUrlString = params[0];
            return fetchSongData(objectUrlString);
        }

        @Override
        protected void onPostExecute(JSONObject songData) {
            if (mListener != null) {
                mListener.onItemClick(String.valueOf(songData));
            }
        }
    }

    private JSONObject fetchSongData(String objectUrlString) {
        JSONObject songData = null;
        try {
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
}
