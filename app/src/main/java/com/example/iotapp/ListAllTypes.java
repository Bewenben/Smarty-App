package com.example.iotapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class ListAllTypes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String[] names = {"Lights","CCTV","Wi-Fi","Media","Thermostat"};
        ListView listview;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        listview = findViewById(R.id.listview);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,android.R.id.text1,names);
        listview.setAdapter(arrayAdapter);

        listview.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent x = new Intent(ListAllTypes.this, Devices.class);
            x.putExtra("collection",names[i]);
            startActivity(x);
        });

    }
}