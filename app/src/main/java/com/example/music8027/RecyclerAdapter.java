package com.example.music8027;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE = 1;
    private final Context context;
    private final List<Object> listRecyclerItem;

    public RecyclerAdapter(Context context, List<Object> listRecyclerItem) {
        this.context = context;
        this.listRecyclerItem = listRecyclerItem;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private ImageView art;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.song_name);
            art = (ImageView) itemView.findViewById(R.id.album_art);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (i) {
            case TYPE:

            default:

                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.list_item, viewGroup, false);

                return new ItemViewHolder((layoutView));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        int viewType = getItemViewType(i);

        switch (viewType) {
            case TYPE:
            default:

                ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
                songUnit song_unit = (songUnit) listRecyclerItem.get(i);

                URL img_url = null;
                try {
                    img_url = new URL(song_unit.getArt());
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
                Bitmap imgIcon = null;
                try {
                    imgIcon = BitmapFactory.decodeStream(img_url.openConnection().getInputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                itemViewHolder.name.setText(song_unit.getName());
                itemViewHolder.art.setImageBitmap(imgIcon);
        }

    }

    @Override
    public int getItemCount() {
        return listRecyclerItem.size();
    }
}