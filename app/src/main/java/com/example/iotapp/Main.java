package com.example.iotapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class Main extends AppCompatActivity {

    Button home;
    Button profile;
    Boolean homebool = false;
    Boolean profilebool= true;
    static FirebaseStorage storage;
    FirebaseAuth auth;
    static File localFile;
    StorageReference storageReference;
    FirebaseUser user;
    FirebaseFirestore db;
    static String email;
    Homepage mainfragment = new Homepage();
    Bundle homebundle = new Bundle();
    static Boolean profilefound = false;
    FragmentManager manager = getSupportFragmentManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user != null) {
            email = String.valueOf(user.getEmail());
        }

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragmentContainerView3, mainfragment);
        transaction.commit();

        setProfile();

        if (ContextCompat.checkSelfPermission(Main.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Main.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

        home = findViewById(R.id.button2);
        profile = findViewById(R.id.button3);

        profile.setOnClickListener(v -> {
            if(profilebool){
                profilebool = false;
                homebool = true;
                FragmentTransaction transaction1 = manager.beginTransaction();
                Profilepage profilefragment = new Profilepage();
                Bundle b = new Bundle();
                if(profilefound){
                    b.putString("profilepic",localFile.toString());
                    b.putBoolean("bool",profilefound);
                }
                profilefragment.setArguments(b);
                transaction1.replace(R.id.fragmentContainerView3, profilefragment);
                profile.setBackgroundResource(R.color.lightblue);
                home.setBackgroundResource(R.color.transparent);
                home.setTextColor(getResources().getColor(R.color.black));
                profile.setTextColor(getResources().getColor(R.color.white));
                transaction1.commit();
            }
        });

        home.setOnClickListener(v -> {
            if(homebool){
                homebool = false;
                profilebool = true;
                FragmentTransaction transaction12 = manager.beginTransaction();
                Homepage mainfragment = new Homepage();
                mainfragment.setArguments(homebundle);
                transaction12.replace(R.id.fragmentContainerView3, mainfragment);
                home.setBackgroundResource(R.color.lightblue);
                profile.setBackgroundResource(R.color.transparent);
                profile.setTextColor(getResources().getColor(R.color.black));
                home.setTextColor(getResources().getColor(R.color.white));
                transaction12.commit();
            }
        });

    }

    static void setProfile(){
        try {
            StorageReference gsReference = storage.getReferenceFromUrl("gs://iot-app-faaab.appspot.com/users/" + email);
            localFile= File.createTempFile("images", "jpg");
            gsReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> profilefound = true);
        } catch (Exception ignored){

        }
    }
}