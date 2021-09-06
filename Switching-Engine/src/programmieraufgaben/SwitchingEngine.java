package programmieraufgaben;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Eine vereinfachte Switch Engine mit folgenden möglichen Kommandos:
 * frame <Eingangsportnummer> <Absenderadresse> <Zieladresse>,
 * table,
 * statistics,
 * del Xs bzw. del Xmin,
 * exit
 */
public class SwitchingEngine {
    private List<String[]> table; //die Tabelle wo der Verlauf gespeichert wird
    private int ports; //Globale Variable für Anzahl der Ports
    private int[] stats; //um wir die Statistiken aufspüren

    /**
     * Diese Methode überprüft die Eingabe und erstellt die für den
     * weiteren Funktionsablauf nötige Datenstruktur
     * @param portNumber Anzahl der Ports, die der Switch verwalten soll
     * @return Gibt bei erfolgreicher erstellung TRUE sonst FALSE zurück
     */
    public boolean createSwitch(int portNumber) {
        if (portNumber > 0){ //checkt ob Portsanzahl Positiv ist
            ports = portNumber; //Portsanzahl wird zugewiesen
            stats = new int[ports];
            table = new ArrayList<>();
            System.out.println("Ein " + ports + "-Port-Switch wurde erzeugt.\n");
            return true;
        }
        return false;
    }

    /**
     * Diese Methode überprüft und interpretiert die Eingaben und führt
     * die geforderten Funktionen aus.
     * @param command Anweisung die der Switch verarbeiten soll
     * @return Gibt an ob der Switch beendet werden soll: TRUE beenden, FALSE weitermachen
     */
    public boolean handleCommand(String command) {

        String[] befehl = command.split(" ",2);  //die Eingabe wird auf 2 geteilt, Kommando & Argumente
        boolean invalid = true; //initialisiert mit TRUE
        switch (befehl[0]){ //Falls ein nicht-Abbruch-Kommando durchgefürht wird, wird invalid auf FALSE gesetzt
            case "frame":invalid=cmdFrame(befehl[1]);break;
            case "table":invalid=cmdTable();break;
            case "statistics":invalid=cmdStatistics();break;
            case "del":invalid=cmdDel(befehl[1]);break;
            case "exit":break;
            default:System.out.println("Ungültige Eingabe!\n");return false;
        }
        return invalid;
    }

    /**
     * Diese Methode zeigt die Liste "table" in Format an
     * @return false
     */
    public Boolean cmdTable(){
        String formatierung = " %5s\t %4s \t %-15s %n"; //table alignment format
        System.out.format("Adresse\t Port \t Zeit\n"); //Überschrift
        for (String[] entry : table)
            System.out.format(formatierung, entry[1], entry[0], entry[2]);
        System.out.println();
        return false;
    }

    /**
     * Diese Methode zeigt das "stats" bzw. statistics in Format an
     * selbe Ästhetik wie cmdTable()
     * @return false
     */
    public boolean cmdStatistics(){
        String formatierung = " %-4s \t %-4s %n";
        System.out.format("Port \t Frame\n");
        for (int i =0; i<stats.length; i++ )
            System.out.format(formatierung,i+1,stats[i]);
        System.out.println();
        return false;
    }

    /**
     * Diese Methode liest die Argumente von frame-Kommando und entscheidet entsprechend des
     * Zustands der Engine, welche Folgeaktion fur den Frame notwendig ist und gibt diese aus. ¨
     * Außerdem kommt es gegebenfalls zu einer Aktualisierung der Switch-Tabelle.
     * @param args : die frame-Argumente <Eingangsportnummer> <Absenderadresse> <Zieladresse>
     * @return false
     */
    public boolean cmdFrame(String args){
        String[] params = args.split(" ");
        String[] entry= new String[3];

        //Ob die Eingaben gültig sind
        if(params.length == 3 && args.matches("\\d{1,3}\\s\\d{1,3}\\s\\d{1,3}")){
            int einPort = Integer.parseInt(params[0]);
            int absendAdress = Integer.parseInt(params[1]);
            int zielAdress = Integer.parseInt(params[2]);

            //Ob die Regeln gehalten sind
            if (einPort<1 || einPort>ports || absendAdress < 1 || absendAdress > 254 || zielAdress < 1 || zielAdress > 255){
                System.out.println("Ungültige Eingabe!\n");
                return false;
            }


            entry[0] = params[0];
            stats[Integer.parseInt(params[0])-1]++; //Port ist für die Eingabe benutzt
            entry[1] = params[1];
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            entry[2] = format.format( new Date() );

            //Ob es sich um Broadcast Anfrage handelt
            if (params[2].equals("255")) {
                System.out.println("Broadcast: Ausgabe auf allen Ports außer Port "+entry[0]+".\n");
                for (int i=1; i<=ports; i++) {
                    if (i != Integer.parseInt(entry[0]))
                        stats[i-1]++;
                }
                addToTable(entry);
                return false;
            }


            for (String[] element : table){
                //Ob die Zieladresse in der Switch-Tabelle schon vorhanden ist
                if (element[1].equals(params[2])){

                    //Wenn der Eingangsport & der gespeicherte Port der Zieladresse gleich sind
                    if (element[0].equals(entry[0])){
                        System.out.println("Frame wird gefiltert und verworfen.\n");
                        addToTable(entry);
                        return false;
                    }

                    //sonst Weiterleitung, bei der der Frame auf Port x ausgegeben wird.
                    System.out.println("Ausgabe auf Port "+element[0]+".\n");
                    stats[Integer.parseInt(element[0])-1]++;
                    addToTable(entry);
                    return false;
                }
            }

            //Ob es keinen Eintrag für Zieladresse in der Switch-Tabelle gibt
            System.out.println("Ausgabe auf allen Ports außer Port "+entry[0]+".\n");
            for (int i=1; i<=ports; i++) {
                if (i != Integer.parseInt(entry[0]))
                    stats[i-1]++;
            }
            addToTable(entry);
            return false;

        }
        System.out.println("Ungültige Eingabe!\n");
        return false;
    }


