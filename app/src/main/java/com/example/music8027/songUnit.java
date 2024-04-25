package com.example.music8027;

import java.util.List;

public class songUnit {
    private String id;
    private String title;
    private String image;
    private String album;
    private String url;
    private String type;
    private String singers;
    private String language;
    private String songIDs;

    public songUnit(String id, String title, String image, String album, String url, String type, String singers, String language, String songIDs) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.album = album;
        this.url = url;
        this.type = type;
        this.singers = singers;
        this.language = language;
        this.songIDs = songIDs;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getAlbum() {
        return album;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public String getSingers() {
        return singers;
    }

    public String getLanguage() {
        return language;
    }

    public String getSongIDs() {
        return songIDs;
    }
}
