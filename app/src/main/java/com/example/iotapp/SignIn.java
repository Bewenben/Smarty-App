package com.example.iotapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.github.penfeizhou.animation.apng.APNGDrawable;
import com.github.penfeizhou.animation.loader.AssetStreamLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SignIn extends AppCompatActivity {

    ImageView imageView;
    FirebaseAuth auth;
    EditText passwordtv;
    EditText emailtv;
    TextView forgotpasstv;
    LinearLayout loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        auth = FirebaseAuth.getInstance();

//        final LayoutInflater factory = getLayoutInflater();

//        final View textEntryView = factory.inflate(R.layout.activity_forgot_pass, null);

        imageView = findViewById(R.id.imageView2);
        TextView signup = findViewById(R.id.textView22);
        TextView signin = findViewById(R.id.textView11);
        emailtv = findViewById(R.id.email);
        passwordtv = findViewById(R.id.password);
        forgotpasstv = findViewById(R.id.textView23);
        loading = findViewById(R.id.progress);

        signin.setOnClickListener(v -> {
            if(!emailtv.getText().toString().isEmpty()){
                if(!passwordtv.getText().toString().isEmpty()){
                    loading.setVisibility(View.VISIBLE);
                    emailtv.clearFocus();
                    passwordtv.clearFocus();
                    signin(emailtv.getText().toString(),passwordtv.getText().toString());
                } else{
                    passwordtv.setError("Please enter your password");
                    passwordtv.requestFocus();
                }
            } else {
                emailtv.setError("Please enter your email");
                emailtv.requestFocus();
            }
        });

        signup.setOnClickListener(v -> {
            Intent i = new Intent(SignIn.this,SignUp.class);
            startActivity(i);
        });

        forgotpasstv.setOnClickListener(v -> {
            Intent i = new Intent(SignIn.this,ForgotPass.class);
            startActivity(i);
        });

        AssetStreamLoader assetLoader = new AssetStreamLoader(SignIn.this, "speaker.apng");
        APNGDrawable apngDrawable = new APNGDrawable(assetLoader);
        imageView.setImageDrawable(apngDrawable);

        apngDrawable.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
            @Override
            public void onAnimationStart(Drawable drawable) {
                super.onAnimationStart(drawable);
            }
        });
    }

    private void signin(String email, String password){
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    updateUI();
                }
            else {
                    loading.setVisibility(View.GONE);
                    String errorCode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();
                    switch (errorCode) {

                        case "ERROR_INVALID_CUSTOM_TOKEN":
                            Toast.makeText(SignIn.this, "The custom token format is incorrect. Please check the documentation.", Toast.LENGTH_LONG).show();
                            break;

                        case "ERROR_CUSTOM_TOKEN_MISMATCH":
                            Toast.makeText(SignIn.this, "The custom token corresponds to a different audience.", Toast.LENGTH_LONG).show();
                            break;

                        case "ERROR_INVALID_CREDENTIAL":
                            Toast.makeText(SignIn.this, "The supplied auth credential is malformed or has expired.", Toast.LENGTH_LONG).show();
                            break;

                        case "ERROR_INVALID_EMAIL":
                            Toast.makeText(SignIn.this, "The email address is badly formatted.", Toast.LENGTH_LONG).show();
                            emailtv.setError("The email address is badly formatted.");
                            emailtv.requestFocus();
                            break;

                        case "ERROR_WRONG_PASSWORD":
                            Toast.makeText(SignIn.this, "The password is invalid or the user does not have a password.", Toast.LENGTH_LONG).show();
                            passwordtv.requestFocus();
                            passwordtv.setText("");
                            break;

                        case "ERROR_USER_MISMATCH":
                            Toast.makeText(SignIn.this, "The supplied credentials do not correspond to the previously signed in user.", Toast.LENGTH_LONG).show();
                            break;

                        case "ERROR_REQUIRES_RECENT_LOGIN":
                            Toast.makeText(SignIn.this, "This operation is sensitive and requires recent authentication. Log in again before retrying this request.", Toast.LENGTH_LONG).show();
                            break;

                        case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                            Toast.makeText(SignIn.this, "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.", Toast.LENGTH_LONG).show();
                            break;

                        case "ERROR_EMAIL_ALREADY_IN_USE":
                            Toast.makeText(SignIn.this, "The email address is already in use by another account.   ", Toast.LENGTH_LONG).show();
                            emailtv.setError("The email address is already in use by another account.");
                            emailtv.requestFocus();
                            break;

                        case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                            Toast.makeText(SignIn.this, "This credential is already associated with a different user account.", Toast.LENGTH_LONG).show();
                            break;

                        case "ERROR_USER_DISABLED":
                            Toast.makeText(SignIn.this, "The user account has been disabled by an administrator.", Toast.LENGTH_LONG).show();
                            break;

                        case "ERROR_USER_TOKEN_EXPIRED":

                        case "ERROR_INVALID_USER_TOKEN":
                            Toast.makeText(SignIn.this, "The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                            break;

                        case "ERROR_USER_NOT_FOUND":
                            Toast.makeText(SignIn.this, "There is no user record corresponding to this identifier. The user may have been deleted.", Toast.LENGTH_LONG).show();
                            break;

                        case "ERROR_OPERATION_NOT_ALLOWED":
                            Toast.makeText(SignIn.this, "This operation is not allowed. You must enable this service in the console.", Toast.LENGTH_LONG).show();
                            break;

                        case "ERROR_WEAK_PASSWORD":
                            Toast.makeText(SignIn.this, "The given password is invalid.", Toast.LENGTH_LONG).show();
                            passwordtv.setError("The password is invalid it must 6 characters at least");
                            passwordtv.requestFocus();
                            break;
                    }
                }
            });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            Intent i = new Intent(SignIn.this, Main.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
    }

    private void updateUI(){
            Intent i = new Intent(SignIn.this, Main.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

}