    /**
     *Diese Methode fügt ein Eintrag zu der Tabelle in sortierter Reihenfolge nach Adresse
     * @param entry : die hinzuzufügende Zeile in der Tabelle (List table)
     */
    public void addToTable(String[] entry){
        int indx = 0;
        boolean duplicate = false;

        for (int i=0; i<table.size(); i++){
            if (table.get(i)[1].equals(entry[1])){ //checkt ob Adresse schon gespeichert ist & setzt duplicate auf TRUE
                duplicate = true;
                break;
            }
            //wenn eine größere Adresse erreicht wird, brecht der Durchlauf, und die neue Adresse davor gelegt.
            if(Integer.parseInt(table.get(i)[1]) > Integer.parseInt(entry[1]))
                break;
            indx++;
        }

        if (duplicate) { //wenn Duplikat vorhanden ist dann wird der Eintrag umgesetzt
            table.set(indx, entry);
        }else{ //sonst wird im richtigen reihenfolge hinzugefügt
            table.add(indx, entry);
        }
    }


    /**
     * Prüft ob die Eingabe gültig ist und wandelt es in Millisekunden um, folgt der Aufruf von deleteOlderThan()
     * @param zeitPunkt Benutzer Eingabe für Zeit
     * @return false
     */
    public boolean cmdDel(String zeitPunkt){

        if (zeitPunkt.matches("^\\d++min")) { //Ob die Eingabe in richtige Format eingegeben wurde
            //extrahiert die min Zeit und rechnet die ms
            long miliSec = Integer.parseInt(zeitPunkt.split("m")[0]) * 60000;
            deleteOlderThan(miliSec);
            return false;
        }
        if (zeitPunkt.matches("^\\d++s")){
            int miliSec = Integer.parseInt(zeitPunkt.split("s")[0]) * 1000;
            deleteOlderThan(miliSec);
            return false;
        }

        System.out.println("Ungültige Eingabe!\n");
        return false;
    }


    /**
     * Entfernt von der Liste "table" alle Einträge die älter als @param
     * @param miliSec : die Eingegebene Zeit nach der Umwandlung zum Millisekunden
     */
    public void deleteOlderThan(long miliSec){
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            String now = format.format(new Date());
            Iterator<String[]> itr = table.iterator(); //für das Durchlaufen und Löschen von Elementen der Liste "table"
            List<String > oldEntries = new ArrayList<>();  //speichert die zu löschenden Adressen
            long diff; //die Zeitunterschied zwischen Jetzt (now) und die gespeicherte Zeiten

            while (itr.hasNext()) {
                String[] entry = itr.next();
                Date d1 = format.parse(now);
                Date d2 = format.parse(entry[2]);
                Calendar c1 = Calendar.getInstance();
                Calendar c2 = Calendar.getInstance();
                c1.setTime(d1);
                c2.setTime(d2);

                if (c1.get(Calendar.HOUR_OF_DAY) < 12) {  //Ob now im neuen Tag befindet
                    c1.set(Calendar.DAY_OF_YEAR, c1.get(Calendar.DAY_OF_YEAR) + 1);
                }

                diff = c1.getTimeInMillis() - c2.getTimeInMillis();
                if (diff > miliSec) {
                    oldEntries.add(entry[1]);
                    itr.remove();
                }
            }
            if (oldEntries.size() == 0)
                System.out.println("Keine Adressen wurden aus der Switch-Tabelle gelöscht.\n");
            else
                System.out.println("Folgende Adressen wurden aus der Switch-Tabelle gelöscht: "+String.join(", ", oldEntries)+"\n");
        }catch (ParseException ignored){}
    }
}

