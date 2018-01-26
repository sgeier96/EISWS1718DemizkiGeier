package com.example.stefangeier.intime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class addingActivity extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_activity);
    }


    public void enterOverviewScreen(View v){
        Intent intent = new Intent(v.getContext(), Overview.class);
        startActivity(intent);
    }

    public void enterScheduleScreen(View v){
        Intent intent = new Intent(v.getContext(), scheduleScreen.class);
        startActivity(intent);
    }

    public void enterOptionsScreen() {
        Toast.makeText(getApplicationContext(), "Not yet implemented", Toast.LENGTH_SHORT).show();
    }

    public void enterLastScreen() {
        Toast.makeText(getApplicationContext(), "Not yet implemented", Toast.LENGTH_SHORT).show();
    }
}
