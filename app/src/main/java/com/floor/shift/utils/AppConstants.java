package com.floor.shift.utils;

/*
    Copyright (C) 2015 SeniorPanda
    Created by SeniorPanda on 2/15/2015.
*/

import com.floor.shift.entity.DJ;
import com.floor.shift.entity.FloorMap;
import com.floor.shift.entity.Media;
import com.floor.shift.entity.Messages;

import java.util.ArrayList;

public class AppConstants {
    public static String PARSE_APP_ID = "8GY9gtFGlqncz9J30voWxG53y1gHnltu0ZBHXkOm";
    public static String PARSE_CLIENT_KEY = "GIJURfYdPVchD9oaq9hLoaRhWTMm77qaQqzTYvms";

    public static final boolean ENABLE_LOG = true;
    public static String URL_BASE = "http://streamcomediaapps.com/index.php";
    public static String URL_IMG_CROP = "http://thumbor.soundstation.com/unsafe/%dx%d/filters:format(jpeg)/%s";

    public static Media media = null;
    public static DJ SELECTED_DJ = new DJ();
    public static String URL_FB = "http://facebook.com";
    public static String URL_TWITTER = "http://twitter.com";
    /*vlaues for playing*/
    public static boolean IS_PLAYING = true;
    public static Messages Current_Song = new Messages();

    public static ArrayList<Messages> PlayList = new ArrayList<Messages>();

    public static double FT = 0.3048; // 1 ft = 0.3048 m
    public static double curLat, curLng;

    public static int CURRENT_FRAGMENT = 6;

    public static ArrayList<FloorMap> CurrentFloorMaps = new ArrayList<FloorMap>();

}
