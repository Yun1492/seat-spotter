package models;

// TO be modified for accepting userID
public class UploadData {
    //private String location;
    private int crowdedness;

    public UploadData() {}

    public UploadData(int c) {
        //this.location = loc;
        this.crowdedness = c;
    }

    //public void setLocation(String loc) { this.location = loc; }

    //public String getLocation() { return this.location; }

    public void setCrowdedness(int c) { this.crowdedness = c; }

    public int getCrowdedness() { return this.crowdedness; }
}
