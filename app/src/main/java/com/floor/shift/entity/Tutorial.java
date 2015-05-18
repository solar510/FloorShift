package com.floor.shift.entity;

/*
    Copyright (C) 2015 SeniorPanda
    Created by SeniorPanda on 1/15/2015.
*/

import java.io.Serializable;

public class Tutorial implements Serializable{
    public int id;
    public String name;
    public String image_url;

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

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
