package com.example.stefangeier.intime;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class newSchedule extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_TRACK_LOCATION = 0;
    Location initialLocation = null;
    Location finalTargetLocation = null;
    int hourDeadline = -1;
    int minuteDeadline = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_schedule);
        TextView scheduleNameHeader = findViewById(R.id.newSchedule_HeaderLabel);
        TextView nextScheduleNameTextView = findViewById(R.id.newSchedule_nextScheduleLabel);
        TextView nextScheduleTimeLeftTextView = findViewById(R.id.newSchedule_nextScheduleTime);

        TextView scheduleNameTextView = findViewById(R.id.newSchedule_scheduleNameLabel);
        TextView initialLocationTextView = findViewById(R.id.newSchedule_initialLocationLabel);
        TextView finalTargetLocationTextView = findViewById(R.id.newSchedule_FinalTargetLocationLabel);
        TextView timeOfArrivalTextView = findViewById(R.id.newSchedule_timeOfArrivalLabel);

        EditText scheduleNameEditText = findViewById(R.id.newSchedule_scheduleNameEditText);
        EditText initialLocationEditText = findViewById(R.id.newSchedule_initialLocationEditText);
        EditText finalTargetLocationEditText = findViewById(R.id.newSchedule_FinalTargetLocationEditText);

        Button initialLocationTrackBtn = findViewById(R.id.newSchedule_initialLocationTrackerBtn);
        Button finalTargetLocationSetBtn = findViewById(R.id.newSchedule_finalTargetLocationSetBtn);
        Button timeOfArrivalSetBtn = findViewById(R.id.newSchedule_timeOfArrivalBtn);

    }

    public void setFinalTargetLocation(View v) {
        //setting dummy-location to 'TH-Köln - Campus Gummersbach'
        finalTargetLocation = new Location(LocationManager.GPS_PROVIDER);
        finalTargetLocation.setLatitude(51.022974);
        finalTargetLocation.setLongitude(7.561998);
        Toast.makeText(getBaseContext(), "Using dummy-location", Toast.LENGTH_SHORT).show();
    }

    public void locatePosition(View v) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new InTimeLocationListener(getBaseContext());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("*********** IN locatePosition NO PERMISSION **************");
            Toast.makeText(this, "Please activate GPS", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_TRACK_LOCATION);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
        initialLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        System.out.println("IS GPS ENABLED: " + locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        System.out.println("IS NETWORK ENABLED: " + locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
        if (initialLocation == null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
            Log.d("GPS", "GPS Enabled");
            if (locationManager != null) {
                initialLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (initialLocation == null) {
                   Log.v("[ERROR]", "Cant get last known location");
                   return;
                }
            }
        }
        Toast.makeText(getBaseContext(), String.valueOf(initialLocation.getLatitude() + ", " + initialLocation.getLongitude()), Toast.LENGTH_SHORT).show();
    }

    public void enterOverviewScreen(View v){
        Intent intent = new Intent(v.getContext(), Overview.class);
        startActivity(intent);
        finish();
    }

    public void enterNewScheduleActivityScreen(View v) throws IOException {
        EditText scheduleNameEditText = findViewById(R.id.newSchedule_scheduleNameEditText);
        //EditText finalTargetLocationEditText = findViewById(R.id.newSchedule_FinalTargetLocationEditText);

        Intent intent = new Intent(v.getContext(), newSchedule_activities.class);
        String scheduleName = scheduleNameEditText.getText().toString();
        //String finalTargetLocation = finalTargetLocationEditText.getText().toString();

        if(!scheduleName.isEmpty() && initialLocation != null && finalTargetLocation != null && hourDeadline != -1 && minuteDeadline != -1){
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.GERMANY);
            List<Address> initialAddress = geocoder.getFromLocation(initialLocation.getLatitude(), initialLocation.getLongitude(), 1);
            intent.putExtra("scheduleName", scheduleName);
            intent.putExtra("initialLocation", initialLocation);
            intent.putExtra("finalTargetLocation", finalTargetLocation);
            intent.putExtra("hourDeadline", hourDeadline);
            intent.putExtra("minuteDeadline", minuteDeadline);

            startActivity(intent);
            finish();
        } else {
            String TAG = "[SCHEDULE INPUT WRONG] ";
            Toast.makeText(getApplicationContext(), "Insufficient input", Toast.LENGTH_SHORT).show();
        }
    }

    public void showTimePickerDialog(View v){
        final Button timeOfArrivalSetBtn = findViewById(R.id.newSchedule_timeOfArrivalBtn);

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hourDeadline = selectedHour;
                minuteDeadline = selectedMinute;
                String s = hourDeadline + ":" + minuteDeadline + " (24h-Mode)";
                timeOfArrivalSetBtn.setText(s);
            }
        }, hour, minute, true);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void enterOptionsScreen() {
        Toast.makeText(this, "Not yet implemented", Toast.LENGTH_SHORT).show();
    }

    public void enterLastScreen() {
        Toast.makeText(this, "Not yet implemented", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_TRACK_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    System.out.println("PERMISSION GRANTED!");
                    locatePosition(findViewById(R.id.newSchedule_initialLocationTrackerBtn));

                } else {

                    System.out.println("PERMISSION DENIED!");
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

}
