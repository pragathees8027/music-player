package com.example.music8027;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class MainPlay extends AppCompatActivity {
    private String play_state = "playing";
    private String shuffle_state = "shuffle off";
    private String thumb_state = "removed from liked songs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mainplay);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_play), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialButton buttonCalc = (MaterialButton) findViewById(R.id.calculator);
        MaterialButton play = (MaterialButton) findViewById(R.id.play);
        MaterialButton shuffle = (MaterialButton) findViewById(R.id.shuffle);
        MaterialButton thumb = (MaterialButton) findViewById(R.id.thumbs);

        buttonCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Calc = new Intent(view.getContext(), MainCalc.class);
                view.getContext().startActivity(Calc);
                Toast.makeText(getApplicationContext(), "Loading Calculator", Toast.LENGTH_SHORT).show();
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (play_state == "paused") {
                    play.setIconResource(R.drawable.ic_pause_circle_filled);
                    play_state = "playing";
                } else {
                    play.setIconResource(R.drawable.ic_play_circle_filled);
                    play_state = "paused";
                }
                Toast.makeText(getApplicationContext(), play_state, Toast.LENGTH_SHORT).show();
            }
        });

        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shuffle_state == "shuffle on") {
                    shuffle.setIconResource(R.drawable.ic_shuffle);
                    shuffle_state = "shuffle off";
                } else {
                    shuffle.setIconResource(R.drawable.ic_shuffle_on);
                    shuffle_state = "shuffle on";
                }
                Toast.makeText(getApplicationContext(), shuffle_state, Toast.LENGTH_SHORT).show();
            }
        });

        thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (thumb_state == "added to liked songs") {
                    thumb.setIconResource(R.drawable.ic_thumb_up_off_alt);
                    thumb_state = "removed from liked songs";
                } else {
                    thumb.setIconResource(R.drawable.ic_thumb_up);
                    thumb_state = "added to liked songs";
                }
                Toast.makeText(getApplicationContext(), thumb_state, Toast.LENGTH_SHORT).show();
            }
        });
    }

}