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
    private MaterialButton songBtn, albumBtn, artistBtn, playlistBtn, detAdd, detDel, detClose, songList, songSuggest, albumList, artistList, artistSuggest, reload;
    private LottieAnimationView loadingAnimation, noResult;
    private MaterialCardView detCard;
    private TextView detName, detInfo, fullText, header;
    private ImageView detImg;
    private String objectID = "";
    private List<String> keysToInclude = new ArrayList<>();
    private int objectPosition;
    private String collection = "userSongs", searchSpecifier = "", field = "songs";
    private Toast toast = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_songs, container, false);

        mRecyclerView = view.findViewById(R.id.song_recycle_view);
        mRecyclerView.setHasFixedSize(true);

        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                try {
                    JSONObject searchItem = searchResult.get(position);
                    if (searchItem.getString("type").equals("song"))
                        return 2;
                    return 1;
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        mAdapter = new RecyclerAdapter(getContext(), viewItems, searchSpecifier, new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) throws JSONException {
                objectPosition = position;
                objectID = searchResult.get(position).getString("id");
                String type = searchResult.get(position).getString("type");;
                String searchSpec = null;
                switch (type) {
                    case "song":
                        searchSpec = "songs/";
                        break;

                    case "album":
                        searchSpec = "albums?id=";
                        break;

                    case "artist":
                        searchSpec = "artists/";
                        break;

                    case "playlist":
                        searchSpec = "playlists?id=";
                        break;
                }
                String objectUrl = "https://saavn.dev/api/" + searchSpec + objectID;
                Log.e(TAG, objectUrl);
                new FetchSongDataTask().execute(objectUrl);
            }
        });

        layoutManager = gridLayoutManager;
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        songBtn = view.findViewById(R.id.songSearch);
        albumBtn = view.findViewById(R.id.albumSearch);
        artistBtn = view.findViewById(R.id.artistSearch);
        playlistBtn = view.findViewById(R.id.playlistSearch);
        detAdd = view.findViewById(R.id.detailsAdd);
        detDel = view.findViewById(R.id.detailsDel);
        detClose = view.findViewById(R.id.detailsClose);
        detImg = view.findViewById(R.id.detailsImg);
        detName = view.findViewById(R.id.detailsName);
        detInfo = view.findViewById(R.id.detailsInfo);
        fullText = view.findViewById(R.id.fullText);
        loadingAnimation = view.findViewById(R.id.loading_animation);
        noResult = view.findViewById(R.id.no_result_animation);
        detCard = view.findViewById(R.id.detailsCard);
        albumList = view.findViewById(R.id.albumList);
        songList = view.findViewById(R.id.songList);
        artistList = view.findViewById(R.id.artistList);
        songSuggest = view.findViewById(R.id.songSuggest);
        artistSuggest = view.findViewById(R.id.artistSuggest);
        reload = view.findViewById(R.id.reload);
        header = view.findViewById(R.id.home);

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerReload();
            }
        });

        songBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collection = "userSongs";
                field = "songs";
                header.setText(Character.toUpperCase(field.charAt(0)) + field.substring(1));
                songBtn.setIconTintResource(R.color.glass_red);
                albumBtn.setIconTintResource(R.color.teal);
                artistBtn.setIconTintResource(R.color.teal);
                playlistBtn.setIconTintResource(R.color.teal);
                triggerReload();
            }
        });

        albumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collection = "userAlbums";
                field = "albums";
                header.setText(Character.toUpperCase(field.charAt(0)) + field.substring(1));
                songBtn.setIconTintResource(R.color.teal);
                albumBtn.setIconTintResource(R.color.glass_red);
                artistBtn.setIconTintResource(R.color.teal);
                playlistBtn.setIconTintResource(R.color.teal);
                triggerReload();
            }
        });

        artistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collection = "userArtists";
                field = "artists";
                header.setText(Character.toUpperCase(field.charAt(0)) + field.substring(1));
                songBtn.setIconTintResource(R.color.teal);
                albumBtn.setIconTintResource(R.color.teal);
                artistBtn.setIconTintResource(R.color.glass_red);
                playlistBtn.setIconTintResource(R.color.teal);
                triggerReload();
            }
        });

        playlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collection = "userPlaylists";
                field = "playlists";
                header.setText(Character.toUpperCase(field.charAt(0)) + field.substring(1));
                songBtn.setIconTintResource(R.color.teal);
                albumBtn.setIconTintResource(R.color.teal);
                artistBtn.setIconTintResource(R.color.teal);
                playlistBtn.setIconTintResource(R.color.glass_red);
                triggerReload();
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
                triggerReload();
            }
        });

        detAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (objectID != null && !objectID.isEmpty()) {
                    JSONObject jsonObject = searchResult.get(objectPosition);
                    String collection = null;
                    String field = null;
                    String type = null;
                    try {
                        type = jsonObject.getString("type");
                    } catch (JSONException e) {
                        Log.d(TAG, "Error retrieving object type, ",e);
                    };

                    switch (type) {
                        case "song":
                            collection = "userSongs";
                            field = "songs";
                            break;

                        case "album":
                            collection = "userAlbums";
                            field = "albums";
                            break;

                        case "artist":
                            collection = "userArtists";
                            field = "artists";
                            break;

                        case "playlist":
                            collection = "userPlaylists";
                            field = "playlists";
                            break;
                    }
                    String jsonString = jsonObject.toString();
                    loadingAnimation.setVisibility(View.VISIBLE);
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    if (currentUser != null) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference userDocRef = db.collection(collection).document(currentUser.getUid());
                        String finalField = field;
                        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    List<String> currentSongs = (List<String>) documentSnapshot.get(finalField);
                                    if (currentSongs == null) {
                                        currentSongs = new ArrayList<>();
                                    }
                                    if (!currentSongs.contains(jsonString)) {
                                        currentSongs.add(jsonString);
                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put(finalField, currentSongs);
                                        userDocRef.update(updates)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        if (toast != null)
                                                            toast.cancel();
                                                        Log.d(TAG, "ObjectID added to Firestore songs array successfully!");
                                                        toast = Toast.makeText(requireContext(), "Added to user's list", Toast.LENGTH_SHORT);
                                                        toast.show();
                                                        loadingAnimation.setVisibility(View.GONE);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        if (toast != null)
                                                            toast.cancel();
                                                        Log.e(TAG, "Error updating document", e);
                                                        toast = Toast.makeText(requireContext(), "Error while adding", Toast.LENGTH_SHORT);
                                                        toast.show();
                                                        loadingAnimation.setVisibility(View.GONE);
                                                    }
                                                });
                                    } else {
                                        if (toast != null)
                                            toast.cancel();
                                        Log.d(TAG, "ObjectID already exists");
                                        toast = Toast.makeText(requireContext(), "Already present on user's list", Toast.LENGTH_SHORT);
                                        toast.show();
                                        loadingAnimation.setVisibility(View.GONE);
                                    }
                                } else {
                                    if (toast != null)
                                        toast.cancel();
                                    Log.d(TAG, "Document does not exist");
                                    toast = Toast.makeText(requireContext(), "Firestore db error", Toast.LENGTH_SHORT);
                                    toast.show();
                                    loadingAnimation.setVisibility(View.GONE);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if (toast != null)
                                    toast.cancel();
                                Log.e(TAG, "Error fetching document", e);
                                toast = Toast.makeText(requireContext(), "Error fetching user's list", Toast.LENGTH_SHORT);
                                toast.show();
                                loadingAnimation.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        if (toast != null)
                            toast.cancel();
                        toast = Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT);
                        toast.show();
                        loadingAnimation.setVisibility(View.GONE);
                    }
                } else {
                    if (toast != null)
                        toast.cancel();
                    toast = Toast.makeText(requireContext(), "Object id null", Toast.LENGTH_SHORT);
                    toast.show();
                    loadingAnimation.setVisibility(View.GONE);
                }
            }
        });

        detDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (objectID != null && !objectID.isEmpty()) {
                    loadingAnimation.setVisibility(View.VISIBLE);
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    JSONObject jsonObject = searchResult.get(objectPosition);
                    String jsonString = jsonObject.toString();
                    String collection = null;
                    String field = null;
                    String type = null;
                    try {
                        type = jsonObject.getString("type");
                    } catch (JSONException e) {
                        Log.d(TAG, "Error retrieving object type, ",e);
                    };

                    switch (type) {
                        case "song":
                            collection = "userSongs";
                            field = "songs";
                            break;

                        case "album":
                            collection = "userAlbums";
                            field = "albums";
                            break;

                        case "artist":
                            collection = "userArtists";
                            field = "artists";
                            break;

                        case "playlist":
                            collection = "userPlaylists";
                            field = "playlists";
                            break;
                    }

                    if (currentUser != null) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference userDocRef = db.collection(collection).document(currentUser.getUid());
                        String finalField = field;
                        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    List<String> currentSongs = (List<String>) documentSnapshot.get(finalField);
                                    if (currentSongs == null) {
                                        currentSongs = new ArrayList<>();
                                    }
                                    if (!currentSongs.contains(jsonString)){
                                        if (toast != null)
                                            toast.cancel();
                                        Log.d(TAG, "ObjectID not found it user's list");
                                        toast = Toast.makeText(requireContext(), "Item is not in user's list", Toast.LENGTH_SHORT);
                                        toast.show();
                                    } else {
                                        currentSongs.remove(jsonString);
                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put(finalField, currentSongs);
                                        userDocRef.update(updates)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        if (toast != null)
                                                            toast.cancel();
                                                        Log.d(TAG, "ObjectID removed from Firestore songs array successfully!");
                                                        toast = Toast.makeText(requireContext(), "Removed from user's list", Toast.LENGTH_SHORT);
                                                        toast.show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        if (toast != null)
                                                            toast.cancel();
                                                        Log.e(TAG, "Error updating document", e);
                                                        toast = Toast.makeText(requireContext(), "Error while removing", Toast.LENGTH_SHORT);
                                                        toast.show();
                                                    }
                                                });
                                    }
                                    loadingAnimation.setVisibility(View.GONE);
                                } else {
                                    if (toast != null)
                                        toast.cancel();
                                    Log.d(TAG, "Document does not exist");
                                    toast = Toast.makeText(requireContext(), "Firestore db error", Toast.LENGTH_SHORT);
                                    toast.show();
                                    loadingAnimation.setVisibility(View.GONE);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if (toast != null)
                                    toast.cancel();
                                Log.e(TAG, "Error fetching document", e);
                                toast = Toast.makeText(requireContext(), "Error fetching user's list", Toast.LENGTH_SHORT);
                                toast.show();
                                loadingAnimation.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        if (toast != null)
                            toast.cancel();
                        toast = Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT);
                        toast.show();
                        loadingAnimation.setVisibility(View.GONE);
                    }
                } else {
                    if (toast != null)
                        toast.cancel();
                    toast = Toast.makeText(requireContext(), "Object id null", Toast.LENGTH_SHORT);
                    toast.show();
                    loadingAnimation.setVisibility(View.GONE);
                }
            }
        });

        triggerReload();
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
                DocumentReference userDocRef = db.collection(params[0]).document(currentUser.getUid());
                userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try {
                            if (documentSnapshot.exists()) {
                                searchResult.clear();
                                songsList = (List<String>) documentSnapshot.get(params[1]);
                                if (songsList == null) {
                                } else {
                                    for (String jsonString : songsList) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(jsonString);
                                            searchResult.add(jsonObject);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    updateRecyclerView(searchResult);
                                }
                            } else {
                                if (toast != null)
                                    toast.cancel();
                                toast = Toast.makeText(requireContext(), "User data doesn't exist", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        } catch (Exception e) {
                            if (toast != null)
                                toast.cancel();
                            toast = Toast.makeText(requireContext(), "Error retrieving user list", Toast.LENGTH_SHORT);
                            toast.show();
                            Log.e(TAG, "Error retrieving user list", e);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (toast != null)
                            toast.cancel();
                        toast = Toast.makeText(requireContext(), "Error retrieving user data", Toast.LENGTH_SHORT);
                        toast.show();
                        Log.e(TAG, "Error retrieving user document", e);
                    }
                });
            } else {
                if (toast != null)
                    toast.cancel();
                toast = Toast.makeText(requireContext(), "No user logged in", Toast.LENGTH_SHORT);
                toast.show();
            }
            return searchResult;
        }

        private void updateRecyclerView(ArrayList<JSONObject> searchResult) {
            viewItems.clear();
            viewItems.addAll(searchResult);
            mAdapter.notifyDataSetChanged();
            loadingAnimation.setVisibility(View.GONE);
            if (searchResult.isEmpty()) {
                noResult.setVisibility(View.VISIBLE);
                if (toast != null)
                    toast.cancel();
                toast = Toast.makeText(requireContext(), "User's list is empty", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                if (toast != null)
                    toast.cancel();
                toast = Toast.makeText(requireContext(), "Fetched user data", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public void triggerReload() {
        new songsFragment.FetchDataTask().execute(collection, field);
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
            JSONObject songData = null;
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
                if ((Object)songData.get("data") instanceof  JSONArray)
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
                    detDel.setVisibility(View.VISIBLE);
                } else if (objectDetails.getString("type").equals("artist")) {
                    if (objectDetails.has("fanCount")) {
                        tmpString = objectDetails.getString("fanCount");
                        tmpString2 = objectDetails.getString("followerCount");
                        if (Integer.valueOf(tmpString) > Integer.parseInt(tmpString2))
                            detInfo.setText("Fans: " + tmpString);
                        else
                            detInfo.setText("Followers: " + tmpString2);
                    } else
                        detInfo.setText("null");
                    songList.setVisibility(View.VISIBLE);
                    albumList.setVisibility(View.VISIBLE);
                    artistSuggest.setVisibility(View.VISIBLE);
                    detAdd.setVisibility(View.VISIBLE);
                    detDel.setVisibility(View.VISIBLE);
                } else if (objectDetails.getString("type").equals("album")) {
                    if (objectDetails.has("description")) {
                        tmpString2 = objectDetails.getString("description");
                        detInfo.setText(tmpString2);
                    } else if (objectDetails.has("artists")) {
                        if (objectDetails.getJSONObject("artists").has("primary"))
                            tmpString = objectDetails.getJSONObject("artists").getJSONArray("primary").getJSONObject(0).getString("name");
                        detInfo.setText(tmpString);
                    } else
                        detInfo.setText("null");
                    songList.setVisibility(View.VISIBLE);
                    artistList.setVisibility(View.VISIBLE);
                    detAdd.setVisibility(View.VISIBLE);
                    detDel.setVisibility(View.VISIBLE);
                } else if (objectDetails.getString("type").equals("playlist")) {
                    if (objectDetails.has("description"))
                        detInfo.setText(objectDetails.getString("description"));
                    else
                        detInfo.setText("null");
                    songList.setVisibility(View.VISIBLE);
                    detAdd.setVisibility(View.VISIBLE);
                    detDel.setVisibility(View.VISIBLE);
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