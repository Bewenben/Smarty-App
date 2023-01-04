package com.example.iotapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUp extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText fname;
    EditText lname;
    EditText emailtv;
    EditText passwordtv;
    EditText confirmpass;
    String id;
    LinearLayout loading;
//    ActionCodeSettings actionCodeSettings;
//    FirebaseUser emailsignup = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        loading = findViewById(R.id.progress);
//        String url = "http://www.example.com/verify?uid=" + emailsignup.getUid();
//        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
//                .setUrl(url)
//                .setIOSBundleId("com.example.ios")
//                // The default for this is populated with the current android package name.
//                .setAndroidPackageName("com.example.android", false, null)
//                .build();

        TextView signin = findViewById(R.id.textView22);
        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        emailtv = findViewById(R.id.email);
        passwordtv = findViewById(R.id.password);
        confirmpass = findViewById(R.id.confirmpass);
        TextView signup = findViewById(R.id.textView11);

        signup.setOnClickListener(v -> {
            loading.setVisibility(View.VISIBLE);
            if(!fname.getText().toString().isEmpty()){
                if (!lname.getText().toString().isEmpty()){
                    String afname = fname.getText().toString().substring(0,1).toUpperCase() + fname.getText().toString().substring(1);
                    String alname = lname.getText().toString().substring(0,1).toUpperCase() + lname.getText().toString().substring(1);
                    if (!emailtv.getText().toString().isEmpty()){
                        if (!passwordtv.getText().toString().isEmpty() && (Integer.parseInt(passwordtv.getText().toString()) > 6)){
                            if(confirmpass.getText().toString().equals(passwordtv.getText().toString())){
                                signup(emailtv.getText().toString(),passwordtv.getText().toString(),afname,alname);
                            } else {
                                loading.setVisibility(View.GONE);
                                confirmpass.setError("Password does not match");
                                confirmpass.requestFocus();
                            }
                        } else {
                            loading.setVisibility(View.GONE);
                            passwordtv.setError("Please enter your password");
                            passwordtv.requestFocus();
                        }
                    } else {
                        loading.setVisibility(View.GONE);
                        emailtv.setError("Please enter your email");
                        emailtv.requestFocus();
                    }
                } else {
                    loading.setVisibility(View.GONE);
                    lname.setError("Please enter your last name");
                    lname.requestFocus();
                }
            } else {
                loading.setVisibility(View.GONE);
                fname.setError("Please enter your first name");
                fname.requestFocus();
            }
        });


        signin.setOnClickListener(v -> {
            Intent i = new Intent(SignUp.this,SignIn.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });

    }

    private void signup(String email, String password, String firstname, String lastname){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){

                FirebaseUser useremail = auth.getCurrentUser();

                Map<String, Object> user = new HashMap<>();
                assert useremail != null;
                user.put("email",useremail.getEmail());
                user.put("first",firstname);
                user.put("last",lastname);

                db.collection("Users")
                        .add(user)
                        .addOnSuccessListener(documentReference -> {
                            id = documentReference.getId();
//                                    emailsignup.sendEmailVerification(actionCodeSettings)
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if (task.isSuccessful()) {
//                                                        Log.d("TAG", "Email sent.");
//                                                    }
//                                                }
//                                            });
                            GiveRole(id,firstname,lastname);
                        })
                        .addOnFailureListener(e -> Log.w("TAG", "Error adding document", e));

            } else {
                loading.setVisibility(View.GONE);
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch(FirebaseAuthWeakPasswordException e) {
                    passwordtv.setError("Weak Password, please enter a strong password");
                    passwordtv.requestFocus();
                } catch(FirebaseAuthInvalidCredentialsException e) {
                    emailtv.setError("Please enter a valid email");
                    emailtv.requestFocus();
                } catch(FirebaseAuthUserCollisionException e) {
                    emailtv.setError("User already exists.");
                    emailtv.requestFocus();
                } catch(Exception e) {
                    Log.e("TAG", e.getMessage());
                }
            }
        });
    }

    private void GiveRole(String id, String firstname,String lastname){

        FirebaseUser useremail = auth.getCurrentUser();

        Map<String, Object> user = new HashMap<>();
        assert useremail != null;
        user.put("email",useremail.getEmail());
        user.put("first",firstname);
        user.put("last",lastname);

        Map<String, Object> home = new HashMap<>();
        home.put("name","My Home");

        Map<String,Object> Devices = new HashMap<>();

        ArrayList<Object> Rooms = new ArrayList<>();

        Map<String,Object> CCTVData = new HashMap<>();

        Map<String,Object> LightsData = new HashMap<>();

        Map<String,Object> MediaData = new HashMap<>();

        Map<String,Object> ThermostatData = new HashMap<>();

        Map<String,Object> WiFiData = new HashMap<>();

        Devices.put("CCTV",CCTVData);
        Devices.put("Lights",LightsData);
        Devices.put("Media",MediaData);
        Devices.put("Thermostat",ThermostatData);
        Devices.put("Wi-Fi",WiFiData);

        ArrayList<Object> roles = new ArrayList<>();
        Map<String, Object> userrole = new HashMap<>();
        userrole.put("id",id);
        userrole.put("role","Administrator");
        roles.add(userrole);

        home.put("rooms",Rooms);
        home.put("roles",roles);
        home.put("devices",Devices);
        user.put("home",home);

        db.collection("Users").document(id)
                .update(user)
                .addOnSuccessListener(aVoid -> updateUI())
                .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));
    }

    private void updateUI(){
        Intent i = new Intent(SignUp.this, Main.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}