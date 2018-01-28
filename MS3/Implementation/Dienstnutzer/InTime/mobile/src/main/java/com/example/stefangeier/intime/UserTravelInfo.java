package com.example.stefangeier.intime;

/**
 * Created by Stefan Geier on 28.01.2018.
 */

public class UserTravelInfo {
    public String Ankunftszeit;
    public String Abfahrtszeit = "test";
    public String Ankunftsort = "37";
    public String Abfahrtsort = "8";

    public UserTravelInfo(String sBusArrivalHour, String sBusArrivalMinute){
        Ankunftszeit = "2018-01-29T" + sBusArrivalHour + ":" + sBusArrivalMinute + ":00+01:00";
    }
}
