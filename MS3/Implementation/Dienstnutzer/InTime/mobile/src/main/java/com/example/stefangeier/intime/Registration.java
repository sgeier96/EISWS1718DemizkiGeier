package com.example.stefangeier.intime;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.HashMap;


public class Registration extends AppCompatActivity {
    String localhost = "http://10.0.2.2:6458/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

       final Gson gson = new Gson();

        Button btnConfirm = (Button) findViewById(R.id.registerDone);
        btnConfirm.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(v.getContext(), Overview.class);

                String android_id = Settings.Secure.getString(v.getContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                EditText editTextUsername = findViewById(R.id.editUsername);
                String username = String.valueOf(editTextUsername.getText());
                User user = new User(username, android_id);

                GsonWrapper gsonWrapper = new GsonWrapper(user, "registration");
                HashMap<String, String> httpHeader = new HashMap<>();
                httpHeader.put("User-Agent", "Mozilla/5.0");
                httpHeader.put("Accept-Language", "en-US, en;q=0.5");
                httpHeader.put("Content-Type", "application/json");
                SendPostTask sendPostTask = new SendPostTask(localhost, "POST", httpHeader);
                sendPostTask.execute(gson.toJson(gsonWrapper));

                startActivity(intent);
                finish();
            }
        });
    }
}
