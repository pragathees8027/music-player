package com.example.music8027;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.music8027.databinding.ActivityMainBinding;



public class MainActivity extends AppCompatActivity {
    ActivityMainBinding main_binding;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main_binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(main_binding.getRoot());
        replaceFragment(new homeFragment());

        main_binding.mainBar.setOnItemSelectedListener(menuItem -> {
             if (menuItem.getItemId() == R.id.bar_home){
                 replaceFragment(new homeFragment());
             } else if (menuItem.getItemId() == R.id.bar_search){
                 replaceFragment(new searchFragment());
             } else if (menuItem.getItemId() == R.id.bar_playing){
                 replaceFragment(new playFragment());
             } else if (menuItem.getItemId() == R.id.bar_songs){
                 replaceFragment(new songsFragment());
             } else if (menuItem.getItemId() == R.id.bar_settings){
                 replaceFragment(new settingsFragment());
             }
            return true;
        });
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragMan = getSupportFragmentManager();
        FragmentTransaction fragTransact = fragMan.beginTransaction();
        fragTransact.replace(R.id.mainFrame, fragment);
        fragTransact.commit();
    }
}
