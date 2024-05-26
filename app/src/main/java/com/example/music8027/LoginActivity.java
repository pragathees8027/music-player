package com.example.music8027;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mailersend.sdk.emails.*;
import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.exceptions.MailerSendException;
import org.apache.commons.lang3.RandomStringUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private DataManager dataManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView forgotPassword;
    private EditText email, password;
    private LottieAnimationView loadingAnimation;
    private Toast toast = null;
    private static final String TAG = "LoginActivity";
    private String otpString = null;
    private String userPass = null;
    @Override
    public void onStart() {
        dataManager = DataManager.getInstance(LoginActivity.this);
        super.onStart();
        if(dataManager.getUserID() != null){
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        dataManager = DataManager.getInstance(LoginActivity.this);

        if (!isNetworkAvailable(getApplicationContext())) {
            if (toast != null)
                toast.cancel();
            toast = Toast.makeText(LoginActivity.this, "No internet",
                    Toast.LENGTH_SHORT);
            toast.show();
        }

        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.loginToggleGroup);
        MaterialButton loginButton = findViewById(R.id.loginButton);
        MaterialButton signUpButton = findViewById(R.id.signUpButton);
        loadingAnimation = findViewById(R.id.login_animation);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        forgotPassword = findViewById(R.id.forgotPassword);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailString = email.getText().toString().trim();

                if (TextUtils.isEmpty(emailString)) {
                    loadingAnimation.setVisibility(View.GONE);
                    forgotPassword.setVisibility(View.VISIBLE);
                    toggleGroup.setVisibility(View.VISIBLE);
                    if (toast != null)
                        toast.cancel();
                    toast = Toast.makeText(LoginActivity.this, "Enter your email to reset password", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                sendVerificationEmail(emailString);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailString, passwordString;
                emailString = email.getText().toString();
                passwordString = password.getText().toString();

                if (toast != null)
                    toast.cancel();

                if (TextUtils.isEmpty(emailString)) {
                    toast = Toast.makeText(LoginActivity.this, "Email cannot be empty",
                            Toast.LENGTH_SHORT);
                    toast.show();
                    email.setError("Email is required");
                    email.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(passwordString)) {
                    toast = Toast.makeText(LoginActivity.this, "Password cannot be empty",
                            Toast.LENGTH_SHORT);
                    password.setError("Password is required");
                    password.requestFocus();
                    toast.show();
                    return;
                }

                toggleGroup.setVisibility(View.GONE);
                forgotPassword.setVisibility(View.GONE);
                loadingAnimation.setVisibility(View.VISIBLE);

                db.collection("users")
                        .document(emailString)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                userPass = documentSnapshot.getString("password");
                            } else {
                                if (toast != null)
                                    toast.cancel();
                                Log.d(TAG, "Error fetching user data, No such document");
                                toast = Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            if (toast != null)
                                toast.cancel();
                            Log.e(TAG, "Error fetching user data: " + e.getMessage());
                            toast = Toast.makeText(LoginActivity.this, "Error fetching user data", Toast.LENGTH_SHORT);
                            toast.show();
                        });

                if (passwordString.equals(userPass)) {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    if (toast != null)
                        toast.cancel();
                    Log.e(TAG, "Invalid credentials");
                    toast = Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT);
                    toast.show();
                }
                toggleGroup.setVisibility(View.VISIBLE);
                forgotPassword.setVisibility(View.VISIBLE);
                loadingAnimation.setVisibility(View.GONE);
                dataManager.setUserID(emailString);
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void sendVerificationEmail(String emailString) {
        String subject = "Password Reset";
        String msg = "Your password has been reset.\nUse the below password to login and change your password in profile\nPassword: ";
        otpString = RandomStringUtils.random(8, true, true);
        updateUserPassword(otpString, emailString);
        SendEmailTask sendEmailTask = new SendEmailTask(emailString, emailString, subject, otpString, msg, 8);
        sendEmailTask.execute();

        if (toast != null)
            toast.cancel();

        toast = Toast.makeText(LoginActivity.this, "Sending email...", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void updateUserPassword(String password, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("password", password);
        user.put("email", email);

        db.collection("users")
                .document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        user.put("name", documentSnapshot.get("name"));
                    }
                });

        db.collection("users")
                .document(email)
                .update(user)
                .addOnSuccessListener(aVoid -> {
                    if (toast != null)
                        toast.cancel();
                    toast  = Toast.makeText(LoginActivity.this, "User details updated successfully", Toast.LENGTH_SHORT);
                    toast.show();
                })
                .addOnFailureListener(e -> {
                    if (toast != null)
                        toast.cancel();
                    Log.e(TAG, "Error updating user details: " + e.getMessage());
                    toast = Toast.makeText(LoginActivity.this, "Failed to update user details", Toast.LENGTH_SHORT);
                    toast.show();
                });
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    protected void onDestroy(){
        super.onDestroy();
    }
}