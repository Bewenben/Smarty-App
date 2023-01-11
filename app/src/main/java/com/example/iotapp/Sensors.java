package com.example.iotapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

public class Sensors extends AppCompatActivity {

        SensorManager sMgr;
        Sensor temp;
        Sensor humid;
        Sensor light;
        Sensor accelerate;
        ImageView lightimage;
        ImageView motionimage;
        FloatingActionButton floatingActionButton;
        TextView RoomText;
        TextView TempText;
        TextView HumidText;
        Spinner spinner;
        TextView desc;
        SensorEventListener templistener;
        SensorEventListener humidlistener;
//        SensorEventListener lightlistenerimage;
        SensorEventListener lightlistenertext;
        SensorEventListener acceleratelistener;
        FirebaseFirestore db;
        FirebaseUser user;
        ListenerRegistration lightlisten;
        Map<String, Object> collect;
        int lightreading = 0;
        int tempreading = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        sMgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        temp = sMgr.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        humid = sMgr.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        accelerate = sMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        light = sMgr.getDefaultSensor(Sensor.TYPE_LIGHT);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        RoomText = findViewById(R.id.textView13);
        TempText = findViewById(R.id.textView14);
        HumidText = findViewById(R.id.textView20);
        desc = findViewById(R.id.textView15);
        spinner = findViewById(R.id.spinner3);
        lightimage = findViewById(R.id.imageView8);
        motionimage = findViewById(R.id.imageView9);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Thermostat");
        arrayList.add("Lights");
        arrayList.add("Wi-Fi");
        arrayList.add("Media");
        arrayList.add("CCTV");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        Intent i = getIntent();
        String room = i.getStringExtra("Room");
        RoomText.setText(room);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String Room = parent.getItemAtPosition(position).toString();
                switch (Room) {
                    case "Thermostat":
                        desc.setText("Room Temperature");
                        TempText.setText(tempreading + " °C");
                        TempText.setTextSize(60);
                        sMgr.unregisterListener(lightlistenertext);
                        sMgr.registerListener(templistener, temp, SensorManager.SENSOR_DELAY_NORMAL);
                        break;
                    case "Lights":
                        desc.setText("Light Intensity");
                        TempText.setText(lightreading + " %");
                        TempText.setTextSize(60);
                        sMgr.unregisterListener(templistener);
                        sMgr.registerListener(lightlistenertext, light, SensorManager.SENSOR_DELAY_NORMAL);
                        break;
                    case "Wi-Fi":
                        desc.setText("Wi-Fi");
                        if (isConnected()) {
                            TempText.setText("Connected to Wi-Fi");
                            TempText.setTextSize(30);
                        } else {
                            TempText.setText("Disconnected from Wi-Fi");
                            TempText.setTextSize(25);
                        }
                        sMgr.unregisterListener(templistener);
                        sMgr.unregisterListener(lightlistenertext);
                        break;
                    case "Media":
                        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        int maxVolume = 15;
                        int currentVolumePercentage = 100 * currentVolume / maxVolume;
                        desc.setText("Volume");
                        TempText.setText(String.format("%s%%", currentVolumePercentage));
                        TempText.setTextSize(60);
                        sMgr.unregisterListener(templistener);
                        sMgr.unregisterListener(lightlistenertext);
                        break;
                    case "CCTV":
                        desc.setText("CCTV");
                        TempText.setText("Recording");
                        TempText.setTextSize(40);
                        sMgr.unregisterListener(templistener);
                        sMgr.unregisterListener(lightlistenertext);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });

