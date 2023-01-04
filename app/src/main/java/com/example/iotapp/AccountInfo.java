package com.example.iotapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountInfo extends AppCompatActivity {

    EditText useremail;
    EditText userfirst;
    EditText userlast;
    EditText newpass;
    Button changeemail;
    Button changefirst;
    Button changelast;
    Button changepass;
    LinearLayout loading;
    FirebaseUser user;
    FirebaseFirestore db;
    FirebaseAuth auth;
    String id;
    Pattern p = Pattern.compile("[^a-z]", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        loading = findViewById(R.id.progress);
        useremail = findViewById(R.id.editTextTextPersonName4);
        userfirst = findViewById(R.id.editTextTextPersonName5);
        userlast = findViewById(R.id.editTextTextPersonName6);
        newpass = findViewById(R.id.editTextTextPersonName8);
        changeemail = findViewById(R.id.button4);
        changefirst = findViewById(R.id.button5);
        changelast = findViewById(R.id.button6);
        changepass = findViewById(R.id.button7);

        db.collection("Users").whereEqualTo("email",user.getEmail())
                        .get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()){
                                    id = document.getId();
                                    userfirst.setHint(document.getString("first"));
                                    userlast.setHint(document.getString("last"));
                                    useremail.setHint(user.getEmail());
                                }
                            }
                        }).addOnFailureListener(e -> {

                        });

        // TODO: Change Email.

        changefirst.setOnClickListener(v -> {
            Matcher m = p.matcher(userfirst.getText().toString());
            boolean bool = m.find();
            if(!bool){
                String first = userfirst.getText().toString().substring(0,1).toUpperCase() + userfirst.getText().toString().substring(1);
                db.collection("Users").document(id).update("first",first)
                        .addOnCompleteListener(task -> {
                            Toast.makeText(AccountInfo.this,"Edited Successfully", Toast.LENGTH_SHORT).show();
                            userfirst.setHint(first);
                            userfirst.setText("");
                        });
//                    Toast.makeText(AccountInfo.this,"Right Name", Toast.LENGTH_SHORT).show();
            } else {
                userfirst.setError("Your first name cannot have any special characters or numbers.");
                userfirst.requestFocus();
            }
        });

        changelast.setOnClickListener(v -> {
            loading.setVisibility(View.VISIBLE);
            Matcher m = p.matcher(userlast.getText().toString());
            boolean bool = m.find();
            if(!bool){
                String last = userlast.getText().toString().substring(0,1).toUpperCase() + userlast.getText().toString().substring(1);
                db.collection("Users").document(id).update("last",last)
                        .addOnCompleteListener(task -> {
                            Toast.makeText(AccountInfo.this,"Edited Successfully", Toast.LENGTH_SHORT).show();
                            userlast.setHint(last);
                            userlast.setText("");
                            loading.setVisibility(View.GONE);
                        });
//                    Toast.makeText(AccountInfo.this,"Right Name", Toast.LENGTH_SHORT).show();
            } else {
                userlast.setError("Your last name cannot have any special characters or numbers.");
                userlast.requestFocus();
            }
        });

        // FIXME: Re-authentication doesn't work properly somehow

        changepass.setOnClickListener(v -> {
            loading.setVisibility(View.VISIBLE);
            AuthCredential credential = EmailAuthProvider
                    .getCredential(Objects.requireNonNull(user.getEmail()), newpass.getText().toString());
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {

                    });
        });

    }
}