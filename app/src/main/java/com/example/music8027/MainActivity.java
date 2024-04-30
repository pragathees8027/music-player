package com.example.music8027;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.music8027.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    private String[] permissions = new String[7];
    private int i= 0;
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        replaceFragment(new homeFragment());

        permissions[i++] = Manifest.permission.READ_EXTERNAL_STORAGE;
        permissions[i++] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        permissions[i++] = Manifest.permission.INTERNET;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions[i++] = Manifest.permission.POST_NOTIFICATIONS;
            permissions[i++] = Manifest.permission.READ_MEDIA_AUDIO;
            permissions[i++] = Manifest.permission.READ_MEDIA_IMAGES;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                permissions[i++] = Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED;
            }
        }

        requestPermissions();

        mainBinding.mainBar.setOnItemSelectedListener(menuItem -> {
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(fragment.getClass().getName(), 0);

        if (!fragmentPopped) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                    .replace(R.id.mainFrame, fragment, fragment.getClass().getName())
                    .addToBackStack(fragment.getClass().getName())
                    .commit();
        }
    }

    public void requestPermissions() {
        boolean req = false;
        String[] getPermissions= new String[i];
        for (int j = 0; j < i; j++)
            getPermissions[j] = permissions[j];
        for (String s : getPermissions) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, s)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.
                    checkSelfPermission(MainActivity.this, s)
                    != PackageManager.PERMISSION_GRANTED) {
                req = true;
            }
        }
        if (req) {
            ActivityCompat.requestPermissions(MainActivity.this, getPermissions, 1);
        }
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + 300 > System.currentTimeMillis()) {
            Toast.makeText(getBaseContext(), "App closed", Toast.LENGTH_SHORT).show();
            finish();
        }
        super.onBackPressed();
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.mainFrame);
        if (currentFragment instanceof homeFragment) {
            mainBinding.mainBar.setSelectedItemId(R.id.bar_home);
        } else if (currentFragment instanceof searchFragment) {
            mainBinding.mainBar.setSelectedItemId(R.id.bar_search);
        } else if (currentFragment instanceof playFragment) {
            mainBinding.mainBar.setSelectedItemId(R.id.bar_playing);
        } else if (currentFragment instanceof songsFragment) {
            mainBinding.mainBar.setSelectedItemId(R.id.bar_songs);
        } else if (currentFragment instanceof settingsFragment) {
            mainBinding.mainBar.setSelectedItemId(R.id.bar_settings);
        } else {
            finish();
        }
        mBackPressed = System.currentTimeMillis();
    }
}
