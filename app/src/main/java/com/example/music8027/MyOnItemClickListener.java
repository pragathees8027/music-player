package com.example.music8027;

import org.json.JSONObject;
import java.util.List;

public class MyOnItemClickListener implements RecyclerAdapter.OnItemClickListener {
    private List<Object> listRecyclerItem;

    public MyOnItemClickListener(List<Object> listRecyclerItem) {
        this.listRecyclerItem = listRecyclerItem;
    }

    @Override
    public void onItemClick(int position) {
        returnPosition(position);
    }

    public int returnPosition(int position) {
        return position;
    }
}
