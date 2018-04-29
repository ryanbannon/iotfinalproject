package com.ryanbannon.iotfinalproject;

/**
 * Created by ryans on 27/04/2018.
 */

public class Photo {

    private String timestamp;
    private String URL;

    public Photo(){
    }

    public Photo(String timestamp, String URL){
        this.timestamp = timestamp;
        this.URL = URL;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
