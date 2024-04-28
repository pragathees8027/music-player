package com.example.music8027;

import android.content.Context;
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

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {
    private final Context context;
    private final List<Object> listRecyclerItem;

    public RecyclerAdapter(Context context, List<Object> listRecyclerItem) {
        this.context = context;
        this.listRecyclerItem = listRecyclerItem;
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
        if (type == 1) {
            View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.list_item, viewGroup, false);
            return new ItemViewHolder(layoutView);
        }

        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.list_item_square, viewGroup, false);
        return new ItemViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder viewHolder, int i) {
        JSONObject songUnit = (JSONObject) listRecyclerItem.get(i);

        try {
            if (songUnit.getString("type").equals("song")) {
                viewHolder.song_info.setText(String.format("%s - %s", songUnit.getString("title"), songUnit.getString("album")));
            } else if (songUnit.getString("type").equals("album")) {
                viewHolder.song_info.setText(String.format("%s ( %s )", songUnit.getString("type"), songUnit.getString("artist")));
            } else if (songUnit.getString("type").equals("artist")) {
                viewHolder.song_info.setText(String.format(songUnit.getString("type")));
            } else if (songUnit.getString("type").equals("playlist")) {
                viewHolder.song_info.setText(String.format("%s\n( %s )", songUnit.getString("type"), songUnit.getString("language")));
            }
            viewHolder.song_name.setText(songUnit.getString("title"));
            Picasso.get().load(songUnit.getJSONArray("image").getJSONObject(2).getString("url")).into(viewHolder.song_art);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return listRecyclerItem.size();
    }
}
