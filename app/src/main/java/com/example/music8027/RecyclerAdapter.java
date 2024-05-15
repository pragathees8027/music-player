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
import java.util.concurrent.ExecutionException;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    private final Context context;
    private final List<Object> listRecyclerItem;
    private String searchType;


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

    public class ItemViewHolder extends RecyclerView.ViewHolder {

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
    }

    @Override
    public int getItemCount() {
        return listRecyclerItem.size();
    }

}
