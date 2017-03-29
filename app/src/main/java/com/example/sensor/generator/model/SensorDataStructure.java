package com.example.sensor.generator.model;

import org.json.JSONObject;

/**
 * Created by sindhya on 3/17/17.
 */
public class SensorDataStructure {

    public String temperature;
    public String humidity;
    public String activity;

    public SensorDataStructure(){

    }
    public SensorDataStructure(JSONObject jsonObject) {

        try {
            temperature = jsonObject.getString("temperature");
            humidity = jsonObject.getString("humidity");
            activity = jsonObject.getString("activity");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getTemperature() {
        return temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getActivity() {
        return activity;
    }
}
