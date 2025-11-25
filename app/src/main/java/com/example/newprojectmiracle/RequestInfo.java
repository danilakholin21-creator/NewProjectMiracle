package com.example.newprojectmiracle;

public class RequestInfo {
    private long id;
    private String text;

    public RequestInfo(long id, String text) {
        this.id = id;
        this.text = text;
    }

    public long getId() { return id; }
    public String getText() { return text; }
}