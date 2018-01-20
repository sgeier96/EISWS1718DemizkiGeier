var fs = require('fs');                   //I/O-Funktionen
var express = require("express");         //app.get, etc.
var request = require("request");         //request(url, function(err, res, body))), etc.
var bodyParser = require("body-parser");  //parser for i.e. JSON
const writeJsonFile = require('write-json-file'); //Stringify and write JSON to a file atomically
const loadJsonFile = require('load-json-file'); //Read and parse a JSON file. Same author as ⬆

var app = express();
var households = [];

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
//----------------------------------------------------------------------------//
//----------------------------------PSEUDO-CODE!!!!!!!!!!!!!!!!---------------//
//----------Abgebrochen. Datenstruktur der JSON notwendig---------------------//
//----------------------------------------------------------------------------//

//Vermutlich besser, die Übernahme der Daten wie Household, User, etc. über HTTP-POST zu machen
/*app.post("/*", function(request, response){
  var household = "";

  startHouseholdManager(household);
});*/

app.get('/*', function(request, response){
  response.send("There you have your GET-Answer");
});

app.post('/', function(request, response){
  console.log(request.body);  //our JSON

  var data = request.body;
  processRequestParameter(data);
  //processRegistration(data);

  response.send(JSON.stringify(request.body));
});



function processRequestParameter(data){
  switch(data.parameter){
    case 'registration':
      processRegistration(data.user);
      break;
    case 'login':
      processLogin(data.user);
      break;
      //...
    default:
      console.log("unknown parameter");
  }
}

function processRegistration(userData){
  console.log("Whole passedJsonAsObject: " + JSON.stringify(userData));
  console.log("android_id:" + JSON.stringify(userData.android_id));
  console.log("username: " + JSON.stringify(userData.username));

  var loadedJson = '';
  loadJsonFile('userdatabase.json').then(json => {
    loadedJson = json;
  });

  for (var key in loadedJson){
    if(loadedJson[key] != userData.android_id){ //if old json doesnt content newly passed User
      console.log("[processRegistration] New user found! Updating userdatabase...");
      writeJsonFile.sync('userdatabase.json', userData);
      console.log("[processRegistration] Done updating userdatabase!");
    } else {
      console.log("[processRegistration] User with android_id '" +
                  userData.android_id + "' already registered! Aborting...");
      //EITHER HTTP-STATUSCODE 412 (Precondition Failed) or 422 (Unprocessable Entity)
      break;
    }
  } //end of json-loop
}//end of processRegistration function

function processLogin(userData){
  var loadedJson = '';
  loadedJsonFile('userdatabase.json').then(json => {
    loadedJson = json;
  });

  for(var key in loadedJson){
    if(loadedJson[key] == userData.android_id){
      console.log("[processLogin] User found! Logging in...");
    } else {
      console.log("[processLogin] User with android_id '" +
                  userData.android_id + "' not found! Registration necessary!");
    }
  }//end of json-loop
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
