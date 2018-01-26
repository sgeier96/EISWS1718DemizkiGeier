package com.example.stefangeier.intime;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Stefan Geier on 24.01.2018
 * with the help of
 * https://stackoverflow.com/questions/1513485/how-do-i-get-the-current-gps-location-programmatically-in-android
 */

public class InTimeLocationListener implements LocationListener {

    Context context = null;

    public InTimeLocationListener(Context context){
        this.context = context;
    }
    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        /*--Getting city name from coordinates--*/
        String cityName = null;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        System.out.println("*********** IN OnLocationChanged **************");
        try {
            System.out.println("*********** IN OnLocationChanged TRY**************");
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0){
                System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
                System.out.println("*********** IN OnLocationChanged ADDRESSES > 0**************");
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        String[] result = new String[3];

        result[0] = String.valueOf(longitude);
        result[1] = String.valueOf(latitude);
        result[2] = cityName;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

}
