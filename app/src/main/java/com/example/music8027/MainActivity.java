package com.example.music8027;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.example.music8027.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding main_binding;
    String[] permission = new String[7];

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main_binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(main_binding.getRoot());
        replaceFragment(new homeFragment());

        permission[0] = Manifest.permission.READ_EXTERNAL_STORAGE;
        permission[1] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission[2] = Manifest.permission.POST_NOTIFICATIONS;
            permission[3] = Manifest.permission.READ_MEDIA_AUDIO;
            permission[4] = Manifest.permission.READ_MEDIA_IMAGES;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                permission[5] = Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED;
            }
        }
        permission[6] = Manifest.permission.INTERNET;

        requestPermission();

        main_binding.mainBar.setOnItemSelectedListener(menuItem -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.mainFrame);
             if (menuItem.getItemId() == R.id.bar_home){
                 if (currentFragment instanceof homeFragment){
                 } else {
                     replaceFragment(new homeFragment());
                 }
             } else if (menuItem.getItemId() == R.id.bar_search){
                 if (currentFragment instanceof searchFragment){
                 } else {
                     replaceFragment(new searchFragment());
                 }
             } else if (menuItem.getItemId() == R.id.bar_playing){
                 if (currentFragment instanceof playFragment){
                 } else {
                     replaceFragment(new playFragment());
                 }
             } else if (menuItem.getItemId() == R.id.bar_songs){
                 if (currentFragment instanceof songsFragment){
                 } else {
                     replaceFragment(new songsFragment());
                 }
             } else if (menuItem.getItemId() == R.id.bar_settings){
                 if (currentFragment instanceof settingsFragment){
                 } else {
                     replaceFragment(new settingsFragment());
                 }
             }
            return true;
        });
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragMan = getSupportFragmentManager();
        fragMan.beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                .replace(R.id.mainFrame, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void requestPermission() {
        boolean req = false;
        for (String s : permission) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, s)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.
                    checkSelfPermission(MainActivity.this, s)
                    != PackageManager.PERMISSION_GRANTED) {
                req = true;
            }
        }
        if (req){
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {permission[0],permission[1],permission[2],permission[3],permission[4],permission[5],permission[6]}, 1);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.mainFrame);
            if (currentFragment instanceof homeFragment){
                main_binding.mainBar.setSelectedItemId(R.id.bar_home);
            } else if (currentFragment instanceof searchFragment){
                main_binding.mainBar.setSelectedItemId(R.id.bar_search);
            } else if (currentFragment instanceof playFragment){
                main_binding.mainBar.setSelectedItemId(R.id.bar_playing);
            } else if (currentFragment instanceof songsFragment){
                main_binding.mainBar.setSelectedItemId(R.id.bar_songs);
            } else if (currentFragment instanceof settingsFragment){
                main_binding.mainBar.setSelectedItemId(R.id.bar_settings);
            } else {
                finish();
            }
        }
}
