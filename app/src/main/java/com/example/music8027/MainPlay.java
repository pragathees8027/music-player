package com.example.music8027;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class MainPlay extends AppCompatActivity {
    private String play_state = "playing";
    private String shuffle_state = "shuffle off";
    private String thumb_state = "removed from liked songs";
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;

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
                String title = "Music8027";
                String content = play_state;
                push_notifaction(title, content);
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

    public void push_notifaction(String notif_title, String notif_content){
        Uri sound = Uri. parse (ContentResolver. SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/quite_impressed.mp3" ) ;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainPlay. this,
                default_notification_channel_id )
                .setSmallIcon(R.mipmap.ic_launcher )
                .setContentTitle(notif_title)
                .setSound(sound)
                .setContentText(notif_content);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context. NOTIFICATION_SERVICE ) ;

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();

        NotificationChannel notificationChannel = new
                NotificationChannel( NOTIFICATION_CHANNEL_ID , getText(R.string.app_name) , NotificationManager.IMPORTANCE_HIGH) ;
        notificationChannel.enableLights( true ) ;
        notificationChannel.setLightColor(Color. RED ) ;
        notificationChannel.enableVibration( true ) ;
        notificationChannel.setVibrationPattern( new long []{ 100 , 200 , 300 , 400 , 500 , 400 , 300 , 200 , 400 }) ;
        notificationChannel.setSound(sound , audioAttributes) ;
        mBuilder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(notificationChannel) ;
        assert mNotificationManager != null;
        mNotificationManager.notify(( int ) System. currentTimeMillis (), mBuilder.build()) ;
    }
}