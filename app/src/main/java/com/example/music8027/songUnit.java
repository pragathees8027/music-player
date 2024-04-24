package com.example.music8027;

public class songUnit {
    private String name = null;
    private String id = null;
    private String art = null;

    public songUnit(String name, String art, String id) {
        this.id = id;
        this.name = name;
        this.art = art;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getArt() {
        return art;
    }
}
