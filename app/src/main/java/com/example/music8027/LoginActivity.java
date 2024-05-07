package com.example.music8027;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private TextView forgotPassword;
    private EditText email, password;
    private LottieAnimationView loadingAnimation;
    private Toast toast = null;
    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        if (!isNetworkAvailable(getApplicationContext())) {
            if (toast != null)
                toast.cancel();
            toast = Toast.makeText(LoginActivity.this, "No internet",
                    Toast.LENGTH_SHORT);
            toast.show();
        }

        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.loginToggleGroup);
        MaterialButton loginButton = findViewById(R.id.loginButton);
        MaterialButton singUpButton = findViewById(R.id.signUpButton);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loadingAnimation = findViewById(R.id.login_animation);
        forgotPassword = findViewById(R.id.forgotPassword);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleGroup.setVisibility(View.GONE);
                forgotPassword.setVisibility(View.GONE);
                loadingAnimation.setVisibility(View.VISIBLE);

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

                mAuth.sendPasswordResetEmail(emailString)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                loadingAnimation.setVisibility(View.GONE);
                                forgotPassword.setVisibility(View.VISIBLE);
                                toggleGroup.setVisibility(View.VISIBLE);
                                if (toast != null)
                                    toast.cancel();
                                if (task.isSuccessful()) {
                                    toast = Toast.makeText(LoginActivity.this, "Password reset email sent. Check your email inbox.", Toast.LENGTH_SHORT);
                                } else {
                                    toast = Toast.makeText(LoginActivity.this, "Failed to send password reset email. Please try again.", Toast.LENGTH_SHORT);
                                }
                                toast.show();
                            }
                        });
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

                mAuth.signInWithEmailAndPassword(emailString, passwordString)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    loadingAnimation.setVisibility(View.GONE);
                                    forgotPassword.setVisibility(View.VISIBLE);
                                    toggleGroup.setVisibility(View.VISIBLE);
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Handler handler1 = new Handler();
                                            handler1.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    toast = Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT);
                                                    toast.show();
                                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                                    startActivity(i);
                                                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                                }
                                            }, 1000);
                                        }
                                    }, 2000);
                                } else {
                                    Handler handler2 = new Handler();
                                    handler2.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Handler handler3 = new Handler();
                                            handler3.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    toast = Toast.makeText(LoginActivity.this, "Invalid credentials.",
                                                            Toast.LENGTH_SHORT);
                                                    toast.show();
                                                }
                                            },1000);

                                        }
                                    },2000);
                                }
                            }
                        });
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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
}