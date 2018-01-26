package com.example.stefangeier.intime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Stefan Geier on 23.01.2018.
 */

public class InTimeActivity extends AppCompatActivity{

    public void enterOverviewScreen(View v){
        Intent intent = new Intent(v.getContext(), Overview.class);
        startActivity(intent);
    }

    public void enterOptionsScreen(){

    }

    public void enterLastScreen(){

    }
}
