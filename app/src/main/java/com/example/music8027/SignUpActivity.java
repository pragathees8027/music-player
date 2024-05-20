package com.example.music8027;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    private EditText editTextName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private LottieAnimationView loadingAnimation;
    private MaterialButtonToggleGroup toggleGroup;
    private  Toast toast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextName = findViewById(R.id.userName);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextConfirmPassword = findViewById(R.id.confirmPassword);
        toggleGroup = findViewById(R.id.loginToggleGroup);
        MaterialButton signUpButton = findViewById(R.id.signUpButton);
        MaterialButton loginButton = findViewById(R.id.loginButton);
        loadingAnimation = findViewById(R.id.login_animation);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleGroup.setVisibility(View.GONE);
                loadingAnimation.setVisibility(View.VISIBLE);
                sendVerificationEmail();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void sendVerificationEmail() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mAuth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> verificationTask) {
                                            if (verificationTask.isSuccessful()) {
                                                if (toast != null)
                                                    toast.cancel();
                                                toast = Toast.makeText(SignUpActivity.this, "Verification email sent", Toast.LENGTH_SHORT);
                                                toast.show();
                                                toggleGroup.setVisibility(View.GONE);
                                                loadingAnimation.setVisibility(View.VISIBLE);
                                                checkEmailVerificationStatus();
                                            } else {
                                                if (toast != null)
                                                    toast.cancel();
                                                toast = Toast.makeText(SignUpActivity.this, "Failed to send verification email", Toast.LENGTH_SHORT);
                                                toast.show();
                                                toggleGroup.setVisibility(View.VISIBLE);
                                                loadingAnimation.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        } else {
                            if (toast != null)
                                toast.cancel();
                            toast = Toast.makeText(SignUpActivity.this, "Failed to create account. Please try again", Toast.LENGTH_SHORT);
                            toast.show();
                            toggleGroup.setVisibility(View.VISIBLE);
                            loadingAnimation.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void checkEmailVerificationStatus() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    user.reload();
                    if (user.isEmailVerified()) {
                        addUserToFirestore(editTextName.getText().toString().trim(), editTextEmail.getText().toString().trim());
                    } else {
                        checkEmailVerificationStatus();
                    }
                }
            }
        }, 2000);
    }

    public void addUserToFirestore(String name, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);

        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}
