package com.floor.shift.entity;

import com.parse.ParseGeoPoint;

import java.io.Serializable;
import java.util.Date;

public class Messages implements Serializable {
    public String id;
    public String img_url;
    public String message;
    public Date time;
    public ParseGeoPoint from_loc;
    public String user_id;
    public String to_loc;//FloorMap ID
    public boolean type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ParseGeoPoint getFrom_loc() {
        return from_loc;
    }

    public void setFrom_loc(ParseGeoPoint from_loc) {
        this.from_loc = from_loc;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTo_loc() {
        return to_loc;
    }

    public void setTo_loc(String to_loc) {
        this.to_loc = to_loc;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}