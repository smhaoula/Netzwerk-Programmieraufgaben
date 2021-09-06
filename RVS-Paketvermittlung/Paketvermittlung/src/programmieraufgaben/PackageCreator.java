package programmieraufgaben;

import java.util.*;
import java.text.*;


public class PackageCreator {

    /**
     * Hier sollen die Kommandozeilen-Abfragen abgefragt und die Antworten
     * gespeichert werden
     * Es sollte auf Fehlerbehandlung geachtet werden (falsche Eingaben, ...)
     *
     * @param dataPackage Hier wird das Objekt übergeben in das die abgefragten Werte gespeichert werden sollen
     * @return Gibt das als Parameter übergebene Objekt, dass mit den abgefragten Werten befüllt wurde zurück
     */

    public DataPackage fillParameters(DataPackage dataPackage) {
        //ein scanner erstellen um die Eingaben abzulesen.
        Scanner eingabe = new Scanner(System.in);

        ////Version abfragen
        System.out.print("Version: ");
        int version = eingabe.nextInt();
        //prüfung der gültigen Eingabe
        while (version!=4 && version!=6) {
            System.out.println("Falsche Eingabe! (Entweder 4 oder 6)");
            System.out.print("Version: ");
            version = eingabe.nextInt();
        }
        dataPackage.setVersion(version); //eingegebene Wert zuweisen

        ////Absender und Empfänger abfragen
        System.out.print("Absender: ");
        dataPackage.setSender(eingabe.next()); //eingegebene Wert zuweisen
        System.out.print("Empfaenger: ");
        dataPackage.setReceiver(eingabe.next()); //eingegebene Wert zuweisen

        ////Nachricht Eingabebereich
        System.out.println("Nachricht: ");
        String last; //speichert die Eingabe der letzten Zeil
        StringBuilder nachricht = new StringBuilder(eingabe.nextLine());

        boolean erste = true; //dient die Trennung von erster Zeil im Eingabe
        //für mehrzeilige Eingabe
        while (eingabe.hasNextLine()) {
            last = eingabe.nextLine(); //Eingabe ablesen
            if (erste){ //Trennung von erster Zeil
                nachricht.append(last);
                erste=false;
                continue;
            }
            if (last.equals("."))
                break;
            nachricht.append("\\n").append(last); //appendiert die "\n" statt Zeilenumbruch
        }
        dataPackage.setDatenteil(nachricht.toString()); //eingegebene Wert zuweisen

        return dataPackage;
    }

    /**
     * Aus dem als Parameter übergebenen Paket sollen die Informationen
     * ausgelesen und in einzelne Datenpakete aufgeteilt werden
     *
     * @param dataPackage Hier wird das Objekt übergeben in das das Resultat gespeichert werden soll
     * @return Gibt das als Parameter übergebene Objekt mit den aufgeteiltet Datenpaketen zurück
     */
    public List<DataPackage> splitPackage(DataPackage dataPackage) {
       List<DataPackage> dataPackages = new LinkedList<>();

        //länge der Datenteil
        int laenge = dataPackage.getDataPackageLength();
        //länge der Nachricht
        int totalletters = dataPackage.getDatenteil().length();

        String nachricht = dataPackage.getDatenteil();

        //boolean ob das gewählte Teil gesendet werden kann
        boolean zuweisbar;

        //Durchlauf um die ganze Nachrichts Charakter
        int i = 0;
        while(i<totalletters){
            //erstellen wir eine neue Datapackage mit eingenschaften der letzten
            DataPackage dataPackNew = new DataPackage(dataPackage);
            zuweisbar = false;// initialisierung

            //Überspringen die Leerzeichen am anfang der Block/Teil
            if(nachricht.charAt(i)==' '){
                i++;
                continue;
            }

            //Durchlauf um die Charakters im Block/Teil
            for (int j = laenge; j>=0; j--){

                //prüft für gültigkeit
                if (i+j >= totalletters || !(Character.isLetter(nachricht.charAt(i+j)))) {
                    zuweisbar = true;
                    if (i+j>=totalletters)
                        continue;
                }

                //erzwingt überspringen unter paar Bedingungen und damit \n nicht getrennt wurde.
                if (i+j+2<=totalletters && i+j>1 && nachricht.substring(i+j,i+j+2).equals("\\n"))
                    continue;

                //wenn gültig, erstellt eine neue Datapaket
                if (i+j < totalletters && j<laenge && zuweisbar && nachricht.charAt(i+j)!=' '){
                    dataPackNew.setDatenteil(nachricht.substring(i, i + j + 1));
                    dataPackNew.setPacketId(dataPackages.size());
                    dataPackages.add(dataPackNew);
                    i = i + j + 1;
                    break;
                }
            }

            //falls wir im Block/Teil kein folge von Zeichen, die die Voraussetzungen erfüllt
            if (!zuweisbar){

                //K = um wie viel Zeichen/Buchstaben überschreitet das Wort die Grenze
                int k=laenge;
                //Zählen von extra Buchstaben/Zeichen
                while(i+k<totalletters){
                    if (!Character.isLetter(nachricht.charAt(i+k)))
                        break;
                    k++;
                }
                System.out.println("Die Nachricht kann nicht versendet werden, da sie ein Wort mit Länge "+k+" > "+laenge+" enthällt");
                break;
            }
        }

        return dataPackages;
    }

    /**
     * Diese Methode gibt den Inhalt der empfangenen Pakete in der Komandozeile aus
     *
     * @param dataPackages Hier wird die Liste übergeben, deren Elemente in die Kommandozeile ausgegeben werden sollen
     */
    public void printOutPackage(List<DataPackage> dataPackages) {


        System.out.println("Aufteilung in "+dataPackages.size()+" Pakete:");
        System.out.print("[");
        //prüft ob wir überhaupt Pakete haben
        if (dataPackages.size()>0) {
            //prüft ob mehrere Pakete gibts
            if (dataPackages.size()>1) {
                //Durchlauf um die zu dargestellten Pakete ausser die Letzte.
                for (int i = 0; i < dataPackages.size() - 1; i++) {
                    System.out.print("\"" + dataPackages.get(i).getDatenteil() + "\", ");
                }
            }
            //Dantenteil der letzten Pakete bzw. einzelne darstellen.
            System.out.print("\"" + dataPackages.get(dataPackages.size() - 1).getDatenteil() + "\"");
        }
        System.out.println("]");
    }
}
