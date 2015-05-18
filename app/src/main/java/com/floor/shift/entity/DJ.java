package com.floor.shift.entity;

/*
    Copyright (C) 2015 SeniorPanda
    Created by SeniorPanda on 1/15/2015.
*/

public class DJ {
    public int id;

    public String name;
    public String gender;
    public String image_url;
    public String info;
    public String fb_info;
    public String tw_info;
    public String email;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getFb_info() {
        return fb_info;
    }

    public void setFb_info(String fb_info) {
        this.fb_info = fb_info;
    }

    public String getTw_info() {
        return tw_info;
    }

    public void setTw_info(String tw_info) {
        this.tw_info = tw_info;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
