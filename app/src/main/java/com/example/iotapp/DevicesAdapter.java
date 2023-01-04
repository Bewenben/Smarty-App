package com.example.iotapp;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DevicesAdapter extends BaseAdapter {

    private final String[] imageNames;
    private final int[] imagesPhotos;
    private final String[] bgColors;
    private final LayoutInflater layoutInflater;

    public DevicesAdapter(String[] imageNames, int[] imagesPhotos, Context context, String[] bgColors) {
        this.imageNames = imageNames;
        this.imagesPhotos = imagesPhotos;
        this.bgColors = bgColors;
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
            view = layoutInflater.inflate(R.layout.row_items, viewGroup,false);
        }

        TextView tvName = view.findViewById(R.id.gridname);
        ImageView imagePhoto = view.findViewById(R.id.image_grid);
        tvName.setText(imageNames[i]);
        imagePhoto.setImageResource(imagesPhotos[i]);
        imagePhoto.setBackgroundColor(Color.parseColor(bgColors[i]));
        imagePhoto.setPadding(24,24,24,24);


        return view;
    }
}
