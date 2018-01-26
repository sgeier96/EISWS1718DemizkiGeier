package com.example.stefangeier.intime;


/**
 * Created by Stefan Geier on 21.11.2017.
 */

class User {
    private String username;
    private Schedule currentSchedule;
    private String android_id;

    User(String username, String android_id){
        this.username = username;
        this.android_id = android_id;
    }

    public Schedule getSchedule(){
        return currentSchedule;
    }
    public void setSchedule(Schedule passedSchedule){
        this.currentSchedule = passedSchedule;
    }
    public String getUsername(){
        return username;
    }
    public void setUsername(String passedUsername){
        this.username = passedUsername;
    }
}
