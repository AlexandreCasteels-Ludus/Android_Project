package com.example.projectbeer;

import android.media.Image;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Date;

public class Beer implements Serializable {

    public Beer(){};

    //General Data
        String name;
        String type;
        String brewery;
        float percent;

        Beer(String a_name, String a_type, String a_brewery, float a_percent){
            name = a_name;
            type = a_type;
            brewery = a_brewery;
            percent = a_percent;
        }

        //Personal Data
        int container;
        float volume;
        LatLng consumption_place;

        String comment;
        float rating;
        String image;
        Date date;

        Beer(int a_container, float a_volume, LatLng a_consumption_place, String a_comment, float a_rating, String a_image, Date a_date){
            container = a_container;
            volume = a_volume;
            consumption_place = a_consumption_place;
            comment = a_comment;
            rating = a_rating;
            image = a_image;
            date = a_date;
        }

    }

