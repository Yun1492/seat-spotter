package models;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.Timestamp;

public class CrowdInfo {

    //private final int id;
    private int crowdedness;
    private Timestamp datetime;

    // Default constructor required for Firebase 
    public CrowdInfo() {}

    public CrowdInfo(int crowdedness, Timestamp datetime) {
        //this.id = id;
        this.crowdedness = crowdedness;
        this.datetime = datetime;
    };

    public int getCrowdedness() { return crowdedness; }

    public void setCrowdedness(int crowdedness) { this.crowdedness = crowdedness; }

    public Timestamp getDatetime() { return datetime; }

    public void setDatetime(Timestamp datetime) { this.datetime = datetime; }

    public Map<String, Object> serialize() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(this, Map.class);
    }

}
