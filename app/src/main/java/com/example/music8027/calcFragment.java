package com.example.music8027;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

public class calcFragment extends Fragment {
    View view;
    private  String count = null;
    private int countInt = 0;
    private int usage = 0;
    TextView resDisp;
    EditText getCount;
    Spinner spinnerQuality;
    private  String selection = null;
    ArrayAdapter<CharSequence> adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calc, container, false);
        spinnerQuality = view.findViewById(R.id.song_size);

        adapter= ArrayAdapter.createFromResource(requireContext() , R.array.quality, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerQuality.setAdapter(adapter);

        MaterialButton getResult = view.findViewById(R.id.get_result);
        getCount = view.findViewById(R.id.song_count);
        resDisp = view.findViewById(R.id.result);

        getResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selection = spinnerQuality.getSelectedItem().toString();
                switch (selection){
                    case "low":
                        count = getCount.getText().toString();
                        countInt = Integer.parseInt(count);
                        usage = countInt * 3;
                        resDisp.setText(Integer.toString(usage));
                        break;

                    case "medium":
                        count = getCount.getText().toString();
                        countInt = Integer.parseInt(count);
                        usage = countInt * 6;
                        resDisp.setText(Integer.toString(usage));
                        break;

                    case "high":
                        count = getCount.getText().toString();
                        countInt = Integer.parseInt(count);
                        usage = countInt * 12;
                        resDisp.setText(Integer.toString(usage));
                        break;
                }
            }
        });

        return view;
    }
}