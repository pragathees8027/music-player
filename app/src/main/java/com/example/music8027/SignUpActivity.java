package com.example.music8027;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.apache.commons.lang3.RandomStringUtils;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    private EditText editTextName, editTextEmail, editTextPassword, editTextotp;
    private FirebaseFirestore db;
    private LottieAnimationView loadingAnimation;
    private MaterialButtonToggleGroup toggleGroup;
    private Toast toast = null;
    private DataManager dataManager;
    private  String otpString = null;
    private  boolean oldUser = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        db = FirebaseFirestore.getInstance();
        dataManager = DataManager.getInstance(this);

        editTextName = findViewById(R.id.userName);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextotp = findViewById(R.id.confirmPassword);
        toggleGroup = findViewById(R.id.loginToggleGroup);
        MaterialButton signUpButton = findViewById(R.id.signUpButton);
        MaterialButton loginButton = findViewById(R.id.loginButton);
        MaterialButton otpButton = findViewById(R.id.otpButton);
        loadingAnimation = findViewById(R.id.login_animation);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        otpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationEmail();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                if (toast != null)
                    toast.cancel();

                if (TextUtils.isEmpty(name)) {
                    toast = Toast.makeText(SignUpActivity.this, "Name cannot be empty",
                            Toast.LENGTH_SHORT);
                    toast.show();
                    editTextName.setError("Name is required");
                    editTextName.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    toast = Toast.makeText(SignUpActivity.this, "Email cannot be empty",
                            Toast.LENGTH_SHORT);
                    toast.show();
                    editTextEmail.setError("Email is required");
                    editTextEmail.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    toast = Toast.makeText(SignUpActivity.this, "Password cannot be empty",
                            Toast.LENGTH_SHORT);
                    editTextPassword.setError("Password is required");
                    editTextPassword.requestFocus();
                    toast.show();
                    return;
                }

                if (editTextotp.getText().toString().equals(otpString)) {
                    addUserToFirestore();
                    dataManager.setUserID(email);
                }
                Log.w(TAG, otpString);
            }
        });
    }

    private void sendVerificationEmail() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            toast = Toast.makeText(SignUpActivity.this, "Name cannot be empty",
                    Toast.LENGTH_SHORT);
            toast.show();
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            toast = Toast.makeText(SignUpActivity.this, "Email cannot be empty",
                    Toast.LENGTH_SHORT);
            toast.show();
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            toast = Toast.makeText(SignUpActivity.this, "Password cannot be empty",
                    Toast.LENGTH_SHORT);
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            toast.show();
            return;
        }

        DocumentReference userDocRef = db.collection("users").document(email);
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        oldUser = document.exists();
                        Log.d(TAG, "User already exists!");

                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });

        if (oldUser) {
            if (toast != null)
                toast.cancel();
            toast = Toast.makeText(SignUpActivity.this, "User already exists", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        String subject = "Email Verification";
        String msg = "OTP: ";
        otpString = RandomStringUtils.random(5, true, true);

        SendEmailTask sendEmailTask = new SendEmailTask(name, email, subject, otpString, msg, 5);
        sendEmailTask.execute();

        if (toast != null)
            toast.cancel();

        toast = Toast.makeText(SignUpActivity.this, "Sending email...", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void addUserToFirestore() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("password", password);

        db.collection("users")
                .document(email)
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (toast != null)
                                toast.cancel();
                            toast = Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT);
                            toast.show();
                            Log.d(TAG, "User details added to Firestore");
                            Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            finish();
                        } else {
                            if (toast != null)
                                toast.cancel();
                            Log.w(TAG, "Error adding user details to Firestore", task.getException());
                            toast = Toast.makeText(SignUpActivity.this, "Error adding user details to Firestore", Toast.LENGTH_SHORT);
                            toast.show();
                            toggleGroup.setVisibility(View.VISIBLE);
                            loadingAnimation.setVisibility(View.GONE);
                        }
                    }
                });

        Map<String, Object> userSongs = new HashMap<>();
        userSongs.put("songs", new ArrayList<String>() { });

        db.collection("userSongs")
                .document(email)
                .set(userSongs);

        Map<String, Object> userAlbums = new HashMap<>();
        userAlbums.put("albums", new ArrayList<String>() { });

        db.collection("userAlbums")
                .document(email)
                .set(userAlbums);

        Map<String, Object> userArtists = new HashMap<>();
        userArtists.put("artists", new ArrayList<String>() { });

        db.collection("userArtists")
                .document(email)
                .set(userArtists);

        Map<String, Object> userPlaylists = new HashMap<>();
        userPlaylists.put("playlists", new ArrayList<String>() { });

        db.collection("userPlaylists")
                .document(email)
                .set(userPlaylists);
    }
}
