package com.example.stefangeier.intime;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private VrsXmlParser parser = new VrsXmlParser();

    int toastDuration = Toast.LENGTH_SHORT;
    private NumberPicker durationPicker;
    private NumberPicker priorityPicker;
    String result;
    TextView tv1;

    Gson gson = new Gson();

    ArrayList<Activity> activityArrayList = new ArrayList<>();
    ArrayList<Activity> schedule = new ArrayList<>();

    Button button_tpd;
    static final int DIALOG_ID = 0;
    int hourDeadline;
    int minuteDeadline;
    String departureTime = null;
    int departureInMinutes;
    String output = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        durationPicker = (NumberPicker)findViewById(R.id.activityDurationNumberPicker);
        durationPicker.setMinValue(0);
        durationPicker.setMaxValue(23);
        durationPicker.setWrapSelectorWheel(true);

        priorityPicker = (NumberPicker)findViewById(R.id.activityPriorityNumberPicker);
        priorityPicker.setMinValue(0);
        priorityPicker.setMaxValue(5);
        priorityPicker.setWrapSelectorWheel(true);

        showTimePickerDialog();



        tv1=(TextView)findViewById(R.id.textView);
        try {
            InputStream is = getAssets().open("Haltestellenabfahrtsplan.xml");
            List<VrsXmlParser.Entry> list = parser.parse(is);
            departureTime = String.valueOf(list.get(0).departureTime);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        String s = departureTime.substring(departureTime.indexOf("T") + 1, departureTime.indexOf("+"));
        departureInMinutes = convertToMinutes(convertToTime(s.substring(0,5)));
        //tv1.setText(String.valueOf(departureHHMM.getHours()) + ":" + String.valueOf(departureHHMM.getMinutes()));




    }

    public void sendPostRequest(View v){
        SendPostTask sendPostTask = new SendPostTask();
        sendPostTask.execute(writeJSON(v));
    }

    public void dialog(View v){
        new AlertDialog.Builder(this)
                .setTitle("Info")
                .setMessage(result)
                .setNegativeButton("Abbrechen", null)
                .setPositiveButton("Yahs", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendRequest();
                    }
                }).create().show();
    }

    public void sayText(String saidText){
        Toast toast = Toast.makeText(this, saidText, toastDuration);
        toast.show();
    }

    public void sendRequest(){
      RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://www.google.com";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        result = response.substring(0, 500);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                result = "That didn't work!";
            }
        });
        queue.add(stringRequest);
    }



    public void showTimePickerDialog(){
        button_tpd = (Button)findViewById(R.id.setDeadlineButton);
        button_tpd.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                     showDialog(DIALOG_ID);

                    }
                }
        );
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if(id == DIALOG_ID){
            return new TimePickerDialog(MainActivity.this, kTimePickerListener, hourDeadline, minuteDeadline, true);
        }
        return null;
    }

    protected TimePickerDialog.OnTimeSetListener kTimePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                    hourDeadline = i;
                    minuteDeadline = i1;
                }
            };

    public void addActivityToSchedule(View v){
        EditText field1 = findViewById(R.id.activityNameEditText);
        activityArrayList.add(new Activity(String.valueOf(field1.getText()), durationPicker.getValue(), priorityPicker.getValue()));
        sayText(String.valueOf(activityArrayList.get(activityArrayList.size()-1).activityName + " | " + activityArrayList.get(activityArrayList.size()-1).activityDuration));
    }

    public void manageSchedule(View v){
        int minutesNeeded = 0;

        System.out.println(getCurrentTime());
        //sort activities by priority
        Collections.sort(activityArrayList, new Comparator<Activity>() {
            @Override
            public int compare(Activity o1, Activity o2) {
                return o1.priority < o2.priority ? 1
                        : o1.priority > o2.priority ? -1
                        : 0;
            }
        });
        for (int i = 0; i < activityArrayList.size(); i++){
            minutesNeeded += activityArrayList.get(i).activityDuration;
        }
        System.out.println("Minutes needed: " + minutesNeeded);
        //System.out.println("Done at: " + Math.addExact()); //TODO Show The Time At Which Point The User Would Be Done

        //Check whether we are below the deadline or not
        if(convertToMinutes(getCurrentTime()) + minutesNeeded < departureInMinutes){
            System.out.println("schedule currently doable");

            for (int i = 0; i < activityArrayList.size(); i++){
                schedule.add(activityArrayList.get(i));
            }
        } else {
            System.out.println("schedule currently not doable");
            rearrangeActivityDurations(minutesNeeded);

            for (int i = 0; i < activityArrayList.size(); i++){
                schedule.add(activityArrayList.get(i));
                //TODO print the activities as a schedule in a JSON
            }
        }

        for(int i = 0; i < schedule.size(); i++){
            output = output.concat(String.valueOf(schedule.get(i).activityName + " | " + schedule.get(i).activityDuration) + "\n");
            tv1.setText(output);

        }

    }

    //TODO Implement the possibility to delete an activity with a priority of 1
    private void rearrangeActivityDurations(int minutesNeeded){
        int minutesToWin = (convertToMinutes(getCurrentTime()) + minutesNeeded) - departureInMinutes;
        int minutesToSubtract = 0;
        
        while (minutesToWin > 0) {
            for (int i = activityArrayList.size() - 1; i >= 0; i--) {
                    switch(activityArrayList.get(i).priority){
                        case 1:
                            for(int j = 0; j < 10; j++){
                                minutesToSubtract += 1;
                                minutesToWin -= 1;
                                if(minutesToWin == 0){
                                    break;
                                }
                            }
                        case 2:
                            for(int j = 0; j < 7; j++){
                                minutesToSubtract += 1;
                                minutesToWin -= 1;
                                if(minutesToWin == 0){
                                    break;
                                }
                            }
                        case 3:
                            for(int j = 0; j < 5; j++){
                                minutesToSubtract += 1;
                                minutesToWin -= 1;
                                if(minutesToWin == 0){
                                    break;
                                }
                            }
                        case 4:
                            for(int j = 0; j < 3; j++){
                                minutesToSubtract += 1;
                                minutesToWin -= 1;
                                if(minutesToWin == 0){
                                    break;
                                }
                            }
                        case 5:
                            continue;
                    }
                    activityArrayList.set(i, new Activity(activityArrayList.get(i).activityName, activityArrayList.get(i).activityDuration - minutesToSubtract, activityArrayList.get(i).priority));
            }
            }
        System.out.println("While left.");
    }



    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

    public Date convertToTime(String timeString){
        Date result = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
        try {
            result =  dateFormat.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    private int convertToMinutes(Date time){

        return (time.getHours()*60)+time.getMinutes();
    }

    private Date getCurrentTime(){
        Date currentTime = new Date();
        //currentTime.setHours((currentTime.getHours()+6)%24);
        //currentTime.setMinutes((currentTime.getMinutes())%60);

        currentTime.setHours(17);
        currentTime.setMinutes(30);
        return currentTime;
    }

    public String writeJSON(View v){

        Resource resource1 = new Resource("bathroom", 1);
        Resource resource2 = new Resource("second bathroom", 1);
        Resource resource3 = new Resource("kitchen table", 3);
        Household household = new Household("22nd");
        Household household2 = new Household("oofhouse");
        household.addResource(resource1);
        household.addResource(resource2);
        household.addResource(resource3);
        User user1 = new User("stefan");
        User user2 = new User("vadim");
        User user3 = new User("fooman");
        household.addResident(user1);
        household.addResident(user2);
        household2.addResident(user3);
        household2.addResource(resource1);


        writeToFile(gson.toJson(household), getApplicationContext());
        String jsonString = gson.toJson(household).toString();
        System.out.println("ReadFromFile: " + readFromFile(getApplicationContext()));
        System.out.println("jsonString: " + jsonString);

        return jsonString;
    }

    public void writeToFile(String data, Context context){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("households.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch(IOException e){
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("households.json");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("households reading", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("households reading", "Can not read file: " + e.toString());
        }

        return ret;
    }
    
/*  --------------JUST SOME OLD CODE... to be determined whether deletable or not------------------- 
    private Date addTime(Date before, Date summand){
        Date result = new Date();

        result.setHours(before.getHours() + summand.getHours()%24);
        result.setMinutes(before.getMinutes() + summand.getMinutes()%60);
        //Maybe for hours too...(x+y>24, one day later)
        /*if (before.getMinutes() + summand.getMinutes()>60) {
            result.setHours(result.getHours() + 1);
        }/

        return result;
    }

    private Date substractTime(Date timeBeingSubstracted, Date timeToSubstract){
        Date result = new Date();

        /*if (timeBeingSubstracted.getMinutes() - timeToSubstract.getMinutes() < 0){
            result.setHours(result.getHours()-1);
            result.setMinutes(60 - (timeBeingSubstracted.getMinutes() - timeToSubstract.getMinutes()));
        } else if(timeBeingSubstracted.getHours() - timeToSubstract.getHours() < 0){
            result.setHours(24 - (timeBeingSubstracted.getHours() - timeToSubstract.getHours()));
            //one day earlier to be implemented
        } else if (timeBeingSubstracted.getMinutes() - timeToSubstract.getMinutes() > 0){
            result.setHours(timeBeingSubstracted.getHours() - timeToSubstract.getHours());
            result.setMinutes(timeBeingSubstracted.getMinutes() - timeToSubstract.getMinutes());
        } else {
            result.setHours(0);
            result.setMinutes(0);
        }/
        result.setHours(timeBeingSubstracted.getHours() - timeToSubstract.getHours());
        result.setMinutes(timeBeingSubstracted.getMinutes() - timeToSubstract.getMinutes());

        return result;
    } */

   
}