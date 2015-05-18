package com.floor.shift.entity;

import com.parse.ParseGeoPoint;

import java.io.Serializable;
import java.util.ArrayList;

public class FloorMap implements Serializable {
    public String id;
    public String name;
    public String imgUrl;
    public ParseGeoPoint pos1, pos2, pos3, pos4;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public ParseGeoPoint getPos1() {
        return pos1;
    }

    public void setPos1(ParseGeoPoint pos1) {
        this.pos1 = pos1;
    }

    public ParseGeoPoint getPos2() {
        return pos2;
    }

    public void setPos2(ParseGeoPoint pos2) {
        this.pos2 = pos2;
    }

    public ParseGeoPoint getPos3() {
        return pos3;
    }

    public void setPos3(ParseGeoPoint pos3) {
        this.pos3 = pos3;
    }

    public ParseGeoPoint getPos4() {
        return pos4;
    }

    public void setPos4(ParseGeoPoint pos4) {
        this.pos4 = pos4;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}