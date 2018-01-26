package com.example.stefangeier.intime;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Stefan Geier on 13.12.2017.
 *
 * Aufgrund der exception "android.os.NetworkOnMainThreadException" ist die
 * Verlagerung und Vererbung von AsyncTask notwendig.
 * (Laut eines Foreneintrages auf stackoverflow.com)
 *
 * AsyncTask < Params, Progress, Result >
 *     Params: the type of doInBackground()'s and execute()'s var-args parameters.
 *     Progress: the type of publishProgress()'s and pnProgressUpdate()'s var-args parameters.
 *     Result: the type of doInBackground()'s return value, onPostExecute()'s parameter,
 *             onCancelled()'s parameter and get()'s return value.
 */

class SendPostTask extends AsyncTask<String, Integer, String>{
    MainActivity mainActivity = new MainActivity();
    String urlString;
    HashMap<String, String> requestProperties = new HashMap<>();
    String requestMethod;
    String result;

    public SendPostTask(String targetUrl, String requestMethod, HashMap<String, String> requestProperties){
        this.urlString = targetUrl;
        this.requestMethod = requestMethod;
        this.requestProperties = requestProperties;
    }
        protected String doInBackground(String... strings) {
            final String USER_AGENT = "Mozilla/5.0";
            //String urlString = "http://httpbin.org/post";
            int responseCode = 500;
            try {
                //String urlString = "https://eisws1718demizkigeier.herokuapp.com";
                /*
                If you are referring your localhost on your system from the Android emulator then
                you have to use http://10.0.2.2:8080/ Because Android emulator runs in a Virtual
                Machine therefore here 127.0.0.1 or localhost will be emulator's own loopback
                address.
                (https://stackoverflow.com/questions/5495534/java-net-connectexception-localhost-127-0-0-18080-connection-refused#5495789)
                 */
                //String urlString = "http://10.0.2.2:6458/";
                URL url = new URL(urlString);
                try {
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //add request header
                    connection.setRequestMethod(requestMethod);
                    for (HashMap.Entry<String, String> entry : requestProperties.entrySet()){
                        String key = entry.getKey();
                        String value = entry.getValue();
                        connection.setRequestProperty(key, value);
                    }

                    String jsonString = strings[0];

                    //Send post request
                    connection.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    wr.writeBytes(jsonString);
                    wr.flush();
                    wr.close();

                    responseCode = connection.getResponseCode();
                    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
                    System.out.println("\nSending 'POST' request to URL: " + url.toString());
                    System.out.println("POSTing json: " + jsonString);
                    System.out.println("Response Code:" + responseCode);

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    //print result
                    result = response.toString();
                    System.out.println(" RESPONSE: " + response.toString());
                    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }catch(MalformedURLException e){
                e.printStackTrace();
            }
            System.out.println("SENDPOSTTASK DOINBACKGROUND: " + responseCode);
            return result;
        }
}

