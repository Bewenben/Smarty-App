package com.example.iotapp;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.core.graphics.drawable.DrawableCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

public class RoomAdapter extends BaseAdapter {

    private final String[] imageNames;
    private final int[] imagesPhotos;
    private final LayoutInflater layoutInflater;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public RoomAdapter(String[] imageNames, int[] imagesPhotos, Context context) {
        this.imageNames = imageNames;
        this.imagesPhotos = imagesPhotos;
        this.layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imagesPhotos.length;
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
            view = layoutInflater.inflate(R.layout.container_items, viewGroup,false);
        }
        TextView tvName = view.findViewById(R.id.textView10);
        ImageView imagePhoto = view.findViewById(R.id.imageView6);
        tvName.setText(imageNames[i]);
        imagePhoto.setImageResource(imagesPhotos[i]);
        imagePhoto.setPadding(24,24,24,24);
        Switch lightswitch = view.findViewById(R.id.switch1);

        lightswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Drawable imageDrawable = imagePhoto.getDrawable();
                imageDrawable = DrawableCompat.wrap(imageDrawable);
                DrawableCompat.setTint(imageDrawable, Color.parseColor("#0059FF"));
                imagePhoto.setImageDrawable(imageDrawable);
            } else {
                Drawable imageDrawable = imagePhoto.getDrawable();
                imageDrawable = DrawableCompat.wrap(imageDrawable);
                DrawableCompat.setTint(imageDrawable,Color.BLACK);
                imagePhoto.setImageDrawable(imageDrawable);
            }

            getData(imageNames[i], isChecked);
        });

        return view;
    }

    private void getData(String name, boolean bool){
        db.collection(name).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ArrayList<String> list = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                    list.add(documentSnapshot.getId());
                }
                updateData(list, bool, name);
            }
        });
    }

    private void updateData(ArrayList list, boolean bool, String name){
        WriteBatch batch = db.batch();

        for (int x = 0; x < list.size(); x++){
            DocumentReference ref = db.collection(name).document((String) list.get(x));
            batch.update(ref,"condition", bool);
        }

        batch.commit().addOnCompleteListener(task -> {

        });
    }


}
