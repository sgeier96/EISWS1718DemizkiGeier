package com.example.stefangeier.intime;

import android.view.animation.AccelerateInterpolator;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Stefan Geier on 07.11.2017.
 */


class Activity{

    String activityName;
    int activityDuration;
    int priority;
    ArrayList<Resource> necessaryResources = new ArrayList<>();
    boolean parallelizable;
    int[] period;

    Activity(String activityName, int activityDuration, int priority){
        this.activityName = activityName;
        this.activityDuration = activityDuration;
        this.priority = priority;
    }
    Activity(String activityName, int activityDuration, int priority, Resource resource){
        this.activityName = activityName;
        this.activityDuration = activityDuration;
        this.priority = priority;
        this.necessaryResources.add(resource);
    }
    Activity(String activityName, int activityDuration, int priority, ArrayList<Resource> necessaryResources){
        this.activityName = activityName;
        this.activityDuration = activityDuration;
        this.priority = priority;
        this.necessaryResources = necessaryResources;
    }

    public void addResource(Resource resource){
        necessaryResources.add(resource);
    }

    public void setParallelizable(boolean bool){
        this.parallelizable = bool;
    }

    public void setPeriod(int[] passedPeriod){
        this.period = passedPeriod;
    }
}

