package com.example.music8027;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class settingsFragment extends Fragment {

    private static final String TAG = "SignUpActivity";
    private EditText editTextName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private FirebaseFirestore db;
    private LottieAnimationView loadingAnimation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        editTextName = view.findViewById(R.id.userName);
        editTextEmail = view.findViewById(R.id.email);
        editTextPassword = view.findViewById(R.id.password);
        editTextConfirmPassword = view.findViewById(R.id.confirmPassword);

        MaterialButtonToggleGroup toggleGroup = view.findViewById(R.id.loginToggleGroup);
        MaterialButton updateButton = view.findViewById(R.id.updateButton);
        MaterialButton logoutButton = view.findViewById(R.id.logoutButton);
        MaterialButton calcButton = view.findViewById(R.id.calculatorButton);
        loadingAnimation = view.findViewById(R.id.login_animation);

        fetchUserData();
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        calcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).replaceFragment(new calcFragment());
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleGroup.setVisibility(View.GONE);
                loadingAnimation.setVisibility(View.VISIBLE);
                updateUserDetails();
                toggleGroup.setVisibility(View.VISIBLE);
                loadingAnimation.setVisibility(View.GONE);
            }
        });

        return view;
    }

    private void updateUserDetails() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(getActivity(), "Name and Email are required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            if (!password.isEmpty() && !password.equals(confirmPassword)) {
                Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            currentUser.updateEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!password.isEmpty()) {
                                currentUser.updatePassword(password)
                                        .addOnCompleteListener(passwordUpdateTask -> {
                                            if (passwordUpdateTask.isSuccessful()) {
                                                Toast.makeText(getActivity(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getActivity(), "Failed to update password: " + passwordUpdateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            updateUserDocument(name, email);
                        } else {
                            Toast.makeText(getActivity(), "Failed to update email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateUserDocument(String name, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);

        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .update(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "User details updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating user details: " + e.getMessage());
                    Toast.makeText(getActivity(), "Failed to update user details", Toast.LENGTH_SHORT).show();
                });
    }


    private void fetchUserData() {
        if (currentUser != null) {
            db.collection("users")
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("email");
                            editTextName.setText(name);
                            editTextEmail.setText(email);
                            Toast.makeText(getActivity(), "Fetched user data", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "No such document");
                            Toast.makeText(getActivity(), "Error fetching user data, No such document", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching user data: " + e.getMessage());
                        Toast.makeText(getActivity(), "Error fetching user data", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getActivity(), "Not logged in", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "No user logged in");
        }
    }
}
