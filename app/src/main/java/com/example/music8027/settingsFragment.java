package com.example.music8027;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class settingsFragment extends Fragment {
    private static final String TAG = "SignUpActivity";
    private EditText editTextName, editTextPassword, editTextConfirmPassword;
    private TextView editTextEmail;
    private DataManager dataManager;
    private FirebaseFirestore db;
    private LottieAnimationView loadingAnimation;
    private Toast toast = null;
    private String userPass = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        db = FirebaseFirestore.getInstance();
        dataManager = DataManager.getInstance(requireContext());

        editTextName = view.findViewById(R.id.userName);
        editTextEmail = view.findViewById(R.id.email);
        editTextPassword = view.findViewById(R.id.password);
        editTextConfirmPassword = view.findViewById(R.id.confirmPassword);

        MaterialButtonToggleGroup toggleGroup = view.findViewById(R.id.loginToggleGroup);
        MaterialButton updateButton = view.findViewById(R.id.updateButton);
        MaterialButton logoutButton = view.findViewById(R.id.logoutButton);
        MaterialButton calcButton = view.findViewById(R.id.calculatorButton);
        MaterialButton delete = view.findViewById(R.id.deleteAccountButton);
        loadingAnimation = view.findViewById(R.id.login_animation);

        fetchUserData();
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataManager.setUserID(null);
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

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = editTextPassword.getText().toString().trim();
                String confirmPassword = editTextConfirmPassword.getText().toString().trim();

                if (!confirmPassword.equals(userPass) && !password.equals(userPass)) {
                    editTextPassword.setError("Wrong password");
                    editTextConfirmPassword.setError("Wrong password");
                    return;
                }

                toggleGroup.setVisibility(View.GONE);
                loadingAnimation.setVisibility(View.VISIBLE);
                deleteUserData();
                toggleGroup.setVisibility(View.VISIBLE);
                loadingAnimation.setVisibility(View.GONE);
                dataManager.setUserID(null);
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        return view;
    }

    private void updateUserDetails() {
        String name = editTextName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String email = editTextEmail.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (name.isEmpty()) {

            if (toast != null)
                toast.cancel();
            editTextName.setError("Required");
            return;
        }

        if (!confirmPassword.equals(password)) {
            if (toast != null)
                toast.cancel();
            editTextConfirmPassword.setError("Password doesn't match");
            return;
        }

        Map<String, Object> user = new HashMap<>();
        if (password.isEmpty())
            user.put("password", userPass);
        else
            user.put("password", password);
        user.put("email", email);
        user.put("name", name);

        db.collection("users")
                .document(email)
                .update(user)
                .addOnSuccessListener(aVoid -> {
                    if (toast != null)
                        toast.cancel();
                    toast  = Toast.makeText(getActivity(), "User details updated successfully", Toast.LENGTH_SHORT);
                    toast.show();
                })
                .addOnFailureListener(e -> {
                    if (toast != null)
                        toast.cancel();
                    Log.e(TAG, "Error updating user details: " + e.getMessage());
                    toast = Toast.makeText(getActivity(), "Failed to update user details", Toast.LENGTH_SHORT);
                    toast.show();
                });
    }

    private void deleteUserData() {
        ArrayList<DocumentReference> userData = new ArrayList<DocumentReference>();
        userData.add(db.collection("users").document(dataManager.getUserID()));
        userData.add(db.collection("userSongs").document(dataManager.getUserID()));
        userData.add(db.collection("userAlbums").document(dataManager.getUserID()));
        userData.add(db.collection("userArtists").document(dataManager.getUserID()));
        userData.add(db.collection("userPlaylists").document(dataManager.getUserID()));

        for (DocumentReference docRef : userData) {
            try {
                docRef.delete();
                Log.d(TAG, "Document deleted successfully: " + docRef.getId());
            } catch (Exception e) {
                Log.d(TAG, "Error deleting document: " + docRef.getId());
            }
        }

        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(getActivity(), "Deleted user account", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void fetchUserData() {
        if (dataManager.getUserID() != null) {
            db.collection("users")
                    .document(dataManager.getUserID())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("email");
                            userPass = documentSnapshot.getString("password");
                            editTextName.setText(name);
                            editTextEmail.setText(email);
                            if (toast != null)
                                toast.cancel();
                            toast = Toast.makeText(getActivity(), "Fetched user data", Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            if (toast != null)
                                toast.cancel();
                            Log.d(TAG, "No such document");
                            toast = Toast.makeText(getActivity(), "Error fetching user data, No such document", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (toast != null)
                            toast.cancel();
                        Log.e(TAG, "Error fetching user data: " + e.getMessage());
                        toast = Toast.makeText(getActivity(), "Error fetching user data", Toast.LENGTH_SHORT);
                        toast.show();
                    });
        } else {
            if (toast != null)
                toast.cancel();
            toast = Toast.makeText(getActivity(), "Not logged in", Toast.LENGTH_SHORT);
            toast.show();
            Log.d(TAG, "No user logged in");
        }
    }
}
