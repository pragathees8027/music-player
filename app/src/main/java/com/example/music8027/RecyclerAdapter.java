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

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {
    private final Context context;
    private final List<Object> listRecyclerItem;

    public RecyclerAdapter(Context context, List<Object> listRecyclerItem) {
        this.context = context;
        this.listRecyclerItem = listRecyclerItem;
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
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.list_item, viewGroup, false);
        return new ItemViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder viewHolder, int i) {
        songUnit songUnit = (songUnit) listRecyclerItem.get(i);

        if (songUnit.getType().equals("song")) {
            viewHolder.song_name.setText(songUnit.getTitle());
            viewHolder.song_info.setText(String.format("%s - %s", songUnit.getSingers(), songUnit.getAlbum()));
            Picasso.get().load(songUnit.getImage()).into(viewHolder.song_art);
        } else {
            if (songUnit.getSingers() == null) {
                viewHolder.song_name.setText(songUnit.getTitle());
                viewHolder.song_info.setText(String.format(songUnit.getType()));
            } else {
                viewHolder.song_name.setText(songUnit.getTitle());
                viewHolder.song_info.setText(String.format("%s ( %s )", songUnit.getType(), songUnit.getSingers()));
            }
            Picasso.get().load(songUnit.getImage()).into(viewHolder.song_art);
        }
    }

    @Override
    public int getItemCount() {
        return listRecyclerItem.size();
    }
}
