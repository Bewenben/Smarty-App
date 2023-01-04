package com.example.iotapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class HomeMembers extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ListView listView;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ArrayList<String> id = new ArrayList<>();
    ArrayList<String> rolename = new ArrayList<>();
    ArrayList<String> username = new ArrayList<>();
    ArrayList<String> profilepic = new ArrayList<>();
    MembersAdapter adapter;
    FirebaseStorage storage;
    StorageReference storageReference;
    LinearLayout loading;
    String name;
    ArrayList<String> emails = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        listView = findViewById(R.id.listview);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        loading = findViewById(R.id.progress);
        adapter = new MembersAdapter(username,rolename,profilepic, HomeMembers.this);
        db.collection("Users").whereEqualTo("email",user.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                                User userhome = document.toObject(User.class);
                                Map<String, Object> home = userhome.getHome();
                                ArrayList<Object> roles = (ArrayList<Object>) home.get("roles");
                                for (int i = 0; Objects.requireNonNull(roles).size() > i ; i ++){
                                    Map<String,Object> role = (Map<String, Object>) roles.get(i);
                                    for (Map.Entry<String,Object> entry : role.entrySet()) {
                                        if (entry.getKey().equals("id")) {
                                            id.add(entry.getValue().toString());
                                        }
                                        if (entry.getKey().equals("role")){
                                            rolename.add(entry.getValue().toString());
                                        }
                                    }
                                }
                        }
                        getMembers();
                    } else {
                        Log.w("TAG", "Error getting documents.", task.getException());
                    }
                });

    }

    // FIXME: Every name, role and profile pic needs to be synced.

    void getMembers() {
        for (int i = 0; id.size() > i ; i++){
            db.collection("Users").document(id.get(i))
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            name = document.getString("first") + " " + document.getString("last");
                            emails.add(document.getString("email"));
                            username.add(name);
                            getProfileMembers();
                        }
                    });
        }
    }

    void getProfileMembers(){
        for (int i = 0 ; i < emails.size(); i++){
            Log.d("A7A",emails.get(i));
            try {
                StorageReference gsReference = storage.getReferenceFromUrl("gs://iot-app-faaab.appspot.com/users/" + emails.get(i));
                File localFile= File.createTempFile("images", "jpg");
                gsReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                    profilepic.add(localFile.toString());
                    loading.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    listView.setAdapter(adapter);
                });
            } catch (Exception ignored){
                profilepic.add("none");
                loading.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                listView.setAdapter(adapter);
            }
        }
    }
}