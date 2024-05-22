package com.example.music8027;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
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
    private Toast toast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationEmail();
            }
        });
    }

    private void sendVerificationEmail() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String passwordConf = editTextConfirmPassword.getText().toString().trim();

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

        if (TextUtils.isEmpty(passwordConf)) {
            toast = Toast.makeText(SignUpActivity.this, "Password cannot be empty",
                    Toast.LENGTH_SHORT);
            editTextConfirmPassword.setError("Password is required");
            editTextConfirmPassword.requestFocus();
            toast.show();
            return;
        }

        if (!password.equals(passwordConf)) {
            toast = Toast.makeText(SignUpActivity.this, "Password dont match",
                    Toast.LENGTH_SHORT);
            editTextPassword.setError("Passwords dont match");
            editTextConfirmPassword.setError("Passwords dont match");
            editTextPassword.requestFocus();
            editTextConfirmPassword.requestFocus();
            toast.show();
            return;
        }

        toggleGroup.setVisibility(View.GONE);
        loadingAnimation.setVisibility(View.VISIBLE);

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
                            // Handle different authentication errors
                            Exception exception = task.getException();
                            if (exception instanceof FirebaseAuthUserCollisionException) {
                                // ReCAPTCHA verification required
                                showReCAPTCHAPrompt();
                            } else {
                                toast = Toast.makeText(SignUpActivity.this, "Failed to create account. Please try again", Toast.LENGTH_SHORT);
                                toast.show();
                                toggleGroup.setVisibility(View.VISIBLE);
                                loadingAnimation.setVisibility(View.GONE);
                            }
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

    private void showReCAPTCHAPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Verification Required");
        builder.setMessage("Please complete the reCAPTCHA verification to continue.");
        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Initiate reCAPTCHA verification
                sendVerificationEmail();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle cancellation, if necessary
            }
        });
        builder.show();
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

        db.collection("userSongs")
                .document(mAuth.getCurrentUser().getUid())
                .set(user);

        db.collection("userAlbums")
                .document(mAuth.getCurrentUser().getUid())
                .set(user);

        db.collection("userArtists")
                .document(mAuth.getCurrentUser().getUid())
                .set(user);

        db.collection("userPlaylists")
                .document(mAuth.getCurrentUser().getUid())
                .set(user);
    }

    @Override
    public void onBackPressed() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && !user.isEmailVerified()) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                toggleGroup.setVisibility(View.VISIBLE);
                                loadingAnimation.setVisibility(View.GONE);
                                if (toast != null)
                                    toast.cancel();
                                Log.d(TAG, "User deleted");
                                toast = Toast.makeText(SignUpActivity.this, "Sign up canceled", Toast.LENGTH_SHORT);
                                toast.show();
                            } else {
                                if (toast != null)
                                    toast.cancel();
                                Log.w(TAG, "Error deleting user", task.getException());
                                toast = Toast.makeText(SignUpActivity.this, "Clean up error", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    });
        } else {
            super.onBackPressed();
        }
    }
}
