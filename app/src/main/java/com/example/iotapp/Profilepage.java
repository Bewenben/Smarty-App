package com.example.iotapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Objects;


public class Profilepage extends Fragment {

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    String[] setting = {"Manage your account","Edit home members' roles","Delete a device","Delete a room","Log out"};
    String[] icons = {"user.apng","group.apng","bin.apng","bin.apng","logout.apng"};
    String[] image = {"Upload a photo","Take a photo"};
    ImageView imageView;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseAuth auth;
    FirebaseUser user;
    String firstname;
    String lastname;
    FirebaseFirestore db;
    private Uri imageuri;
//
//    // TODO: Rename and change types of parameters
    private String encodedBitmap;
    private Boolean bool;

    public Profilepage() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
//    public static Profilepage newInstance(String param1, String param2) {
//        Profilepage fragment = new Profilepage();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            encodedBitmap = getArguments().getString("profilepic");
            bool = getArguments().getBoolean("bool");
//            name = getArguments().getString("name");
        }
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profilepage, container, false);
        imageView = view.findViewById(R.id.profilemember);
        if(bool){
            try{
                Uri myimage = Uri.parse(encodedBitmap);
                imageView.setPadding(0,0,0,0);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageURI(myimage);
            } catch (Exception ignored){

            }
        }
        TextView textView = view.findViewById(R.id.textView24);

        db.collection("Users")
                .whereEqualTo("email", user.getEmail())
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w("TAG", "Listen failed.", e);
                        return;
                    }

                    assert value != null;
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.get("first") != null) {
                            firstname = Objects.requireNonNull(doc.get("first")).toString();
                            lastname = Objects.requireNonNull(doc.get("last")).toString();
                            textView.setText(doc.get("first").toString() + " " + doc.get("last").toString());
                        }
                    }
                });

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        imageView.setOnClickListener(vieww -> new AlertDialog.Builder(getContext())
                .setTitle("Add new photo").setItems(image, (dialogInterface, i) -> {
                    String selected = image[i];
                    if (selected.equals("Upload a photo")) {
                        Intent Gallery = new Intent(Intent.ACTION_GET_CONTENT);
                        Gallery.setType("image/*");
                        someActivityResultLauncher.launch(Intent.createChooser(Gallery, "Select Picture"));
                    }
                    else {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        try {
                            if(takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null){
                                CaptureResultLauncher.launch(takePictureIntent);
                            }
                        } catch (ActivityNotFoundException e) {
                            // display error state to the user
                        }
                    }
                })
                .show());

        ListView listView = view.findViewById(R.id.listview);
        if(isAdded()){
            SettingsAdapter adapter = new SettingsAdapter(icons,setting, requireContext());
            listView.setAdapter(adapter);
        }
        // Inflate the layout for this fragment
        return view;
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        assert data != null;
                        imageuri = data.getData();
                        imageView.setImageURI(imageuri);
                        imageView.setPadding(0,0,0,0);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(() -> uploadtostorage(imageuri), 500);

                    }
                }
            });

    ActivityResultLauncher<Intent> CaptureResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        assert data != null;
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        photo.compress(Bitmap.CompressFormat.PNG,100, bytes);
                        String path = MediaStore.Images.Media.insertImage(requireContext().getContentResolver(), photo, "photo", null);
                        Uri uriphoto = Uri.parse(path);
                        imageView.setImageURI(uriphoto);
                        imageView.setPadding(0,0,0,0);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(() -> uploadtostorage(uriphoto), 500);

                    }
                }
            });

    private void uploadtostorage(Uri uri){
        if (uri != null) {
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("users/"+ user.getEmail());

            ref.putFile(uri)
                    .addOnSuccessListener(
                            taskSnapshot -> {
                                progressDialog.dismiss();
                                Main.setProfile();
                            })

                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast
                                .makeText(getContext(),
                                        "Failed " + e.getMessage(),
                                        Toast.LENGTH_SHORT)
                                .show();
                    })
                    .addOnProgressListener(
                            taskSnapshot -> {
                                double progress
                                        = (100.0
                                        * taskSnapshot.getBytesTransferred()
                                        / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage(
                                        "Uploaded "
                                                + (int)progress + "%");
                            });
        } else {
            Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
        }
    }
}