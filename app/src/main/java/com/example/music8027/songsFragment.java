package com.example.music8027;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.airbnb.lottie.LottieAnimationView;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class songsFragment extends Fragment {
    View view;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private GridLayoutManager gridLayoutManager;
    private List<Object> viewItems = new ArrayList<>();
    private ArrayList<JSONObject> searchResult = new ArrayList<JSONObject>();
    private List<String> songsList;
    private static final String TAG = "songsFragment";
    private MaterialButton detAdd, detClose, songList, songSuggest, albumList, artistList, artistSuggest, reload;
    private LottieAnimationView loadingAnimation, noResult;
    private MaterialCardView detCard;
    private TextView detName, detInfo, fullText;
    private ImageView detImg;
    private String objectID = "";
    private List<String> keysToInclude = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_songs, container, false);

        mRecyclerView = view.findViewById(R.id.song_recycle_view2);
        mRecyclerView.setHasFixedSize(true);

        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {return 2;}
        });

        mAdapter = new RecyclerAdapter(getContext(), viewItems, "/songs", new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) throws JSONException {
                objectID = searchResult.get(position).getString("id");
                String objectUrl = "https://saavn.dev/api/songs/" + objectID;
                Log.e(TAG, objectUrl);
                new songsFragment.FetchSongDataTask().execute(objectUrl);
            }
        });

        layoutManager = gridLayoutManager;
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        detAdd = view.findViewById(R.id.detailsAdd2);
        detClose = view.findViewById(R.id.detailsClose2);
        detImg = view.findViewById(R.id.detailsImg2);
        detName = view.findViewById(R.id.detailsName2);
        detInfo = view.findViewById(R.id.detailsInfo2);
        fullText = view.findViewById(R.id.fullText2);
        loadingAnimation = view.findViewById(R.id.loading_animation2);
        noResult = view.findViewById(R.id.no_result_animation2);
        detCard = view.findViewById(R.id.detailsCard2);
        albumList = view.findViewById(R.id.albumList2);
        songList = view.findViewById(R.id.songList2);
        artistList = view.findViewById(R.id.artistList2);
        songSuggest = view.findViewById(R.id.songSuggest2);
        artistSuggest = view.findViewById(R.id.artistSuggest2);
        reload = view.findViewById(R.id.reload);


        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new songsFragment.FetchDataTask().execute("");
            }
        });
        detClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songSuggest.setVisibility(View.GONE);
                artistSuggest.setVisibility(View.GONE);
                artistList.setVisibility(View.GONE);
                albumList.setVisibility(View.GONE);
                songList.setVisibility(View.GONE);
                detCard.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        detAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (objectID != null && !objectID.isEmpty()) {
                    loadingAnimation.setVisibility(View.VISIBLE);
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    if (currentUser != null) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());
                        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    List<String> currentSongs = (List<String>) documentSnapshot.get("userSongs");
                                    if (currentSongs == null) {
                                        currentSongs = new ArrayList<>();
                                    }
                                    currentSongs.remove(objectID);
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("userSongs", currentSongs);
                                    userDocRef.update(updates)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "ObjectID removed from Firestore songs array successfully!");
                                                    Toast.makeText(requireContext(), "Removed song from user's list", Toast.LENGTH_SHORT).show();
                                                    loadingAnimation.setVisibility(View.GONE);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e(TAG, "Error updating document", e);
                                                    Toast.makeText(requireContext(), "Error while removing song", Toast.LENGTH_SHORT).show();
                                                    loadingAnimation.setVisibility(View.GONE);
                                                }
                                            });
                                } else {
                                    Log.d(TAG, "Document does not exist");
                                    Toast.makeText(requireContext(), "Firestore db error", Toast.LENGTH_SHORT).show();
                                    loadingAnimation.setVisibility(View.GONE);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error fetching document", e);
                                Toast.makeText(requireContext(), "Error fetching user's list", Toast.LENGTH_SHORT).show();
                                loadingAnimation.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                        loadingAnimation.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(requireContext(), "Song id null", Toast.LENGTH_SHORT).show();
                    loadingAnimation.setVisibility(View.GONE);
                }
            }
        });

        fetchData();
        return view;
    }

    private class FetchDataTask extends AsyncTask<String, Void, ArrayList<JSONObject>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            noResult.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            loadingAnimation.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<JSONObject> doInBackground(String... params) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());
                userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try {
                            if (documentSnapshot.exists()) {
                                searchResult.clear();
                                songsList = (List<String>) documentSnapshot.get("userSongs");
                                if (songsList == null) {
                                    Toast.makeText(requireContext(), "User's song list is empty", Toast.LENGTH_SHORT).show();
                                } else {
                                    for (String jsonString : songsList) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(jsonString);
                                            searchResult.add(jsonObject);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(requireContext(), "User data doesn't exist", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(requireContext(), "Error retrieving user songs", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error retrieving user songs", e);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Error retrieving user data", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error retrieving user document", e);
                    }
                });
            } else {
                Toast.makeText(requireContext(), "No user logged in", Toast.LENGTH_SHORT).show();
            }
            return searchResult;
        }

        @Override
        protected void onPostExecute(ArrayList<JSONObject> searchResult) {
            viewItems.clear();
            viewItems.addAll(searchResult);
            mAdapter.notifyDataSetChanged();
            loadingAnimation.setVisibility(View.GONE);
            if (searchResult.isEmpty())
                noResult.setVisibility(View.VISIBLE);
            else
                mRecyclerView.setVisibility(View.VISIBLE);
            Toast.makeText(requireContext(), "Fetched songs", Toast.LENGTH_SHORT).show();
        }
    }

    public void fetchData() {
        searchResult = new ArrayList<>();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());
            userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    try {
                        if (documentSnapshot.exists()) {
                            searchResult.clear();
                            List<String> songsList = (List<String>) documentSnapshot.get("userSongs");
                            if (songsList != null) {
                                for (String jsonString : songsList) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(jsonString);
                                        searchResult.add(jsonObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error retrieving user songs", e);
                    }
                }
            });
        }
    }

    public class FetchSongDataTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            noResult.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            loadingAnimation.setVisibility(View.VISIBLE);

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject songData = new JSONObject();
            try {
                String objectUrlString = params[0];
                URL objectUrl = new URL(objectUrlString);
                HttpURLConnection connection = (HttpURLConnection) objectUrl.openConnection();
                connection.setRequestMethod("GET");

                StringBuilder response = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                songData = new JSONObject(response.toString());

                connection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return songData;
        }

        @Override
        protected void onPostExecute(JSONObject songData) {
            detCard.setVisibility(View.VISIBLE);
            loadingAnimation.setVisibility(View.GONE);

            try{
                String tmpString = null, tmpString2 = null;
                JSONObject objectDetails;
                if ((Object)songData.get("data") instanceof JSONArray)
                    objectDetails = (JSONObject) songData.getJSONArray("data").get(0);
                else
                    objectDetails = (JSONObject) songData.getJSONObject("data");
                if (objectDetails.getString("type").equals("song")) {
                    if (objectDetails.has("artists") && objectDetails.has("album")) {
                        if (objectDetails.getJSONObject("artists").has("primary"))
                            tmpString = objectDetails.getJSONObject("artists").getJSONArray("primary").getJSONObject(0).getString("name");
                        if (objectDetails.has("album")) {
                            if (objectDetails.getJSONObject("album").has("name"))
                                tmpString2 = objectDetails.getJSONObject("album").getString("name");
                        }
                        detInfo.setText(String.format("%s - %s", tmpString, tmpString2));
                    } else if (objectDetails.has("artists")) {
                        if (objectDetails.getJSONObject("artists").has("primary"))
                            tmpString = objectDetails.getJSONObject("artists").getJSONArray("primary").getJSONObject(0).getString("name");
                        detInfo.setText(tmpString);
                    } else if (objectDetails.has("album")) {
                        if (objectDetails.getJSONObject("album").has("name"))
                            tmpString2 = objectDetails.getJSONObject("album").getString("name");
                        detInfo.setText(tmpString2);
                    } else
                        detInfo.setText("null");
                    songSuggest.setVisibility(View.VISIBLE);
                    artistList.setVisibility(View.VISIBLE);
                    albumList.setVisibility(View.VISIBLE);
                    detAdd.setVisibility(View.VISIBLE);
                }
                if (objectDetails.has("name"))
                    detName.setText(objectDetails.getString("name"));
                else
                    detName.setText("unknown");
                if (objectDetails.has("image")) {
                    int len = objectDetails.getJSONArray("image").length();
                    Picasso.get().load(objectDetails.getJSONArray("image").getJSONObject(len - 1).getString("url")).into(detImg);
                } else
                    Picasso.get().load(R.drawable.vinyl).into(detImg);
                fullText.setText(convertJsonToString(objectDetails, getKeyList()));
            } catch (JSONException e) {
                Log.e(TAG, "Error fetching data", e);
            }
        }
    }

    public String convertJsonToString(JSONObject jsonObject, List<String> keyList) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
            String key = it.next();
            if (keyList.contains(key) && jsonObject.has(key)) {
                try {
                    if (key.equals("availableLanguages") && jsonObject.has("availableLanguages")){
                        String langList = "";
                        JSONArray langArray = jsonObject.getJSONArray("availableLanguages");
                        for (int i = 0; i < langArray.length() ; i++){
                            if (langArray.getString(i).equals("unknown"))
                                continue;;
                            langList = langList + langArray.getString(i) + ", ";
                        }
                        if (!langList.isEmpty()) {
                            langList = langList.substring(0, langList.length()-2);
                            stringBuilder.append(key.toUpperCase()).append(": \n").append(langList).append("\n\n");
                        }
                    }
                    else
                        stringBuilder.append(key.toUpperCase()).append(": \n").append(jsonObject.get(key)).append("\n\n");
                } catch (JSONException e) {
                    Log.e(TAG, "Error fetching key", e);
                }
            }
        }

        if (stringBuilder.length() > 0) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }

        return stringBuilder.toString();
    }

    public List<String> getKeyList() {
        keysToInclude.add("name");
        keysToInclude.add("type");
        keysToInclude.add("year");
        keysToInclude.add("description");
        keysToInclude.add("releaseDate");
        keysToInclude.add("duration");
        keysToInclude.add("label");
        keysToInclude.add("explicitContent");
        keysToInclude.add("playCount");
        keysToInclude.add("songCount");
        keysToInclude.add("followerCount");
        keysToInclude.add("fanCount");
        keysToInclude.add("isVerified");
        keysToInclude.add("dominantLanguage");
        keysToInclude.add("dominantType");
        keysToInclude.add("availableLanguages");
        keysToInclude.add("copyright");

        return keysToInclude;
    }
}