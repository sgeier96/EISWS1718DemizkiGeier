package com.example.stefangeier.intime;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
        List<Entry> entries = new ArrayList<Entry>();

        parser.require(XmlPullParser.START_TAG, namespace, "Response");
        while(parser.next() != XmlPullParser.END_TAG){
            if(parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String name = parser.getName();
            //Starts by looking for the Object tag
             if(name.equals("StopTimetable")){
                System.out.println("StopTimetable found");
            } else if(name.equals("StopEvents")){
                System.out.println("StopEvents found");
            } else if(name.equals("StopEvent")){
                System.out.println("StopEvent found");
                entries.add(readEntry(parser));
            } else {
                 System.out.println("skipped");
                 skip(parser);
            }
        }
        return entries;
    }

    public static class Entry {
        public final int id;
        public final String line;
        public final String direction;
        public final String departureTime;

        private Entry(int id, String line, String direction, String departureTime){
            this.id = id;
            this.line = line;
            this.direction = direction;
            this.departureTime = departureTime;
        }
    }

    //Parses the contents of an entry. If it encounters an id, line, direction or departureTime tag,
    //hands them of to their respective "read" methods for processing. Otherwise, skip the tag

    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, namespace, "StopEvent");
        int id = -1;
        String line = null;
        String direction = null;
        String departureTime = null;

        while (parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String name = parser.getName();
            if (name.equals("ID")){
                id = readId(parser);
            } else if (name.equals("Line")){
                line = readLine(parser);
            } else if (name.equals("Direction")){
                direction = readDirection(parser);
            } else if (name.equals("DepartureTime")){
                departureTime = readDepartureTime(parser);
            } else {
                skip(parser);
            }
        }
        return new Entry(id, line, direction, departureTime);
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
