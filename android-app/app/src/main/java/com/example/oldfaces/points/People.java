package com.example.oldfaces.points;

public class People {
    String name;
    String dir;
    int src;
    int count;

    public People(String name, String dir, int src, int count) {
        this.name = name;
        this.src = src;
        this.count = count;
        this.dir = dir;
    }
    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getDir() {
        return dir;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setSrc(int src) {
        this.src = src;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public int getSrc() {
        return src;
    }

    public int getCount() {
        return count;
    }
}
