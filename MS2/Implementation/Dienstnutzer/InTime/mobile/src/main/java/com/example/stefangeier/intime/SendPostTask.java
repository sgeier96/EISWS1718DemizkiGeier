package com.example.stefangeier.intime;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Stefan Geier on 13.12.2017.
 *
 * Aufgrund der exception "android.os.NetworkOnMainThreadException" ist die
 * Verlagerung und Vererbung von AsyncTask notwendig.
 * (Laut eines Foreneintrages auf stackoverflow.com)
 */

class SendPostTask extends AsyncTask<String, Void, Integer>{

        protected Integer doInBackground(String... strings) {
            final String USER_AGENT = "Mozilla/5.0";
            //String urlString = "http://httpbin.org/post";

            try {
                String urlString = "https://eisws1718demizkigeier.herokuapp.com";
                URL url = new URL(urlString);
                try {
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //add request header
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("User-Agent", USER_AGENT);
                    connection.setRequestProperty("Accept-Language", "en-US, en;q=0.5");
                    connection.setRequestProperty("Content-Type", "application/json");
                    String jsonString = strings[0];

                    //Send post request
                    connection.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    wr.writeBytes(jsonString);
                    wr.flush();
                    wr.close();

                    int responseCode = connection.getResponseCode();
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

                    System.out.println(response.toString());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }catch(MalformedURLException e){
                e.printStackTrace();
            }

            return 1;
        }

}

