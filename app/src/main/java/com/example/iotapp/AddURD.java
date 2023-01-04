package com.example.iotapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddURD extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText textView;
    CardView Roomname;
    CardView HintName;
    ConstraintLayout SpinnerLayout;
    Spinner spinner;
    Button button;
    String[] names = {"Lights","CCTV","Wi-Fi","Media","Thermostat"};
    String[] roles = {"Administrator","Parent","Child"};
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Map<String, Object> device;
    String id;
    String collection;
    String s;
    String s2;
    String memberid;
    Map<String, Object> home;
    Map<String, Object> devices;
    ArrayList<Object> rolenames;
    ArrayList<Object> roomnames;
    User userhome;
    TextView add;
    EditText room;
    boolean found = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);
        add = findViewById(R.id.textView12);
        room = findViewById(R.id.editTextTextPersonName2);
        HintName = findViewById(R.id.Hintname);
        textView = findViewById(R.id.editTextTextPersonName);
        Roomname = findViewById(R.id.Room_name);
        button = findViewById(R.id.button);
        spinner = findViewById(R.id.spinner2);
        SpinnerLayout = findViewById(R.id.SpinnerLayout);
        Intent i = getIntent();
        String hint = i.getStringExtra("hint").substring(4);
        add.setText("Add " + hint);
        button.setText("Add " + hint);
        if (hint.equals("Device")){
            Roomname.setVisibility(View.VISIBLE);
            textView.setHint(hint + "'s name");
            ArrayAdapter<String> namesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
            namesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(namesAdapter);
        }
        else if (hint.equals("User")){
            textView.setHint(hint + "'s email");
            ArrayAdapter<String> rolesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
            rolesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(rolesAdapter);
        }
        else{
            HintName.setVisibility(View.GONE);
            Roomname.setVisibility(View.VISIBLE);
            SpinnerLayout.setVisibility(View.GONE);
            textView.setHint(hint + "'s name");
        }
        button.setOnClickListener(view -> {
            s = textView.getText().toString();
            s2 = room.getText().toString();
            if(hint.equals("Device")) {
                collection = spinner.getSelectedItem().toString();
                db.collection("Users").whereEqualTo("email", user.getEmail()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean roomfound = false;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userhome = document.toObject(User.class);
                            id = document.getId();
                            home = userhome.getHome();
                            ArrayList<Object> roomcheck = (ArrayList<Object>) home.get("rooms");
                            for (int i1 = 0; Objects.requireNonNull(roomcheck).size() > i1; i1++){
                                Map<String,Object> room = (Map<String, Object>) roomcheck.get(i1);
                                for (Map.Entry<String,Object> entry : room.entrySet()) {
                                    if (entry.getKey().equals("name")) {
                                        String roomname = entry.getValue().toString();
                                        if(roomname.equals(s2)){
                                            roomfound = true;
                                        }
                                    }
                                }
                            }
                            devices = (Map<String, Object>) home.get("devices");
                            assert devices != null;
                            for (Map.Entry<String, Object> entry : devices.entrySet()) {
                                if (entry.getKey().equals(collection)) {
                                    device = (Map<String, Object>) entry.getValue();
//                                            ("device",String.valueOf(device));
                                }
                            }
                        }
                        if(roomfound){
                            addDevice();
                        } else {
                            room.setError("Room Not Found");
                            room.requestFocus();
                        }
                    }
                });

            } else if (hint.equals("User")){
                collection = spinner.getSelectedItem().toString();
                db.collection("Users").whereEqualTo("email", user.getEmail()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userhome = document.toObject(User.class);
                            id = document.getId();
                            home = userhome.getHome();
                            rolenames = (ArrayList<Object>) home.get("roles");
                        }
                        CheckUserAvailable();
                    }
                });
            }
            else{
                db.collection("Users").whereEqualTo("email", user.getEmail()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userhome = document.toObject(User.class);
                            id = document.getId();
                            home = userhome.getHome();
                            roomnames = (ArrayList<Object>) home.get("rooms");
                        }
                        addRoom();
                    }
                });
            }
        });

    }

    private void addDevice(){
        s = s.replace(" ","_");
        Map<String, Object> userhome = new HashMap<>();
        Map<String, Object> devicedata = new HashMap<>();
        devicedata.put("condition", true);
        devicedata.put("room",s2);
        devicedata.put("sensor",0);
        device.put(s,devicedata);
        devices.put(collection, device);
        home.put("devices", devices);
        userhome.put("home", home);
        sendData(userhome);
    }

    private void CheckUserAvailable(){
        found = false;
        db.collection("Users").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if(Objects.equals(document.getString("email"), s)){
                        found = true;
                        memberid = document.getId();
                        addMember();
                        break;
                    }
                }
                if(!found){
                    textView.setError("Email does not exist");
                    textView.requestFocus();
                }
            }
        });
    }

    private void addMember(){
        Map<String, Object> userhome = new HashMap<>();
        Map<String, Object> user = new HashMap<>();
        user.put("id", memberid);
        user.put("role",collection);
        rolenames.add(user);
        home.put("roles", rolenames);
        userhome.put("home",home);
        sendData(userhome);
    }

    private void addRoom(){
        Map<String, Object> userhome = new HashMap<>();
        Map<String, Object> roomdata = new HashMap<>();
        roomdata.put("name",s2);
        roomnames.add(roomdata);
        home.put("rooms",roomnames);
        userhome.put("home",home);
        sendData(userhome);
    }

    private void sendData(Map<String, Object> home){
        db.collection("Users").document(id)
                .update(home)
                .addOnSuccessListener(aVoid -> Toast.makeText(AddURD.this, "Added successfully to your home.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));
    }
}