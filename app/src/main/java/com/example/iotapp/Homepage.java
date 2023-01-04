package com.example.iotapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class Homepage extends Fragment implements LocationListener {

    TextView tempview;
    TextView locationview;
    TextView tempcondview;
    TextView homeview;
    LocationManager locationManager;
    CardView edithome;
    ImageView imagetempview;
    ImageButton home;
    GridView gridView;
    GridView gridcont;
    FloatingActionButton floatingActionButton;
    String key = "82005d27a116c2880c8f0fcb866998a0";
    int[] images = {R.drawable.sun, R.drawable.cctvcamera, R.drawable.wifi, R.drawable.speaker, R.drawable.thermostat};
    String[] names = {"Lights", "CCTV", "Wi-Fi", "Media", "Thermostat"};
    String[] bgColors = {"#fbf6e2", "#e8e5f6", "#d4ebf9", "#ebf2e2", "#fdebe9"};
    String[] contname = {"Lights", "Media", "CCTV", "Wi-Fi", "Thermostat"};
    int[] contimages = {R.drawable.sun, R.drawable.speaker, R.drawable.cctvcamera, R.drawable.wifi, R.drawable.thermostat};
    String[] create = {"Add Room", "Add User", "Add Device"};
    TextView seeall;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth;
    Spinner spinner;
    Boolean f_degree = false;
    String id;
    Map<String, Object> homedata;
    ArrayList<String> arrayList;
    ListenerRegistration registration;
    private String defaulthome = "Home";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    public Homepage() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment Homepage.
//     */
    // TODO: Rename and change types and number of parameters
//    public static Homepage newInstance(String param1, String param2) {
//        Homepage fragment = new Homepage();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            defaulthome = getArguments().getString("home");
        }
        getUserid();
        getLocation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_homepage, container, false);
        auth = FirebaseAuth.getInstance();
        homeview = v.findViewById(R.id.textView2);
        tempview = v.findViewById(R.id.textView7);
        locationview = v.findViewById(R.id.textView6);
        tempcondview = v.findViewById(R.id.textView8);
        imagetempview = v.findViewById(R.id.imageView5);
        spinner = v.findViewById(R.id.spinner);
        gridView = v.findViewById(R.id.gridview);
        gridcont = v.findViewById(R.id.gridview2);
        seeall = v.findViewById(R.id.textView5);
        home = v.findViewById(R.id.homemembers);
        edithome = v.findViewById(R.id.edithome);
        floatingActionButton = v.findViewById(R.id.floatingActionButton3);
        RoomAdapter roomAdapter = new RoomAdapter(contname, contimages, requireContext());
        DevicesAdapter deviceAdapter = new DevicesAdapter(names, images, requireContext(), bgColors);
        gridView.setAdapter(deviceAdapter);
        gridcont.setAdapter(roomAdapter);
        homeview.setText(defaulthome);

        edithome.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            ViewGroup viewGroup = v.findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.edithomedialog, viewGroup, false);
            TextView homechange = dialogView.findViewById(R.id.editTextTextPersonName3);
            builder.setView(dialogView).setPositiveButton("Apply", (dialog, which) -> {
                if(homechange.getText() != null){
                    Map<String,Object> userhome = new HashMap<>();
                    homedata.put("name",homechange.getText().toString());
                    userhome.put("home",homedata);
                    db.collection("Users").document(id).update(userhome)
                        .addOnSuccessListener(aVoid -> Toast.makeText(getContext(),"Edited Successfully",Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));
                }
            }).setNegativeButton("Cancel", (dialog, which) -> {

            }).show();
        });

        tempview.setOnClickListener(v1 -> {
            String[] tempsplit = tempview.getText().toString().split(" ");
            if(!tempsplit[0].equals("-")) {
                double temp = Double.parseDouble(tempsplit[0]);
                if (!f_degree) {
                    f_degree = true;
                    long i = Math.round(temp * 9 / 5 + 32.0);
                    tempview.setText(String.format("%s °F", i));
                } else {
                    f_degree = false;
                    long i = Math.round((temp - 32.0) * 5 / 9);
                    tempview.setText(String.format("%s °C", i));
                }
            }
        });


        seeall.setOnClickListener(view -> {
            Intent i = new Intent(getContext(), ListAllTypes.class);
            startActivity(i);
        });

        floatingActionButton.setOnClickListener(view -> new AlertDialog.Builder(getContext())
                .setTitle("Add to your home")
                .setItems(create, (dialogInterface, i) -> {
                    String selected = create[i];
                    Intent x = new Intent(getContext(), AddURD.class);
                    x.putExtra("hint", selected);
                    startActivity(x);
                })
                .show());

        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent x = new Intent(getContext(), Devices.class);
            x.putExtra("collection", names[i]);
            startActivity(x);
        });

        home.setOnClickListener(view -> {
            Intent i = new Intent(getContext(), HomeMembers.class);
            startActivity(i);
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String Room = parent.getItemAtPosition(position).toString();
                if (!Room.equals("Select Room")) {
                    Intent i = new Intent(getContext(), Sensors.class);
                    i.putExtra("Room", Room);
                    startActivity(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // ******************************************************
        // START OF DATABASE EVENT LISTENERS
        // ******************************************************


        // ******************************************************

        db.collection("Users")
                .whereEqualTo("email", user.getEmail())
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w("TAG", "Listen failed.", e);
                        return;
                    }

//                        List<String> cities = new ArrayList<>();
                    assert value != null;
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.get("email") != null) {
                            User userhome = doc.toObject(User.class);
                            Map<String, Object> home = userhome.getHome();
                            homeview.setText(Objects.requireNonNull(home.get("name")).toString());
                        }
                    }
                });

        Query query = db.collection("Users")
                .whereEqualTo("email", user.getEmail());
                 registration = query.addSnapshotListener((value, e) -> {
                     if (e != null) {
                         Log.w("TAG", "Listen failed.", e);
                         return;
                     }
//                        List<String> cities = new ArrayList<>();
                     assert value != null;
                     for (QueryDocumentSnapshot doc : value) {
                         arrayList= new ArrayList<>();
                         arrayList.add("Select Room");
                             User userhome = doc.toObject(User.class);
                             Map<String, Object> home = userhome.getHome();
                             ArrayList<Object> rooms = (ArrayList<Object>) home.get("rooms");
                             try{
                                 for (int i = 0; rooms.size() > i ; i ++){
                                     Map<String,Object> room = (Map<String, Object>) rooms.get(i);
                                     for (Map.Entry<String,Object> entry : room.entrySet()) {
                                         if (entry.getKey().equals("name")) {
                                             String roomname = entry.getValue().toString();
                                             arrayList.add(roomname);
                                         }
                                     }
                                 }
                             } catch (Exception ignored){}
                         ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, arrayList);
                         arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                         spinner.setAdapter(arrayAdapter);
                     }
                 });

        // ******************************************************

        // ******************************************************
        // END OF DATABASE EVENT LISTENERS
        // ******************************************************

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

        return v;
    }

    private void getLocation() {
        try {
            locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 5, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        Weather(location.getLatitude(),location.getLongitude());
    }

    private void Weather(double lat, double lon){
        if (isAdded()) {
            try {
                String s = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + key;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, s, response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                        String icon = jsonObjectWeather.getString("icon").substring(0, 2);
                        String night = jsonObjectWeather.getString("icon").substring(2, 3);
                        String description = jsonObjectWeather.getString("description").substring(0, 1).toUpperCase() + jsonObjectWeather.getString("description").substring(1).toLowerCase();
                        JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                        JSONObject jsonObjectsys = jsonResponse.getJSONObject("sys");
                        String country = jsonObjectsys.getString("country");
                        String city = jsonResponse.getString("name");
                        double temp = jsonObjectMain.getDouble("temp") - 273.15;
                        tempview.setText(String.format("%s °C", Math.round(temp)));
                        tempcondview.setText(description);
                        locationview.setText(String.format("%s, %s", city, country));
                        String uri;
                        if (night.equals("n")) {
                            uri = "@drawable/d" + icon + "n";
                        } else {
                            uri = "@drawable/d" + icon + "d";
                        }
                        if (isAdded()) {
                            int imageResource = getResources().getIdentifier(uri, null, requireActivity().getPackageName());
                            Drawable res = getResources().getDrawable(imageResource);
                            imagetempview.setImageDrawable(res);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, error -> Toast.makeText(requireActivity().getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show());
                RequestQueue requestQueue = Volley.newRequestQueue(requireActivity().getApplicationContext());
                requestQueue.add(stringRequest);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getUserid() {

        db.collection("Users").whereEqualTo("email",user.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User userhome = document.toObject(User.class);
                            id = document.getId();
                            homedata = userhome.getHome();
                        }
                    } else {
                        Log.w("TAG", "Error getting documents.", task.getException());
                    }
                });
    }

    public void onDetach() {
        super.onDetach();
        registration.remove();
    }

}