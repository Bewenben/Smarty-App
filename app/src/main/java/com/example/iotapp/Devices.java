package com.example.iotapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Map;

public class Devices extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ListView listView;
    TextView title;
    TextView notavail;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Map<String, Object> home;
    Map<String,Object> devices;
    Map<String,Object> collect;
    String id;
    LinearLayout loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        listView = findViewById(R.id.listview2);
        title = findViewById(R.id.textView19);
        loading = findViewById(R.id.progress);
        notavail = findViewById(R.id.textView27);
        Intent i = getIntent();
        String collection = i.getStringExtra("collection");
        title.setText(collection);
        ArrayList<String> devices_names = new ArrayList<>();
        ArrayList<Boolean> conditions = new ArrayList<>();
        ArrayList<String> roomname = new ArrayList<>();
        db.collection("Users").whereEqualTo("email",user.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            id = document.getId();
                            User userhome = document.toObject(User.class);
                            home = userhome.getHome();
                            devices = (Map<String, Object>) home.get("devices");
                            assert devices != null;
                            for (Map.Entry<String,Object> entry : devices.entrySet()) {
                                if (entry.getKey().equals(collection)) {
                                    collect = (Map<String, Object>) entry.getValue();
                                    for(Map.Entry<String,Object> entry2 : collect.entrySet()){
                                        String redefined_name = entry2.getKey().replace("_", " ");
                                        devices_names.add(redefined_name);
                                        Map<String, Object> data = (Map<String, Object>) entry2.getValue();
                                        for(Map.Entry<String,Object> entry3 : data.entrySet()){
                                            if (entry3.getKey().equals("condition")) {
                                                conditions.add((Boolean) entry3.getValue());
                                            }
                                            if(entry3.getKey().equals("room")){
                                                roomname.add((entry3.getValue().toString()));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        loading.setVisibility(View.GONE);
                        if(devices_names.size() != 0){
                            listView.setVisibility(View.VISIBLE);
                            DeviceListAdapter adapter = new DeviceListAdapter(devices_names,conditions,collection,id,roomname,home,devices,collect, Devices.this);
                            listView.setAdapter(adapter);
                        } else {
                            notavail.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.w("TAG", "Error getting documents.", task.getException());
                    }
                });

    }
}