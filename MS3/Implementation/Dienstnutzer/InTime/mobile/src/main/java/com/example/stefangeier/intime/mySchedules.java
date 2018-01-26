package com.example.stefangeier.intime;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class mySchedules extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_schedules);

    }
    public void enterOverviewScreen(View v){
        Intent intent = new Intent(v.getContext(), Overview.class);
        startActivity(intent);
    }

    public void enterNewScheduleScreen(View v){
        Intent intent = new Intent(v.getContext(), newSchedule.class);
        startActivity(intent);
    }

    public void enterOptionsScreen() {
        Toast.makeText(this, "Not yet implemented", Toast.LENGTH_SHORT).show();
    }

    public void enterLastScreen() {
        Toast.makeText(this, "Not yet implemented", Toast.LENGTH_SHORT).show();
    }
}
