package com.example.hoang.myapplication.Adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.media.Rating;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hoang.myapplication.Model.Driver;
import com.example.hoang.myapplication.R;

public class FavoriteDriverAdapter extends ArrayAdapter<Driver> {
    Activity context = null;
    ArrayList<Driver> myArray = null;
    int layoutId;

    public FavoriteDriverAdapter(Activity context,
                                 int layoutId,
                                 ArrayList<Driver> arr) {
        super(context, layoutId, arr);
        this.context = context;
        this.layoutId = layoutId;
        this.myArray = arr;
    }


    public View getView(int position, View convertView,
                        ViewGroup parent) {

        LayoutInflater inflater =
                context.getLayoutInflater();
        convertView = inflater.inflate(layoutId, null);
        if (myArray.size() > 0 && position >= 0) {
            final TextView txtname = (TextView) convertView.findViewById(R.id.txtDriverName);
            final RatingBar rating = (RatingBar) convertView.findViewById(R.id.ratingDriver);
            final ImageView img = (ImageView) convertView.findViewById(R.id.imgPro);
            final ImageView type = (ImageView) convertView.findViewById(R.id.imgType);
            rating.setMax(5);
            txtname.setText(myArray.get(position).getmName());
            rating.setRating(myArray.get(position).getRating());

            if (myArray.get(position).getmService().equals("HereBike"))
                type.setImageDrawable(context.getDrawable(R.drawable.ic_motor_mode));
            else
                type.setImageDrawable(context.getDrawable(R.drawable.ic_car_mode));
            if (myArray.get(position).getmProfileImageUrl() != null && !myArray.get(position).getmProfileImageUrl().isEmpty()) {
                Glide.with(context).load(myArray.get(position).getmProfileImageUrl()).into(img);
            }
        }

        return convertView;
    }
}