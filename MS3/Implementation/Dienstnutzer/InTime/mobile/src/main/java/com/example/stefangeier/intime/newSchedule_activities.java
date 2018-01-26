package com.example.stefangeier.intime;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class newSchedule_activities extends AppCompatActivity{

    String scheduleName;
    Location initialLocation;
    Location finalTargetLocation;
    int hourDeadline;
    int minuteDeadline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_schedule_activities);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            scheduleName = extras.getString("scheduleName");
            initialLocation = (Location) extras.get("initialLocation");
            finalTargetLocation = (Location) extras.get("finalTargetLocation");
            hourDeadline = extras.getInt("hourDeadline");
            minuteDeadline = extras.getInt("minuteDeadline");
        }
    }

    public void calculateSchedule(View v){
        VrsXmlParser vrsXmlParser = new VrsXmlParser();
        InputStream isInitialBusstopsResponse;
        InputStream isFinalBusstopsResponse;
        List<VrsXmlParser.Entry> initialBusstopsEntries;
        List<VrsXmlParser.Entry> finalBusstopsEntries;

        HashMap<String, String> httpHeader = new HashMap<>();
        httpHeader.put("Charset", "ISO-8859-15");
        httpHeader.put("Content-Type", "text/xml");
        httpHeader.put("POST", "https://apitest.vrsinfo.de:4443/vrs/cgi/service/ass");
        SendPostTask sendInitialLocationToBusstopTask = new SendPostTask("https://apitest.vrsinfo.de:4443/vrs/cgi/service/ass", "POST", httpHeader);
        SendPostTask sendFinalLocationToBusstopTask = new SendPostTask("https://apitest.vrsinfo.de:4443/vrs/cgi/service/ass", "POST", httpHeader);
        String initialBusstopsRequest = docToString(buildRectangularCoordinateSearchForBusstopsXML(initialLocation));
        String finalBusstopsRequest = docToString(buildRectangularCoordinateSearchForBusstopsXML(finalTargetLocation));

        /*---------------------------- busstops at initial location ------------------------------*/
        try {
            String initialBusstopsResponse = sendInitialLocationToBusstopTask.execute(initialBusstopsRequest).get();
            InputStream is = new ByteArrayInputStream(initialBusstopsResponse.getBytes());
            initialBusstopsEntries = vrsXmlParser.parse(is);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            for (VrsXmlParser.Entry item :
                    initialBusstopsEntries) {
                System.out.println("-------------------- INITIAL BUSSTOP ENTRIES --------------------");
                System.out.println("Stop ID: " + item.stopId);
                System.out.println("VRSNr: " + item.vrsNr);
                System.out.println("GlobalID: " + item.globalId);
            }
        } catch (InterruptedException e) {
            System.out.println("[calculateSchedule]");
            e.printStackTrace();
        } catch (ExecutionException e) {
            System.out.println("[calculateSchedule]");
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*----------------------------- busstops at final location -------------------------------*/
        try {
            String finalBusstopsResponse = sendFinalLocationToBusstopTask.execute(finalBusstopsRequest).get();
            InputStream is = new ByteArrayInputStream(finalBusstopsResponse.getBytes());
            finalBusstopsEntries = vrsXmlParser.parse(is);
            for (VrsXmlParser.Entry item :
                    finalBusstopsEntries) {
                System.out.println("-------------------- FINAL BUSSTOP ENTRIES --------------------");
                System.out.println("Stop ID: " + item.stopId);
                System.out.println("VRSNr: " + item.vrsNr);
                System.out.println("GlobalID: " + item.globalId);
            }
        } catch (InterruptedException e) {
            System.out.println("[calculateSchedule]");
            e.printStackTrace();
        } catch (ExecutionException e) {
            System.out.println("[calculateSchedule]");
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*--------------- looking for a matching route between those two  locations --------------*/
        //TODO looking for a matching route between those two locations

        Toast.makeText(getApplicationContext(), "Schedule successfully created!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), mySchedules.class);
        startActivity(intent);
    }

    public Document buildRectangularCoordinateSearchForBusstopsXML(Location passedLocation){
        int maxDistance = 1000; //in meters

        Deg2UTM utmLocation = new Deg2UTM(passedLocation.getLatitude(), passedLocation.getLongitude());
        String utmRectangle = (utmLocation.Easting-(maxDistance/2)) + "," + (utmLocation.Northing-(maxDistance/2)) + " " +
                (utmLocation.Easting+(maxDistance/2)) + "," + (utmLocation.Northing+(maxDistance/2));

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            /*
                <?xml version="1.0" encoding="ISO-8859-15"?>
                <Request>
                   <ObjectInfo>
                       <ObjectSearch>
                           <CoordinateRectangle srsName="urn:adv:crs:ETRS89_UTM32">355529.2,5644318.9
                               356529.2,5645318.9</CoordinateRectangle>
                               <Classes>
                               <Stop/>
                           </Classes>
                       </ObjectSearch>
                       <Options>
                            <Output>
                                <SRSName>urn:adv:crs:ETRS89_UTM32</SRSName>
                            </Output>
                       </Options>
                    </ObjectInfo>
                </Request
             */

            //root elements
            Document doc = documentBuilder.newDocument();
            Element rootElement = doc.createElement("Request");
            doc.appendChild(rootElement);

            //object info element(s)
            Element objectInfo = doc.createElement("ObjectInfo");
            rootElement.appendChild(objectInfo);

            //object search element(s)
            Element objectSearch = doc.createElement("ObjectSearch");
            objectInfo.appendChild(objectSearch);

            //coordinate Rectangle element(s)
            Element coordinateRectangle = doc.createElement("CoordinateRectangle");
            objectSearch.appendChild(coordinateRectangle);

            //coordinate Rectangle attribute
            Attr coordinateRectangleAttribute = doc.createAttribute("srsName");
            coordinateRectangleAttribute.setValue("urn:adv:crs:ETRS89_UTM32");
            coordinateRectangle.setAttributeNode(coordinateRectangleAttribute);

            //coordinate Rectangle values
            coordinateRectangle.appendChild(doc.createTextNode(utmRectangle));

            //classes element
            Element classes = doc.createElement("Classes");
            objectSearch.appendChild(classes);

            //stop element
            Element stop = doc.createElement("Stop");
            classes.appendChild(stop);

            //options element
            Element options = doc.createElement("Options");
            objectInfo.appendChild(options);

            //output element
            Element output = doc.createElement("Output");
            options.appendChild(output);

            //srsname element
            Element srsName = doc.createElement("SRSName");
            output.appendChild(srsName);
            srsName.appendChild(doc.createTextNode("urn:adv:crs:ETRS89_UTM32"));


            //write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            return doc;

        } catch (ParserConfigurationException e){
            e.printStackTrace();
        } catch (TransformerException e){
            e.printStackTrace();
        }
        return null;
    }

    public Document buildBusTimeTableSearchXml(List<VrsXmlParser> initialBusstopEntries, List<VrsXmlParser> finalBusstopEntries){
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            //root elements
            Document doc = documentBuilder.newDocument();
            Element rootElement = doc.createElement("Request");
            doc.appendChild(rootElement);

            //journey elements
            Element journey = doc.createElement("Journey");
            rootElement.appendChild(journey);

            //'Origin ID' element
            Element originId = doc.createElement("Origin ID");

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String docToString(Document doc) {
        try {
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error converting to String", ex);
        }
    }
    public void enterOverviewScreen(View v){
        Intent intent = new Intent(v.getContext(), Overview.class);
        startActivity(intent);
    }
    public void enterAddActivtyScreen(View v){
        Intent intent = new Intent(v.getContext(), addingActivity.class);
        startActivity(intent);
    }
    public void enterScheduleScreen(View v){
        Intent intent = new Intent(v.getContext(), scheduleScreen.class);
        startActivity(intent);
    }
    public void enterOptionsScreen() {
        Toast.makeText(this, "Not yet implemented", Toast.LENGTH_SHORT).show();
    }
    public void enterLastScreen() {
        Toast.makeText(this, "Not yet implemented", Toast.LENGTH_SHORT).show();
    }
}
