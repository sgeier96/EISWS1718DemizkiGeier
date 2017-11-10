Leider haben wir es nicht geschafft das Projekt als .jar zu "exportieren". 
Wir hoffen das ist kein sonderlich großes Problem und die einzige Alternative die 
wir mit dem für uns neuen Programm erstellen konnten (.apk) erfüllt stattdessen einen möglichst
ähnlichen Zweck wie es die .jar tuen sollte.

In diesem RapidPrototype programmierten wir das Alleinstellungsmerkmal der Zusammensetzung von Aktivitäten mit zeitlicher Deadline.
Diese Deadline wird im späteren Endprodukt eine Busabfahrt sein, wobei wir auch dahingehend bereits untereinander Gespräche führen.
Die Abfrage an die VRS-API ist in diesem Prototypen noch nicht implementiert. Stattdessen haben wir unter assets/ eine .xml abgelegt, 
wie sie auch von der VRS geliefert wird, welche stellvertretend für die Deadline-Bestimmung verwendet wird. 
Ein Teil dieses Prototypen ist außerdem die Bestimmung und verrechnung der jetztigen Zeit. Aus Testgründen ist diese allerdings auskommentiert. 
Sollten Sie sich das anschauen möchten, ist lediglich die currentTime Zuweisung in Zeile 342 der MainActivity.java entsprechend zu kommentieren/auskommentieren.

Es galt von uns in diesem Prototypen das Risiko möglichst zu Beheben, dass ein Zeitplan mit den erforderten Daten nicht erstellt werden kann. 

Zur Prüfung dieses Risikos ist es lediglich nötig die Dauer der einzelnen Aktivitäten dahingehend anzupassen, dass der Zeitplan bis zur Deadline gar nicht
erfüllt werden könnte. In dieser Version wird das Problem so gelöst, dass die Bearbeitungsdauer der Aktivitäten mit einer niedrigen Priorität stark erniedrigt wird, 
während bei Aktivitäten mit einer hohen Priorität diese nur minimal oder gar nicht angepasst wird. 
Dies hat den Zweck, dass der Nutzer dennoch in der Lage ist die Aktivitäten zu Bearbeiten, auch wenn er dadurch dazu gezwungen ist, sich bei bestimmten Tätigkeiten
mehr zu beeilen.


Den von uns bearbeiteten Code finden Sie in der InTime.zip unter 
"InTime\mobile\src\main\java\com\example\stefangeier\intime"
