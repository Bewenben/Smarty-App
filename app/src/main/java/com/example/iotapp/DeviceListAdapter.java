package com.example.iotapp;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DeviceListAdapter extends BaseAdapter{

    private final ArrayList<String> devicename;
    private final ArrayList<Boolean> condition;
    private final LayoutInflater layoutInflater;
    private final String collection;
    private final String id;
    private final Map<String,Object> home;
    private final Map<String, Object> devices;
    private final ArrayList<String> roomname;
    private final Map<String, Object> device;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public DeviceListAdapter(ArrayList<String> devicename,ArrayList<Boolean> condition,String collection,String id,ArrayList<String> roomname,Map<String,Object> home,Map<String, Object> devices,Map<String, Object> device,Context context) {
        this.devicename = devicename;
        this.condition = condition;
        this.collection = collection;
        this.id = id;
        this.roomname = roomname;
        this.home = home;
        this.devices = devices;
        this.device = device;
        this.layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return devicename.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            view = layoutInflater.inflate(R.layout.deviceslayout, viewGroup,false);
        }
        TextView textView = view.findViewById(R.id.textView);
        Switch switch1 = view.findViewById(R.id.switch2);
        textView.setText(devicename.get(i));
        switch1.setChecked(condition.get(i));

        if (switch1.isChecked()){
            textView.setTextColor(Color.parseColor("#0059FF"));
        }
        else{
            textView.setTextColor(Color.parseColor("#000000"));
        }

        switch1.setOnCheckedChangeListener((compoundButton, b) -> {

            addDevice(devicename.get(i),b,roomname.get(i), i);

            if (switch1.isChecked()){
                textView.setTextColor(Color.parseColor("#0059FF"));
            }
            else{
                textView.setTextColor(Color.parseColor("#000000"));
            }
        });

        return view;
    }

    private void addDevice(String s, Boolean bool, String s2, int i){
        Map<String, Object> userhome = new HashMap<>();
        Map<String, Object> devicedata = new HashMap<>();
        devicedata.put("name", s);
        devicedata.put("condition", bool);
        devicedata.put("room",s2);
        device.remove(i);
//        device.add(i,devicedata);
        devices.put(collection, device);
        home.put("devices", devices);
        userhome.put("home", home);

        db.collection("Users").document(id)
                .update(userhome).addOnCompleteListener(task -> {

                })
                .addOnFailureListener(e -> Log.w("TAG", "Error adding document", e));
    }

}
