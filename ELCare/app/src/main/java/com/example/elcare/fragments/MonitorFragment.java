package com.example.elcare.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.elcare.R;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MonitorFragment extends Fragment {

    private TextView temp;
    private TextView tempMonitoring;

    private TextView motionStatus;
    private TextView motionMonitoring;

    private TextView soundStatus;
    private TextView soundMonitoring;

    private TextView humidityStatus;

    public static MonitorFragment newInstance() {
        return new MonitorFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.monitor_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG", "clicked");
                getFragmentManager().beginTransaction().replace(R.id.home_fragment, MainFragment.newInstance()).commit();
            }
        });

        GetMonitorData task = new GetMonitorData();
        task.execute("https://scdf-x-ibm-web.herokuapp.com/hardware");

        temp = view.findViewById(R.id.temp);
        tempMonitoring = view.findViewById(R.id.temp_status);
        motionStatus = view.findViewById(R.id.motion);
        motionMonitoring = view.findViewById(R.id.motion_status);
        soundStatus = view.findViewById(R.id.sound);
        soundMonitoring = view.findViewById(R.id.sound_status);
    }

    public class GetMonitorData extends AsyncTask<String, Void, JSONObject>{

        @Override
        protected JSONObject doInBackground(String... params) {
            HttpURLConnection conn = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                InputStream stream = conn.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                String line = reader.readLine();

                JSONObject json = new JSONObject(line);

                Log.d("DEBUG", "doInBackground: " + json);

                return json;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(conn != null){
                    conn.disconnect();
                }
                if(reader != null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);

            try {
                String temp = json.get("temperature").toString();
                String hum = json.get("humidity").toString();
                String motion = json.get("sensor").toString();
                String sound = json.get("sound").toString();

                Map<String, String> values = new HashMap<String, String>(){
                    {
                        put("1", "NOT OK");
                        put("0", "OK");
                    }
                };

                Log.d("DEBUG", "onPostExecute: " + temp);

                updateTemp(temp, hum);
                updateMotion(values.get(motion));
                updateSound(values.get(sound));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateTemp(String temperature, String humidity){
        temp.setText(temperature + "C " + humidity + "%");
    }

    private void updateMotion(String status){
        if(status == "OK"){
            motionStatus.setTextColor(getResources().getColor(R.color.green));
        }else{
            motionStatus.setTextColor(getResources().getColor(R.color.red));
        }
        motionStatus.setText(status);
    }

    private void updateSound(String status){
        if(status == "OK"){
            soundStatus.setTextColor(getResources().getColor(R.color.green));
        }else{
            soundStatus.setTextColor(getResources().getColor(R.color.red));
        }
        soundStatus.setText(status);
    }

}