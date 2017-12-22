package com.example.stefangeier.intime;

/**
 * Created by Stefan Geier on 21.11.2017.
 */

class Resource {
    private String name;
    private int maxUser;
    private String category; //Maybe...just maybe
    //to compare similar resources and give the user -while rearranging the schedule-
    //an alternative resource if the current one is being used.
    //For example the ability to consider various bathrooms, etc.

    Resource(String name, int maxUser){
        this.name = name;
        this.maxUser = maxUser;
    }
}
