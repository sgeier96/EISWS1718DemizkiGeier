var fs = require('fs');                   //I/O-Funktionen
var express = require("express");         //app.get, etc.
var request = require("request");         //request(url, function(err, res, body))), etc.
var bodyParser = require("body-parser");  //parser for i.e. JSON

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

app.get('/', function(request, response){
  response.send("There you have your GET-Answer");
});

app.post('/', function(request, response){
  console.log("[POST-Acceptor] Received data: " + JSON.stringify(request.body));
  var data = request.body;
  processRequestParameter(data);

  response.send(result);
});



function processRequestParameter(data){
  switch(data.parameter){
    case 'registration':
      processRegistration(data.user);
      break;
    case 'login':
      processLogin(data);
      break;
      //...
    default:
      console.log("unknown parameter");
  }
}

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

/*
function startHouseholdManager(household){
  var users = [];
  users = withdrawTheUsersFromHousehold(household);
  compareTheActivitiesPeriods(users);
}

function compareTheActivitiesPeriods(users){
  for(var i; i < users.size; i++){
    for(var j; j < users[i].schedule.size; j++)
    {
        if(users[i].activityInScheduleOnPosition(j).getPeriod == users[i+1].activityInScheduleOnPosition(j+1).getPeriod){

        }
    }
  }
}*/

//Die Bindung an den Port sollte als letztes im Code stehen
//Start server
app.listen(settings.port, function(){
  console.log("Dienstgeber ist nun auf Port " + settings.port + " verfuegbar.");
});
