package com.example.music8027;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class songsFragment extends Fragment {
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_songs, container, false);
        List<String> songsList = new ArrayList<String>();
        for (int i = 1; i <= 20 ; i++)
            songsList.add("Song "+i);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_expandable_list_item_1, songsList);
        ListView songs_list = view.findViewById(R.id.songs_list);
        songs_list.setAdapter(adapter);

        return view;
    }
}