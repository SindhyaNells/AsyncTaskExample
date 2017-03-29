package com.example.sensor.generator.activity;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.sensor.generator.R;
import com.example.sensor.generator.adapter.SensorArrayAdapter;
import com.example.sensor.generator.model.SensorData;
import com.example.sensor.generator.model.SensorDataStructure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnGenerate;
    private Button btnUpload;
    private EditText editTextReadings;
    private EditText editTextTemperature;
    private EditText editTextHumidity;
    private EditText editTextActivity;
    private ListView listViewOutput;
    private SensorArrayAdapter sensorAdapter;

    SensorData sensorDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editTextTemperature=(EditText)findViewById(R.id.edit_text_temperature);
        editTextTemperature.setEnabled(false);
        editTextHumidity=(EditText)findViewById(R.id.edit_text_humidity);
        editTextHumidity.setEnabled(false);
        editTextActivity=(EditText)findViewById(R.id.edit_text_activity);
        editTextActivity.setEnabled(false);
        editTextReadings=(EditText) findViewById(R.id.edit_text_reading);
        btnGenerate=(Button)findViewById(R.id.btn_generate);
        btnGenerate.setOnClickListener(this);
        btnUpload=(Button)findViewById(R.id.btn_upload);
        btnUpload.setOnClickListener(this);

        listViewOutput=(ListView)findViewById(R.id.list_output);

    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        if(id==R.id.btn_generate){
            //TODO: async task to generate temperature values

            FetchSensorData fetchSensorData=new FetchSensorData();
            fetchSensorData.execute(editTextReadings.getText().toString());

        }else if(id==R.id.btn_upload){

            SaveSensorData saveSensorData=new SaveSensorData();
            saveSensorData.execute(sensorDataList);

        }
    }

    public class SaveSensorData extends AsyncTask<SensorData,Void,Void>{

        private final String LOG_TAG=SaveSensorData.class.getSimpleName();

        @Override
        protected Void doInBackground(SensorData... param) {

            HttpURLConnection urlConnection=null;
            List<SensorDataStructure> sensorList=param[0].data;

            StringBuilder sb=new StringBuilder();
            try {
                String base_url = "http://192.168.29.237:8181/saveSensorData";


                Uri saveSensorUri = Uri.parse(base_url).buildUpon().build();

                URL url = new URL(saveSensorUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.connect();

                try {
                    JSONArray jsonArray = new JSONArray();

                    for (int i = 0; i < sensorList.size(); i++) {
                        JSONObject sensorObj = new JSONObject();
                        sensorObj.put("activity", sensorList.get(i).getActivity());
                        sensorObj.put("temperature",sensorList.get(i).getTemperature());
                        sensorObj.put("humidity",sensorList.get(i).getHumidity());
                        jsonArray.put(sensorObj);
                    }

                    OutputStreamWriter os = new OutputStreamWriter(urlConnection.getOutputStream());
                    os.write(jsonArray.toString());
                    os.close();

                    int HttpResult =urlConnection.getResponseCode();
                    if(HttpResult ==HttpURLConnection.HTTP_OK){
                        BufferedReader br = new BufferedReader(new InputStreamReader(
                                urlConnection.getInputStream(),"utf-8"));
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        br.close();

                        System.out.println(""+sb.toString());

                    }else{
                        System.out.println(urlConnection.getResponseMessage());
                    }

                }catch (JSONException e){
                    Log.e(LOG_TAG,e.getMessage());
                }

            }catch (IOException e){
                Log.e(LOG_TAG,e.getMessage());
            }finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new AlertDialog.Builder(MainActivity.this).setTitle("Sensor Driver")
                    .setMessage("Sensor Data Saved!")
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .show();
        }
    }

    public class FetchSensorData extends AsyncTask<String,Void,SensorData>{


        private final String LOG_TAG=FetchSensorData.class.getSimpleName();


        private SensorData getSensorDataFromJson(String sensorDataJson) throws JSONException{


            JSONArray jsonArray=new JSONArray(sensorDataJson);
            SensorData sensorData=new SensorData();
            for(int i=0;i<jsonArray.length();i++){

                SensorDataStructure sensorDataStructure=new SensorDataStructure(jsonArray.getJSONObject(i));
                sensorData.add(sensorDataStructure);

            }

            return sensorData;
        }

        @Override
        protected SensorData doInBackground(String... params) {

            HttpURLConnection urlConnection=null;
            BufferedReader bufferedReader=null;

            String sensorDataJson=null;

            String readings=params[0];

            try {

                String base_url = "http://192.168.29.237:8181/generateSensorData";
                final String READING_PARAM = "readings";

                Uri sensorUri = Uri.parse(base_url).buildUpon().appendQueryParameter(READING_PARAM, readings).build();

                URL url=new URL(sensorUri.toString());

                urlConnection=(HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream=urlConnection.getInputStream();
                StringBuffer buffer=new StringBuffer();

                if(inputStream==null){
                    return null;
                }

                bufferedReader=new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while((line=bufferedReader.readLine())!=null){
                    buffer.append(line+"\n");
                }

                if(buffer.length()==0){
                    return null;
                }

                sensorDataJson=buffer.toString();

                Log.v(LOG_TAG,"SensorJsonStr: "+sensorDataJson);

            } catch (IOException e) {
                Log.e(LOG_TAG,e.getMessage());
                e.printStackTrace();
            }catch (Exception e){
                Log.e(LOG_TAG,e.getMessage());
            }
            finally {
                if(urlConnection!=null){
                    urlConnection.disconnect();
                }
                if(bufferedReader!=null){
                    try{
                        bufferedReader.close();
                    }catch (final IOException e){
                        Log.e(LOG_TAG,"Error closing stream",e);
                    }
                }

            }


            try {
                return getSensorDataFromJson(sensorDataJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(SensorData sensorData) {
            if(sensorData!=null){
                sensorDataList=sensorData;
                for(int i=0;i<sensorData.getSize();i++){
                    SensorDataStructure sensorDataStructure=sensorData.data.get(i);
                    if(sensorDataStructure!=null) {
                        editTextTemperature.setText(sensorDataStructure.temperature);
                        editTextHumidity.setText(sensorDataStructure.humidity);
                        editTextActivity.setText(sensorDataStructure.activity);
                    }
                }


                sensorAdapter=new SensorArrayAdapter(MainActivity.this,R.layout.activity_list_item,sensorData.data);
                listViewOutput.setAdapter(sensorAdapter);

            }
        }
    }


}
