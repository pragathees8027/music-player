package com.example.music8027;

import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;

public class playFragment extends Fragment {
    private String play_state = "playing";
    private String shuffle_state = "shuffle off";
    private String thumb_state = "removed from liked songs";
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    private Toast toast = null;

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_play, container, false);
        LottieAnimationView animationView = view.findViewById(R.id.play_animation_view);

        SeekBar seek_bar;
        ProgressBar progress_bar;
        ObjectAnimator progressAnimator;
        seek_bar = (SeekBar) view.findViewById(R.id.seekBar);
        progressAnimator = ObjectAnimator.ofInt(seek_bar, "progress", 0,100);
        progressAnimator.setDuration(10000);
        progressAnimator.start();

        MaterialButton play = (MaterialButton) view.findViewById(R.id.play);
        MaterialButton shuffle = (MaterialButton) view.findViewById(R.id.shuffle);
        MaterialButton thumb = (MaterialButton) view.findViewById(R.id.thumbs);
        MaterialButton forward = (MaterialButton) view.findViewById(R.id.next);
        MaterialButton backward = (MaterialButton) view.findViewById(R.id.previous);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (play_state == "paused") {
                    Animation zoom_out = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_out);
                    view.startAnimation(zoom_out);
                    play.setIconResource(R.drawable.pause_circle_24px);
                    play.setIconTintResource(R.color.red);
                    animationView.playAnimation();
                    animationView.setVisibility(View.VISIBLE);
                    progressAnimator.resume();
                    play_state = "playing";
                    Animation zoom_in = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in);
                    view.startAnimation(zoom_in);
                } else {
                    Animation zoom_out = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_out);
                    view.startAnimation(zoom_out);
                    play.setIconResource(R.drawable.play_circle_24px);
                    play.setIconTintResource(R.color.red);
                    animationView.cancelAnimation();
                    animationView.setVisibility(View.GONE);
                    progressAnimator.pause();
                    play_state = "paused";
                    Animation zoom_in = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in);
                    view.startAnimation(zoom_in);
                }
               /* if (toast != null)
                    toast.cancel();
                toast = Toast.makeText(getActivity(), play_state, Toast.LENGTH_SHORT);
                toast.show();*/
                String title = "Music8027";
                String content = play_state;
                push_notifaction(title, content);
            }
        });

        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shuffle_state == "shuffle on") {
                    Animation zoom_out = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_out);
                    view.startAnimation(zoom_out);
                    shuffle.setIconResource(R.drawable.shuffle_24px);
                    shuffle.setIconTintResource(R.color.grey);
                    shuffle_state = "shuffle off";
                    Animation zoom_in = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in);
                    view.startAnimation(zoom_in);
                } else {
                    Animation zoom_out = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_out);
                    view.startAnimation(zoom_out);
                    shuffle.setIconResource(R.drawable.shuffle_on_24px);
                    shuffle.setIconTintResource(R.color.red);
                    shuffle_state = "shuffle on";
                    Animation zoom_in = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in);
                    view.startAnimation(zoom_in);
                }
                if (toast != null)
                    toast.cancel();
                toast = Toast.makeText(getActivity(), shuffle_state, Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (thumb_state == "added to liked songs") {
                    Animation zoom_out = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_out);
                    view.startAnimation(zoom_out);
                    thumb.setIconResource(R.drawable.favorite_24px);
                    thumb.setIconTintResource(R.color.grey);
                    thumb_state = "removed from liked songs";
                    Animation zoom_in = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in);
                    view.startAnimation(zoom_in);
                } else {
                    Animation zoom_out = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_out);
                    view.startAnimation(zoom_out);
                    thumb.setIconResource(R.drawable.heart_check_24px);
                    thumb.setIconTintResource(R.color.red);
                    thumb_state = "added to liked songs";
                    Animation zoom_in = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in);
                    view.startAnimation(zoom_in);
                }
                if (toast != null)
                    toast.cancel();
                toast = Toast.makeText(getActivity(), thumb_state, Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation zoom_out = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_out);
                view.startAnimation(zoom_out);
                progressAnimator.start();
                Animation zoom_in = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in);
                view.startAnimation(zoom_in);
            }
        });

        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation zoom_out = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_out);
                view.startAnimation(zoom_out);
                progressAnimator.start();
                Animation zoom_in = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in);
                view.startAnimation(zoom_in);
            }
        });

        return view;
    }
    public void push_notifaction(String notif_title, String notif_content){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(playFragment.this.requireContext(), default_notification_channel_id )
                .setSmallIcon(R.drawable.ic_notif)
                .setSilent(true)
                .setContentTitle(notif_title)
                .setContentText(notif_content);
        NotificationManager mNotificationManager = (NotificationManager) requireContext().getSystemService(Context. NOTIFICATION_SERVICE ) ;

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build();

        NotificationChannel notificationChannel = new
                NotificationChannel( NOTIFICATION_CHANNEL_ID , getText(R.string.app_name) , NotificationManager.IMPORTANCE_HIGH) ;
        notificationChannel.enableLights( true ) ;
        notificationChannel.setLightColor(Color. RED ) ;
        notificationChannel.enableVibration( false ) ;
        notificationChannel.setVibrationPattern( new long []{ 0 }) ;
        mBuilder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(notificationChannel) ;
        mNotificationManager.cancelAll();
        mNotificationManager.notify(( int ) System. currentTimeMillis (), mBuilder.build()) ;
    }
}