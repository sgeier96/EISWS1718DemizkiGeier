package com.example.stefangeier.intime;

import java.util.ArrayList;

/**
 * Created by Stefan Geier on 21.11.2017.
 */

class Schedule {
    private ArrayList<Activity> activities = new ArrayList<>();

    public void add(Activity activity){
        activities.add(activity);
    }

    public void remove(Activity activity){
        activities.remove(activity);
    }
}
