Leider haben wir es nicht geschafft das Projekt als .jar zu "exportieren". 
Wir hoffen das ist kein sonderlich gro�es Problem und die einzige Alternative die 
wir mit dem f�r uns neuen Programm erstellen konnten (.apk) erf�llt stattdessen einen m�glichst
�hnlichen Zweck wie es die .jar tuen sollte.

In diesem RapidPrototype programmierten wir das Alleinstellungsmerkmal der Zusammensetzung von Aktivit�ten mit zeitlicher Deadline.
Diese Deadline wird im sp�teren Endprodukt eine Busabfahrt sein, wobei wir auch dahingehend bereits untereinander Gespr�che f�hren.
Die Abfrage an die VRS-API ist in diesem Prototypen noch nicht implementiert. Stattdessen haben wir unter assets/ eine .xml abgelegt, 
wie sie auch von der VRS geliefert wird, welche stellvertretend f�r die Deadline-Bestimmung verwendet wird. 
Ein Teil dieses Prototypen ist au�erdem die Bestimmung und verrechnung der jetztigen Zeit. Aus Testgr�nden ist diese allerdings auskommentiert. 
Sollten Sie sich das anschauen m�chten, ist lediglich die currentTime Zuweisung in Zeile 342 der MainActivity.java entsprechend zu kommentieren/auskommentieren.

Es galt von uns in diesem Prototypen das Risiko m�glichst zu Beheben, dass ein Zeitplan mit den erforderten Daten nicht erstellt werden kann. 

Zur Pr�fung dieses Risikos ist es lediglich n�tig die Dauer der einzelnen Aktivit�ten dahingehend anzupassen, dass der Zeitplan bis zur Deadline gar nicht
erf�llt werden k�nnte. In dieser Version wird das Problem so gel�st, dass die Bearbeitungsdauer der Aktivit�ten mit einer niedrigen Priorit�t stark erniedrigt wird, 
w�hrend bei Aktivit�ten mit einer hohen Priorit�t diese nur minimal oder gar nicht angepasst wird. 
Dies hat den Zweck, dass der Nutzer dennoch in der Lage ist die Aktivit�ten zu Bearbeiten, auch wenn er dadurch dazu gezwungen ist, sich bei bestimmten T�tigkeiten
mehr zu beeilen.


Den von uns bearbeiteten Code finden Sie in der InTime.zip unter 
"InTime\mobile\src\main\java\com\example\stefangeier\intime"
