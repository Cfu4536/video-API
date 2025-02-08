package com.example.oldfaces.points;

public class Video {
    String name;
    String path;
    String srcPic;
    int size = 0;
    int time = 0;
    String type = "video";
    Boolean isFavorite = false;

    public Video(String name, String path, int size) {
        this.name = name;
        this.path = path;
        this.size = size;
    }

    public Video(String name, String path, int size, String type) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.type = type;
        if (!(type.equals("video") || type.equals("dir") || type.equals("on-video"))) {
            throw new IllegalArgumentException("文件类型错误");
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public Boolean getFavorite() {
        return isFavorite;
    }

    public void setFavorite(Boolean favorite) {
        isFavorite = favorite;
    }

    public String getSrcPic() {
        return srcPic;
    }

    public void setSrcPic(String srcPic) {
        this.srcPic = srcPic;
    }
}
