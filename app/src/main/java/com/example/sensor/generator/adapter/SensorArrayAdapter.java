package com.example.sensor.generator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.sensor.generator.R;
import com.example.sensor.generator.model.SensorDataStructure;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sindhya on 3/19/17.
 */
public class SensorArrayAdapter extends ArrayAdapter<SensorDataStructure> {

    private List<SensorDataStructure> sensorDataList=new ArrayList<>();
    private static LayoutInflater inflater=null;

    public SensorArrayAdapter(Context context, int resource, List<SensorDataStructure> sensorList) {
        super(context, resource,sensorList);
        this.sensorDataList=new ArrayList<>();
        sensorDataList.addAll(sensorList);
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class ViewHolder{
        TextView list_output_name;
        TextView list_temp_name;
        TextView list_humidity_name;
        TextView list_activity_name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.activity_list_item,null);
            holder=new ViewHolder();
            holder.list_output_name=(TextView)convertView.findViewById(R.id.list_item_output_name);
            holder.list_temp_name=(TextView)convertView.findViewById(R.id.list_item_temperature);
            holder.list_humidity_name=(TextView)convertView.findViewById(R.id.list_item_humidity);
            holder.list_activity_name=(TextView)convertView.findViewById(R.id.list_item_activity);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }

        SensorDataStructure sensorObj=sensorDataList.get(position);
        holder.list_output_name.setText("Output "+(position+1)+":");
        holder.list_temp_name.setText("Temperature: "+sensorObj.temperature);
        holder.list_humidity_name.setText("Humidity: "+sensorObj.humidity);
        holder.list_activity_name.setText("Activity: "+sensorObj.activity);

        return convertView;
    }
}
