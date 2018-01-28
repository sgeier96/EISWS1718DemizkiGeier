var fs = require('fs');                   //I/O-Funktionen
var express = require("express");         //app.get, etc.
var request = require("request");         //request(url, function(err, res, body))), etc.
var bodyParser = require("body-parser");  //parser for i.e. JSON
const writeJsonFile = require('write-json-file'); //Stringify and write JSON to a file atomically
const loadJsonFile = require('load-json-file'); //Read and parse a JSON file. Same author as ⬆

var XMLHttpRequest = require("xmlhttprequest").XMLHttpRequest;                  //For sync/async Requests
var xmllint = require('xmllint')                                                //validator function
var inspect = require('util').inspect;
var xhr = new XMLHttpRequest();                                                 //creates an XMLHttpRequest object
var convert = require('xml-js');                                                //https://www.npmjs.com/package/xml-js

var app = express();
var households = [];
var userdatabasePath = 'userdatabase.json';
var result;

//POST-Zeug von https://codeforgeek.com/2014/09/handle-get-post-request-express-4
//Here we are configuring express to use body-parser as middle-ware.
app.use(bodyParser.urlencoded({extended: false}));
app.use(bodyParser.json());
//----------------------------------------------------------------------------//

//Konfigurationsvariablen
const settings = {
  port: process.env.PORT || 6458
  //HEROKUAPP https://eisws1718demizkigeier.herokuapp.com
  //etc.
}

app.get('/*', function(request, response){
  response.send("There you have your GET-Answer");
});

app.post('/', function(request, response){
  console.log("[POST-Acceptor] Received data: " + JSON.stringify(request.body));
  var data = request.body;
  processRequestParameter(data);
  //vrsCommunication();

  response.send(result);
});

//--------------------------Search object key or values-----------------------//
//http://techslides.com/how-to-parse-and-search-json-in-javascript
//return an array of objects according to key, value, or key and value matching
function getObjects(obj, key, val) {
    var objects = [];
    for (var i in obj) {
        if (!obj.hasOwnProperty(i)) continue;
        if (typeof obj[i] == 'object') {
            objects = objects.concat(getObjects(obj[i], key, val));
        } else
        //if key matches and value matches or if key matches and value is not passed (eliminating the case where key matches but passed value does not)
        if (i == key && val == '' && key == "house_id"){
          var houseID = obj[i];
          return houseID;
          break;
        }
        if (i == key && val == '' && key == "deadline"){
          var deadlines = obj[i];
          return deadlines;
          break;
        }
        if (i == key && val == '' && key == "activities"){
          var activities = obj[i];
          return activities;
          break;
        }
        if (i == key && val == '' && key == "activityDuration"){
          var wholeDuration = obj[i];
          return wholeDuration;
          break;
        }
        if (i == key && obj[i] == val || i == key && val == '') { //
            objects.push(obj);
        } else if (obj[i] == val && key == ''){
            //only add if the object is not already in the array
            if (objects.lastIndexOf(obj) == -1){
                objects.push(obj);
            }
        }
    }
    return objects;
}//end of getObjects()

//---------------------------Abfahrt und Ankunft Anfrage----------------------//
function journey(passedData){
  //var rJson = fs.readFileSync('./clientJson.json'); //dummy Json(Information from client)
  var rJson = JSON.stringify(passedData);
  try {
    var oJson = JSON.parse(rJson);
    var arrivalTime = oJson.Ankunftszeit.toString();
    var departureTime = oJson.Abfahrtszeit;
    var placeofArrival = oJson.Ankunftsort;
    var pointofDeparture = oJson.Abfahrtsort;
    }
  catch (e) {
    console.log("json parsing failed");
  }
    var xmlPOST =
    '<?xml version="1.0" encoding="ISO-8859-15"?>'                                        +
    ' <Request>'                                                                          +
    '   <Journey>'                                                                        +
    '     <Origin ID="'+ pointofDeparture +'" Type="Stop"/>'                              +
    '     <Destination ID="'+ placeofArrival +'" Type="Stop"/>'                           +
    '     <SearchTime SearchDirection="Departure">'+ arrivalTime +'</SearchTime>'         +
    '     <Mode>'                                                                         +
    '       <PublicTransport>'                                                            +
    '         <SearchInterval>'                                                           +
    '           <NumOfRoutes>5</NumOfRoutes>'                                             +
    '         </SearchInterval>'                                                          +
    '         <Product>LongDistanceTrains</Product>'                                      +
    '         <Product>RegionalTrains</Product>'                                          +
    '         <Product>SuburbanTrains</Product>'                                          +
    '         <Product>Underground</Product>'                                             +
    '         <Product>Bus</Product>'                                                     +
    '         <Product>CommunityBus</Product>'                                            +
    '         <Product>OnDemandServices</Product>'                                        +
    '         <Product>LightRail</Product>'                                               +
    '         <SupplementalPayment>false</SupplementalPayment>'                           +
    '         <DisabledAccessRequired/>'                                                  +
    '         <RadiusExtensionOrigin>450</RadiusExtensionOrigin>'                         +
    '         <RadiusExtensionDestination>450</RadiusExtensionDestination>'               +
    '        </PublicTransport>'                                                          +
    '      </Mode>'                                                                       +
    '      <Options>'                                                                     +
    '         <Output>'                                                                   +
    '           <SRSName>urn:adv:crs:ETRS89_UTM32</SRSName>'                              +
    '           <NoShape>false</NoShape>'                                                 +
    '         </Output>'                                                                  +
    '      </Options>'                                                                    +
    '   </Journey>'                                                                       +
    '</Request>'
    //validate(xmlPOST);

    vrsCommunication(xmlPOST, arrivalTime);
}//end of journey

