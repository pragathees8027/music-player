package com.example.music8027;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    private EditText editTextName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private LottieAnimationView loadingAnimation;

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

        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.loginToggleGroup);
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
                signUp(v);
                toggleGroup.setVisibility(View.VISIBLE);
                loadingAnimation.setVisibility(View.GONE);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
    });
}

    public void signUp(View view) {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            addUserToFirestore(name, email);
                            Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        } else {
                            Toast.makeText(SignUpActivity.this, "Authentication failed. Try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addUserToFirestore(String name, String email) {
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
                            Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "User details added to Firestore");
                        } else {
                            Log.w(TAG, "Error adding user details to Firestore", task.getException());
                        }
                    }
                });
    }
}
