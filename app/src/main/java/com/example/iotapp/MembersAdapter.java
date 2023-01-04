package com.example.iotapp;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

public class MembersAdapter extends BaseAdapter {

    private final ArrayList<String> Names;
    private final ArrayList<String> Permissions;
    private final ArrayList<String> Photos;
    private final LayoutInflater layoutInflater;

    public MembersAdapter(ArrayList<String> Names,ArrayList<String> Permissions,ArrayList<String> Photos, Context context) {
        this.Names = Names;
        this.Photos = Photos;
        this.Permissions = Permissions;
        this.layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return Names.size();
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
        if (view == null) {
            view = layoutInflater.inflate(R.layout.homemembers, viewGroup, false);
        }
        TextView textView = view.findViewById(R.id.namemember);
        TextView permission = view.findViewById(R.id.textView16);
        ImageView imageView = view.findViewById(R.id.profilemember);
        permission.setText(Permissions.get(i));
        textView.setText(Names.get(i));

        try{
            if (Objects.equals(Photos.get(i), "none")) {
                imageView.setImageResource(R.drawable.user);
            } else {
                Uri memberimg = Uri.parse(Photos.get(i));
                imageView.setImageURI(memberimg);
                imageView.setPadding(0,0,0,0);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        } catch (Exception ignored){}

            return view;
        }
}
