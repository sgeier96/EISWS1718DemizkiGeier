var fs = require('fs');                   //I/O-Funktionen
var express = require("express");         //app.get, etc.
var request = require("request");         //request(url, function(err, res, body))), etc.
var bodyParser = require("body-parser");  //parser for i.e. JSON

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
  response.send(JSON.stringify(request.availableResources[0].name)); //echoing the result back
});



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
