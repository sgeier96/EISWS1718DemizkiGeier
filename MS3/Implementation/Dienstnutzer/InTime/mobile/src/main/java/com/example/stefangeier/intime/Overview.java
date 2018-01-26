package com.example.stefangeier.intime;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class Overview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
    }

    public void enterExecutingScreen(View v){
        Intent intent = new Intent(v.getContext(), Executing.class);
        startActivity(intent);
    }

    public void enterNewScheduleScreen(View v) {
        Intent intent = new Intent(v.getContext(), newSchedule.class);
        startActivity(intent);
    }

    public void enterMySchedulesScreen(View v){
        Intent intent = new Intent(v.getContext(), mySchedules.class);
        startActivity(intent);
    }
}