//        lightlistenerimage = new SensorEventListener(){
//            @Override
//            public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//            }
//
//            @Override
//            public void onSensorChanged(SensorEvent event) {
//                if(event.values[0] > 0){
//                    Drawable imageDrawable = lightimage.getDrawable();
//                    imageDrawable = DrawableCompat.wrap(imageDrawable);
//                    DrawableCompat.setTint(imageDrawable,Color.parseColor("#fdeae6"));
//                    lightimage.setImageDrawable(imageDrawable);
//                } else {
//                    Drawable imageDrawable = lightimage.getDrawable();
//                    imageDrawable = DrawableCompat.wrap(imageDrawable);
//                    DrawableCompat.setTint(imageDrawable,Color.BLACK);
//                    lightimage.setImageDrawable(imageDrawable);
//                }
//            }
//        };


        lightlistenertext = new SensorEventListener(){
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                lightreading = Math.round(((event.values[0])/40000)*100);
                TempText.setText(String.format("%s %%",lightreading));
            }
        };

        templistener = new SensorEventListener(){
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                tempreading = Math.round(event.values[0]);
                TempText.setText(String.format("%s °C",tempreading));
            }
        };

        humidlistener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                HumidText.setText(String.format("%s%%",Math.round(sensorEvent.values[0])));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        acceleratelistener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if((sensorEvent.values[0] != 0)){
                    Drawable imageDrawable = motionimage.getDrawable();
                    imageDrawable = DrawableCompat.wrap(imageDrawable);
                    DrawableCompat.setTint(imageDrawable,Color.parseColor("#ebf2e2"));
                    motionimage.setImageDrawable(imageDrawable);
                } else {
                    Drawable imageDrawable = motionimage.getDrawable();
                    imageDrawable = DrawableCompat.wrap(imageDrawable);
                    DrawableCompat.setTint(imageDrawable,Color.BLACK);
                    motionimage.setImageDrawable(imageDrawable);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        floatingActionButton.setOnClickListener(view -> Sensors.this.finish());

        Query query = db.collection("Users")
                .whereEqualTo("email", user.getEmail());
        lightlisten = query.addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w("TAG", "Listen failed.", e);
                return;
            }
            assert value != null;
            for (QueryDocumentSnapshot doc : value) {
                User userhome = doc.toObject(User.class);
                Map<String, Object> home = userhome.getHome();
                Map<String, Object> devices = (Map<String, Object>) home.get("devices");
                assert devices != null;
                for (Map.Entry<String, Object> entry : devices.entrySet()) {
                    if (entry.getKey().equals("Lights")) {
                        collect = (Map<String, Object>) entry.getValue();
                        for (Map.Entry<String, Object> entry2 : collect.entrySet()) {
                                Map<String, Object> data = (Map<String, Object>) entry2.getValue();
                                for (Map.Entry<String, Object> entry3 : data.entrySet()) {
                                    if(entry3.getKey().equals("room")){
                                        if (entry3.getValue().toString().equals(room)){
                                            for (Map.Entry<String, Object> finals : data.entrySet()){
                                                if (finals.getKey().equals("sensor")) {
                                                    if(Double.parseDouble(finals.getValue().toString()) != 0){
                                                        Drawable imageDrawable = lightimage.getDrawable();
                                                        imageDrawable = DrawableCompat.wrap(imageDrawable);
                                                        DrawableCompat.setTint(imageDrawable,Color.parseColor("#fdeae6"));
                                                        lightimage.setImageDrawable(imageDrawable);
                                                    } else {
                                                        Drawable imageDrawable = lightimage.getDrawable();
                                                        imageDrawable = DrawableCompat.wrap(imageDrawable);
                                                        DrawableCompat.setTint(imageDrawable,Color.BLACK);
                                                        lightimage.setImageDrawable(imageDrawable);
                                                    }
                                                }
                                            }
                                        } else {

                                            // FIXME: It only works with the first element in the map

                                            Drawable imageDrawable = lightimage.getDrawable();
                                            imageDrawable = DrawableCompat.wrap(imageDrawable);
                                            DrawableCompat.setTint(imageDrawable,Color.BLACK);
                                            lightimage.setImageDrawable(imageDrawable);
                                        }
                                    }
                                }
                        }
                    }
                }
            }
        });

    }

    boolean isConnected(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return mWifi.isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sMgr.registerListener(humidlistener,humid,SensorManager.SENSOR_DELAY_NORMAL);
//        sMgr.registerListener(lightlistenerimage,light,SensorManager.SENSOR_DELAY_NORMAL);
        sMgr.registerListener(acceleratelistener,accelerate,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        lightlisten.remove();
        sMgr.unregisterListener(templistener);
        sMgr.unregisterListener(humidlistener);
//        sMgr.unregisterListener(lightlistenerimage);
        sMgr.unregisterListener(lightlistenertext);
        sMgr.unregisterListener(acceleratelistener);
    }
}