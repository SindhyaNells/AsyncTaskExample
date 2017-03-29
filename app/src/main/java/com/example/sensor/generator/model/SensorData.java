package com.example.sensor.generator.model;

 import java.util.ArrayList;
 import java.util.List;

/**
 * Created by sindhya on 3/17/17.
 */
public class SensorData {

    public List<SensorDataStructure> data = null;

    private static SensorData instance = null;

    public SensorData(){
        data=new ArrayList<>();
    }
    public static SensorData getInstance() {
        if (instance == null) {
            instance = new SensorData();
        }
        return instance;
    }

    public void add(SensorDataStructure sensorDataStructure) {
        data.add(sensorDataStructure);
    }

    public SensorDataStructure get(int position) {
        if (data != null && data.size() > position)
            return data.get(position);
        return null;
    }

    public int getSize() {
        return data.size();
    }

    public void clear() {
        data.clear();
    }


}