//------------------------------TEST API Anbindung----------------------------//
function vrsCommunication(xmlPOST, arrivalTime){

  xhr.onreadystatechange = function() {
      if (xhr.readyState === 4) {
        try {
          var cJson = convert.xml2json(xhr.responseText, {compact: true, spaces: 4});
          var parsedJson = JSON.parse(cJson);

          console.log(require('util').inspect(parsedJson, {colors: true, depth: null }));

          console.log("---------------------arrivalTime---------------------");
          console.log(arrivalTime);
          var searchObj = getObjects(parsedJson,"_text", arrivalTime);              //search speciefied time in response
          if (searchObj.length != 0){                                            //test of different time
            console.log("No changes in departure time");
           }
           else {
             console.log("Attention! New time to go");
           }
         //console.log(inspect(getObjects(tJson,"_text",'2018-01-29T15:21:00+01:00'), { colors: true, depth: Infinity }));
        } catch (e) {
           console.log("failed parse xml response");
        }
        //var strResponse = xhr.responseText;
        //validate(strResponse);
      }
      else {
        console.log("failed request");
      }
  }
  xhr.open("POST","https://apitest.vrsinfo.de:4443/vrs/cgi/service/ass",false);        //Specifies the type(method,url,async) of request
  xhr.responseType = 'document';
  xhr.setRequestHeader("POST", "https://apitest.vrsinfo.de:4443/vrs/cgi/service/ass"); //Set Request Header
  xhr.setRequestHeader("Content-Type", "text/xml");
  xhr.setRequestHeader("Charset", "ISO-8859-15");
  xhr.send(xmlPOST);        //Sends a request string to the server
}//end of vrsCommunication

function  validate (xml){
  var schema = fs.readFileSync('./ASS2Schnittstelle_3.0.4.xsd').toString();
   try {
     xmllint.validateXML({ xml: xml, schema: schema});
     console.log("validate success");
   }
   catch(error) {
     console.log("validate failed");
   }
}
//-----------------------------------------------------------------------------//
function processRequestParameter(data){
  switch(data.parameter){
    case 'registration':
      processRegistration(data.user);
      break;
    case 'login':
      processLogin(data);
      break;
    case 'userSchedule':
      startHouseholdManager(data.user);
      break;  //...
    case 'userTravelInfo':
      journey(data.o);
      break;
    default:
      console.log("unknown parameter");
  }
}//end of validate()

function processRegistration(userData){
  console.log("[processRegistration] Whole passedJsonAsObject: " + JSON.stringify(userData));
  console.log("[processRegistration] android_id:" + JSON.stringify(userData.android_id));
  console.log("[processRegistration] username: " + JSON.stringify(userData.username));

  var unknownUser = true;
  var loadedJson = '';

  console.log("[processingRegistration] Attempting to load userdatabase...");
  var loadedData = fs.readFileSync(userdatabasePath);
  try {
    var loadedJson = JSON.parse(loadedData);
    console.log("[processingRegistration] Userdatabase loaded");
  } catch (e) {
    var loadedJson = [];
    console.log("[processingRegistration] Userdatabase loaded");
  }
  var loadedJsonLength = Object.keys(loadedJson).length;
  for (var i = 0; i < loadedJsonLength; i++) {
    if (loadedJson[i].android_id == userData.android_id) {
      console.log("[processingRegistration] Already registered User! Aborting...");
      unknownUser = false;
      continue;
    }
  }
  if (unknownUser) {
    console.log("[processingRegistration] Android_id not found, thus new User! Registrating...");
    loadedJson.push(userData);
    console.log("[processingRegistration] User added to database");
    fs.writeFileSync(userdatabasePath, JSON.stringify(loadedJson));
    console.log("[processingRegistration] Userdatabase saved. Registration complete.");
  }
}//end of processRegistration function

