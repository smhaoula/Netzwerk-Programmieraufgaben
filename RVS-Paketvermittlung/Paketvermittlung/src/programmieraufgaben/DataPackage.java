package programmieraufgaben;

/**
 * Hier sollen die Nutzereingaben sowie die Resultate gespeichert werden.
 * Die Struktur der Klasse und die Variablen können frei gewählt werden.
 */
public class DataPackage {
    //maximale Datenteil-Länge
    private int dataPackageLength;
    private int version;
    private String sender, receiver;
    private int packetId;
    private String datenteil;


    /**
     * Erzeugt ein DataPackage Objekt und speichert beim erzeugen die maximale Datenteil-Länge
     * @param dataPackageLength
     */
    public DataPackage(int dataPackageLength) {
        this.dataPackageLength = dataPackageLength;
    }


    public DataPackage(DataPackage dataPackage) {
        this.dataPackageLength = dataPackage.dataPackageLength;
        this.version = dataPackage.version;
        this.sender = dataPackage.sender;
        this.receiver = dataPackage.receiver;
        this.packetId = dataPackage.packetId;
        this.datenteil = dataPackage.datenteil;
    }
    /**
     * Gibt die maximale Datenteil-Länge zurück
     * @return maximale Datenteil-Länge
     */
    public int getDataPackageLength() {
        return dataPackageLength;
    }

    /**
     * Setzt die maximale Datenteil-Länge
     * @param dataPackageLength
     */
    public void setDataPackageLength(int dataPackageLength) {
        this.dataPackageLength = dataPackageLength;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public void setPacketId(int packetId) {
        this.packetId = packetId;
    }

    public int getPacketId() {
        return packetId;
    }

    public void setDatenteil(String datenteil) {
        this.datenteil = datenteil;
    }

    public String getDatenteil() {
        return datenteil;
    }

    public void setReceiver(String reciever) {
        this.receiver = reciever;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }
}
