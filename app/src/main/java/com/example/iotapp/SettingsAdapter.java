package com.example.iotapp;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.github.penfeizhou.animation.apng.APNGDrawable;
import com.github.penfeizhou.animation.loader.AssetStreamLoader;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SettingsAdapter extends BaseAdapter {

    private final String[] icons;
    private final String[] setting;
    private final Context context;
    LayoutInflater layoutInflater;

    public SettingsAdapter(String[] icons, String[] setting, Context context) {
        this.icons = icons;
        this.setting = setting;
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return icons.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null){
            view = layoutInflater.inflate(R.layout.setting, parent,false);
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        TextView textView = view.findViewById(R.id.textView23);
        textView.setText(setting[position]);
        ImageView imageView = view.findViewById(R.id.imageView4);

        AssetStreamLoader assetLoader = new AssetStreamLoader(context, icons[position]);
        APNGDrawable apngDrawable = new APNGDrawable(assetLoader);
        imageView.setImageDrawable(apngDrawable);

        apngDrawable.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
            @Override
            public void onAnimationStart(Drawable drawable) {
                super.onAnimationStart(drawable);
            }
        });

        if(Objects.equals(setting[position], "Manage your account")){
            textView.setOnClickListener(v -> {
                Intent i = new Intent(context,AccountInfo.class);
                context.startActivity(i);
            });
        }

        if(Objects.equals(setting[position], "Log out")){
            textView.setOnClickListener(v -> {
                auth.signOut();
                Intent i = new Intent(context,SignIn.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(i);
            });
        }

        return view;
    }
}
