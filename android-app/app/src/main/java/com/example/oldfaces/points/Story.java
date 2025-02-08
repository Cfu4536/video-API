package com.example.oldfaces.points;

public class Story {
    String title;
    String path;

    public Story(String path) {
        this.path = path;
        this.title = path.substring(path.lastIndexOf("/") + 1, path.length() - 5);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
