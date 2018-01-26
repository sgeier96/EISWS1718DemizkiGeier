package com.example.stefangeier.intime;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Stefan Geier on 10.11.2017 with the help and guidance of
 * https://developer.android.com/training/basics/network-ops/xml.html
 * Code has been adjusted to fit into this project. The algorithm and idea behind it was
 * taken from the link mentioned above.
 */

public class VrsXmlParser {

    private static final String namespace = null;

    public List<Entry> parse(InputStream inputStream)throws XmlPullParserException, IOException{
        try{
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            inputStream.close();
        }
    }

    private List<Entry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Entry> entries = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, namespace, "Response");
        while(parser.next() != XmlPullParser.END_DOCUMENT){
            if(parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String name = parser.getName();
            //Starts by looking for the Stop tag
             if(name.equals("Stop")){
                System.out.println("Stop found");
                entries.add(readEntry(parser));
            } else {
                 System.out.println(name + " skipped");
                 //skip(parser);
                 //parser.next();
             }
        }
        return entries;
    }

    public static class Entry {
        public int id = -1;
        public String line = null;
        public String direction = null;
        public String departureTime = null;
        public int stopId = -1;
        public int vrsNr = -1;
        public String globalId = null;

        private Entry(int id, String line, String direction, String departureTime){
            this.id = id;
            this.line = line;
            this.direction = direction;
            this.departureTime = departureTime;
        }

        //Entry for rectangular coordinate busstop requests
        private Entry(int stopId, int vrsNr, String globalId){
            this.stopId = stopId;
            this.vrsNr = vrsNr;
            this.globalId = globalId;
        }
    }

    //Parses the contents of an entry. If it encounters an id, line, direction or departureTime tag,
    //hands them of to their respective "read" methods for processing. Otherwise, skip the tag

    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, namespace, "Stop");
        int id = -1;
        String line = null;
        String direction = null;
        String departureTime = null;
        int stopId = -1;
        int vrsNr = -1;
        String globalId = null;
        String name = parser.getName();

        if (name.equals("Stop")){
            System.out.println("[READ ENTRY] About to process 'Stop'");
            stopId = readStopId(parser);
            System.out.println(stopId);
        }

        while (parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            name = parser.getName();
            if (name.equals("NumberID")) {
                System.out.println("[READ ENTRY] NumberID found!");

                HashMap<String, String> resultHashMap = readNumberId(parser);
                if (resultHashMap.containsKey("vrsNr")) {
                    vrsNr = Integer.parseInt(resultHashMap.get("vrsNr"));
                    parser.nextTag();
                }
                resultHashMap = readNumberId(parser);
                if (resultHashMap.containsKey("globalId")) {
                    globalId = resultHashMap.get("globalId");
                }
            }else {
                skip(parser);
            }
        }
        //return new Entry(id, line, direction, departureTime);
        return new Entry(stopId, vrsNr, globalId);
    }

    //Processes 'Stop ID' tags in the response
    private int readStopId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, namespace, "Stop");
        System.out.println("in readStopId");
        String sStopId = parser.getAttributeValue(null, "ID");
        System.out.println("STOP ID=\"" + sStopId + "\"");
        return Integer.parseInt(sStopId);
    }

    //Processes 'NumberID' tags in the response
    private HashMap<String, String> readNumberId(XmlPullParser parser) throws IOException, XmlPullParserException{
        HashMap<String, String> resultHashMap = new HashMap<>();
        parser.require(XmlPullParser.START_TAG, namespace, "NumberID");
        String tag = parser.getName();
        String numberRangeValue = parser.getAttributeValue(null, "NumberRange");

       if(tag.equals("NumberID")){
           if(numberRangeValue.equals("VRSNr")){
               resultHashMap.put("vrsNr", readText(parser));
           }
           if(numberRangeValue.equals("GlobalID")){
               resultHashMap.put("globalId", readText(parser));
           }
       }

        parser.require(XmlPullParser.END_TAG, namespace, "NumberID");
        return resultHashMap;
    }

    //Processes id tags in the response
    private int readId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, namespace, "ID");
        System.out.println("in readId");
        String id = readText(parser);
        parser.require(XmlPullParser.END_TAG, namespace, "ID");
        return Integer.parseInt(id);
    }

    // Processes line tags in the feed.
    private String readLine(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, namespace, "Line");
        System.out.println("in readLine");
        String line = readText(parser);
        parser.require(XmlPullParser.END_TAG, namespace, "Line");
        return line;
    }

    // Processes direction tags in the feed.
    private String readDirection(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, namespace, "Direction");
        System.out.println("in readDirection");
        String direction = readText(parser);
        System.out.println("DIRECTION: " + direction);
        parser.require(XmlPullParser.END_TAG, namespace, "Direction");
        return direction;
    }

    // Processes departureTime tags in the feed.
    private String readDepartureTime(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, namespace, "DepartureTime");
        System.out.println("in readDepartureTime");
        String departureTime = readText(parser);
        System.out.println("DEPARTURETIME: " + departureTime);
        parser.require(XmlPullParser.END_TAG, namespace, "DepartureTime");
        return departureTime;
    }


    //For the tags id, line, direction, departureTime, extracts their text directions.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT){
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    //Skips not interesting tags
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if(parser.getEventType() != XmlPullParser.START_TAG){
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0){
            switch (parser.next()){
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
