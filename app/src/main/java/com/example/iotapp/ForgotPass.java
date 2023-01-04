package com.example.iotapp;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPass extends AppCompatActivity {

    EditText emailtv;
    TextView resetbtn;
    FirebaseAuth auth;
    LinearLayout loading;
    CountDownTimer cTimer = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        auth = FirebaseAuth.getInstance();
        emailtv = findViewById(R.id.emailtvforgot);
        resetbtn = findViewById(R.id.textView11);
        loading = findViewById(R.id.progress);

        resetbtn.setOnClickListener(v -> {
            loading.setVisibility(View.VISIBLE);
            if(!emailtv.getText().toString().isEmpty()){
                auth.sendPasswordResetEmail(emailtv.getText().toString()).addOnCompleteListener(task -> {
                    loading.setVisibility(View.GONE);
                    if(task.isSuccessful()){
                        Toast.makeText(ForgotPass.this,"Password Reset Email sent to your email",Toast.LENGTH_SHORT).show();
                        Resend();
                    } else {
//                                Toast.makeText(ForgotPass.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                loading.setVisibility(View.GONE);
                emailtv.requestFocus();
                emailtv.setError("Please enter your email.");
            }
        });
    }

    void Resend(){

        // FIXME: A background timer needs to be added every time an email is sent from the device to prevent the device from being blocked by Firebase.

        resetbtn.setClickable(false);
        cTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                resetbtn.setText("Resend in " + millisUntilFinished / 1000);
            }
            public void onFinish() {
                resetbtn.setText("Resend");
                resetbtn.setClickable(true);
            }
        };
        cTimer.start();
    }

}