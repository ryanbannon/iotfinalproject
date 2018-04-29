package com.ryanbannon.iotfinalproject;

/**
 * Created by ryans on 27/04/2018.
 */

public class Weight {

    private String date;
    private String weight;

    public Weight(){
    }

    public Weight(String date, String weight){
        this.date = date;
        this.weight = weight;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
