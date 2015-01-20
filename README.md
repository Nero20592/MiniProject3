MiniProject3
============

Das Programm liegt zum Ausführen als jar-Datei vor.
Die Jar muss im selben Ordner liegen wie der graphiz-Ordner und der c-lib Ordner liegen,
damit die Graphen zur visualisierung gezeichnet werden und die LTLFormulas transformiert werden können.


Wie können Eingaben gemacht werden?
In einer CSV-Datei, welche als Kripke Structure (KS) über den Button 'load KS' 

geladen werden kann.
Das Format ist hierbei wie folgt:

Für einen Startzustand:
I,[name][,AP1 ... ,APn]
Für einen anderen Zustand:
S,[name][,AP1 ... ,APn]
Für eine Transition:
T,[from],[to],[name]

Hierbei müssen zunächst Zustände und anschließend Transitions definiert werden. 
Wichtig ist außerdem, dass für den construct product, Zustände nicht den selben Namen 
haben dürfen! (auch nicht, wenn sie aus einer anderen KS stammen)
Außerdem sind die atomaren propositionen für einen Zustand optional.

Wie kann eine Kripke-Structure in ein Büchi-Automata transformiert werden?
Nachdem eine KS über "load KS" geladen wurde, kann mithilfe des "Transform KStoBA"-
Buttons die KS zu einem Büchi Automata transformiert werden. Sobald die KS korrekt 
transformiert wurde leuchtet die LED "KStoBA" grün.

Wie kann eine LTL-Formula in ein Büchi-Automata transformiert werden?
Die LTL-Formula kann über das Textfeld eingegeben werden und über den Button 
"Transform LTLtoBA" in einen Büchi Automata (BA) transformiert werden. Hierbei sind 
folgende Eingaben möglich:
Propositonal Symbols:
 true, false  
 any lowercase string

 Boolean operators:
 !   (negation)
 ->  (implication) 
 <-> (equivalence)
 &&  (and)
 ||  (or)

 Temporal operators:
 []  (always)
 <>  (eventually)
 U   (until)
 V   (release)
 X   (next)
Sobald die Formula korrekt transformiert wurde, leuchtet die LED "LTLtoBA" grün.

Wie kann ein Kreuzprodukt gebildet werden?
Sobald über den "Load KS"-Button eine KS geladen und mithilfe des "Transform 
KStoBA"-Buttons in ein Büchi-Automata transformiert wurde und eine LTLFormula in ein 
Büchi Automata transformiert wurde, kann über den Button 'construct product' das 
Kreuzprodukt zwischen beiden BAs erstellt werden, welche dadurch auch als Graph 
angezeigt wird und im Ordner in welchem sich auch die Jar befindet als png-Datei 
gespeichert wird.

Wie kann die LTLFormula kontrolliert werden. 
Wurden LTLFormula und die KS erfolgreich transformiert, kann über den "Check LTL"-
Button gecheckt werden, ob die LTLFormula die KS erfüllt. Ist dies der Fall, leuchtet 
die LED "LTL satisfied" grün. Sollte die LTLFormula nicht erfüllt sein, leuchtet die 
LED rot.
