package com.example.stefangeier.intime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class scheduleScreen extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_screen);
    }

    public void enterOverviewScreen(View v){
        Intent intent = new Intent(v.getContext(), Overview.class);
        startActivity(intent);
    }

    public void enterAddActivityScreen(View v){
        Intent intent = new Intent(v.getContext(), addingActivity.class);
        startActivity(intent);
    }

    public void enterMySchedulesScreen(View v){
        Intent intent = new Intent(v.getContext(), mySchedules.class);
        startActivity(intent);
    }

    public void enterOptionsScreen() {
        Toast.makeText(this, "Not yet implemented", Toast.LENGTH_SHORT).show();
    }

    public void enterLastScreen() {
        Toast.makeText(this, "Not yet implemented", Toast.LENGTH_SHORT).show();
    }
}
