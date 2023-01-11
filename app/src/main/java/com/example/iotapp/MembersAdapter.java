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
import java.util.Map;
import java.util.Objects;

public class MembersAdapter extends BaseAdapter {

    private final ArrayList<Object> FullUser;
    private final LayoutInflater layoutInflater;

    public MembersAdapter(ArrayList<Object> FullUser, Context context) {
        this.FullUser = FullUser;
        this.layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return FullUser.size();
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

        Member mem = (Member) FullUser.get(i);

        permission.setText(mem.getRole());
        textView.setText(mem.getName());

        try{
            if (Objects.equals(mem.getPhoto(), "none")) {
                imageView.setImageResource(R.drawable.user);
            } else {
                Uri memberimg = Uri.parse(mem.getPhoto());
                imageView.setImageURI(memberimg);
                imageView.setPadding(0,0,0,0);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        } catch (Exception ignored){}

            return view;
        }
}
