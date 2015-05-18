package com.floor.shift.entity;

/*
    Copyright (C) 2015 SeniorPanda
    Created by SeniorPanda on 1/15/2015.
*/

import java.util.ArrayList;

public class User {
    public int id;
    public String nickname;
    public String password;
    public String email;
    public String profile_url;
    public String gender = "male";
    public String floorMap_id;
    public ArrayList<Messages> favoriteSongs = new ArrayList<Messages>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public ArrayList<Messages> getFavoriteSongs() {
        return favoriteSongs;
    }

    public void setFavoriteSongs(ArrayList<Messages> favoriteSongs) {
        this.favoriteSongs = favoriteSongs;
    }

    public String getFloorMap_id() {
        return floorMap_id;
    }

    public void setFloorMap_id(String floorMap_id) {
        this.floorMap_id = floorMap_id;
    }
}