function processLogin(userData){

  var knownUser = false;
  var loadedJson = '';

  console.log("[processingRegistration] Attempting to load userdatabase...");
  var loadedData = fs.readFileSync(userdatabasePath);
  try {
    var loadedJson = JSON.parse(loadedData);
    console.log("[processingRegistration] Userdatabase loaded");
  } catch (e) {
    var loadedJson = [];
    console.log("[processingRegistration] Userdatabase loaded");
  }
  var loadedJsonLength = Object.keys(loadedJson).length;
  for (var i = 0; i < loadedJsonLength; i++) {
    if (loadedJson[i].android_id == userData.android_id) {
      console.log("[processingRegistration] User registered. Logging in...");
      knownUser = true;
      //startHouseholdManager(loadedJson[i].android_id)                         //startet den Haushaltmodus
      continue;
    }
  }
  if(!knownUser){
    console.log("[processingRegistration] User not registered! Registration required!");
    result = knownUser;
  } else {
    result = knownUser;
  }
}//end of processLogin function

//startHouseholdManager();////-------------------------------------------TEST---------Start-------------------------------------------------////

function searchConflictTime(allUsers, deadlines, detectedUserInfo){
  var activities = {};
  var activityDuration ={}
  var wholeDuration = {};
  var allDates = {};
  var mustManaged = 0 ;
  var conflictPlans = {};
  var sum = 0;

  for (var i = 0; i < allUsers.length; i++) {
    activities[i] = getObjects(allUsers[i],"activities","");                    //object with all activities of user with same household
    activityDuration[i] = getObjects(allUsers[i],"activityDuration","");        //object with all activities duration
    allDates[i] = "2000-01-01T"+ deadlines[i] +":00Z";                          //object with all deadlines as a full dummy dates (better send complete date)
  }
  for (var i = 0; i < allUsers.length; i++) {                                   //calculation of the whole activities duration
    for (var x = 0; x < allUsers.length; x++) {
      sum += Object.values(activityDuration[i])[x];
    }
    wholeDuration[i] = sum * 1000; //maybe in minutes!!
  }
  for (var x = 0; x < allUsers.length; x++) {                                   //check conflict schedules with deadlines and wholeDuration
    for (var i = x+1; i < allUsers.length; i++) {
      var firstDate = new Date(allDates[x])
      var secDate = new Date(allDates[i])

      if (firstDate.getTime() < secDate.getTime() - wholeDuration[i] || firstDate.getTime() > secDate.getTime()){ //Don't need create new activities plan
        break;
      }
      else {
        conflictPlans[mustManaged] = allUsers[x];
        mustManaged += 1;
        conflictPlans[mustManaged] = allUsers[i];
        break;
        }
      }
    }
    for (var i = 0; i < allUsers.length; i++) {
      if (detectedUserInfo[0] == conflictPlans[i]){                             //check the user
        if(!startActivitiesManager(conflictPlans, mustManaged)){                     //if there is a conflict, start function to resolve conflict
          result = "ATTENTION! Schedule conflict with other residents";
        }
        break;
      }
      else {
        console.log(inspect("No conflicts!", { colors: true, depth: Infinity }));
        result = "Schedule executable without conflicts";
        break;
      }
    }
}//end of searchConflictTime()

function startActivitiesManager(conflictPlans, mustManaged) {
  var ressourceBath = false;                                                    ////////////////////////////////////
  var ressourceFöhn = false;                                                    //       Mögliches Vorgehen       //
  var ressourceKitchen = false;                                                 //->Nach der Priorität (5,4,3.. ) //
  var priorityCheck = {};                                                       //->Aktivitäten rausziehen        //
  var priorityCounter = 5;                                                      //->Dauer der Aktivität zu der An-//
                                                                                // fangszeit addieren und Über-   //
  for (var i = 0; i < mustManaged+1; i++) {                                     // schneidungen prüfen(Ressource  //
    priorityCheck[i] = getObjects(conflictPlans[i],"priority","5");             // schonbesetzt?)                 //
  }                                                                             //->Neuvergabe von Reihenfolge    //
  //console.log(inspect(priorityCheck,{ colors: true, depth: Infinity }));      ////////////////////////////////////
  return true; //ActivityManager successfull
}//end of function startActivitiesManager()

function startHouseholdManager(passedData){
  var loadedData = fs.readFileSync("./Household.json");                         //dummy JSON with information about the users
  var loadedJson = JSON.parse(loadedData);
  var android_id = passedData.android_id.toString();

  var detectedUserInfo = getObjects(loadedJson,"div_id" , android_id); //<-----------change parameter !!!
  var allUsers =  getObjects(loadedJson,"house_id",passedData.house_id);

  var deadlines =  getObjects(allUsers,"deadline","");
  searchConflictTime(allUsers, deadlines, detectedUserInfo);

}//end of function startHouseholdManager()


//Die Bindung an den Port sollte als letztes im Code stehen
//Start server
app.listen(settings.port, function(){
  console.log("Dienstgeber ist nun auf Port " + settings.port + " verfuegbar.");
});
