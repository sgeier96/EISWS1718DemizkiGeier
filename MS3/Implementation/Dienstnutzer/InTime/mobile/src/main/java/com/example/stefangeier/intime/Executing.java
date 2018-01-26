package com.example.stefangeier.intime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Executing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_executing);
    }
    public void enterOverviewScreen(View v){
        Intent intent = new Intent(v.getContext(), Overview.class);
        startActivity(intent);
    }
}
