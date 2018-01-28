package com.example.stefangeier.intime;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings.Secure;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity{

    private VrsXmlParser parser = new VrsXmlParser();
    private boolean loginSuccessfull = false;

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
        String android_id= Secure.getString(this.findViewById(android.R.id.content).getContext().getContentResolver(),
                Secure.ANDROID_ID);

        if(attemptLogin(android_id)){
            sayText("Login successfull!");
             /* Enter Overview-Screen */
            Intent intent = new Intent(getApplicationContext(), Overview.class);
            startActivity(intent);
        } else {
            sayText("Registration required!");
            Intent intent = new Intent(getApplicationContext(), Registration.class);
            startActivity(intent);
        }




        durationPicker = (NumberPicker)findViewById(R.id.activityDurationNumberPicker);
        durationPicker.setMinValue(0);
        durationPicker.setMaxValue(23);
        durationPicker.setWrapSelectorWheel(true);

        priorityPicker = (NumberPicker)findViewById(R.id.activityPriorityNumberPicker);
        priorityPicker.setMinValue(0);
        priorityPicker.setMaxValue(5);
        priorityPicker.setWrapSelectorWheel(true);

        showTimePickerDialog();

       /* Button btnRegister = (Button) findViewById(R.id.register);
        btnRegister.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(v.getContext(), Registration.class);
                startActivityForResult(intent, 0);
            }
        });
        */

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


        //String s = departureTime.substring(departureTime.indexOf("T") + 1, departureTime.indexOf("+"));
        //departureInMinutes = convertToMinutes(convertToTime(s.substring(0,5)));
        //tv1.setText(String.valueOf(departureHHMM.getHours()) + ":" + String.valueOf(departureHHMM.getMinutes()));

    }

    public boolean attemptLogin(String android_id){

        final String localhost = "http://10.0.2.2:6458/";
        final String herokuUrl = "https://eisws1718demizkigeier.herokuapp.com";

        HashMap<String, String> httpHeader = new HashMap<>();
        httpHeader.put("User-Agent", "Mozilla/5.0");
        httpHeader.put("Accept-Language", "en-US, en;q=0.5");
        httpHeader.put("Content-Type", "application/json");

        SendPostTask sendLoginTask = new SendPostTask(localhost, "POST", httpHeader);
        GsonWrapper gsonWrapper = new GsonWrapper(android_id, "login");
        String statusCodeOfLogin;
        try {
            System.out.println("##############################");
            statusCodeOfLogin = sendLoginTask.execute(gson.toJson(gsonWrapper)).get();
            if (statusCodeOfLogin.equals("true")){
                return true;
            } else {
                return false;
            }
        } catch (InterruptedException e){
            e.printStackTrace();
            return false;
        } catch (ExecutionException e){
            e.printStackTrace();
            return false;
        }
    }

    public void sendPostRequest(View v){
        HashMap<String, String> httpHeader = new HashMap<>();
        httpHeader.put("User-Agent", "Mozilla/5.0");
        httpHeader.put("Accept-Language", "en-US, en;q=0.5");
        httpHeader.put("Content-Type", "application/json");
        SendPostTask sendPostTask = new SendPostTask("http://10.0.2.2:6458/", "POST", httpHeader);
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
        User user1 = new User("stefan", "dummyAndroid_ID1");
        User user2 = new User("vadim", "dummyAndroid_ID2");
        User user3 = new User("fooman", "dummyAndroid_ID3");
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

        String returnValue = "";

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
                returnValue = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("households reading", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("households reading", "Can not read file: " + e.toString());
        }

        return returnValue;
    }

    public boolean getLoginSuccessfull(){
        return loginSuccessfull;
    }
    public void setLoginSuccessfull(boolean newLoginState){
        loginSuccessfull = newLoginState;
    }
}