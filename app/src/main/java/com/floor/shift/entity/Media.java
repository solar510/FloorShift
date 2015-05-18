package com.floor.shift.entity;

/*
    Copyright (C) 2015 SeniorPanda
    Created by SeniorPanda on 1/15/2015.
*/

import java.io.Serializable;
import java.util.ArrayList;

public class Media implements Serializable {

    public ArrayList<Video> videoList = new ArrayList<Video>();
    public ArrayList<FloorMap> floorMapList = new ArrayList<FloorMap>();

    public ArrayList<Video> getVideoList() {
        return videoList;
    }

    public void setVideoList(ArrayList<Video> videoList) {
        this.videoList = videoList;
    }

    public ArrayList<FloorMap> getFloorMapList() {
        return floorMapList;
    }

    public void setFloorMapList(ArrayList<FloorMap> floorMapList) {
        this.floorMapList = floorMapList;
    }
}