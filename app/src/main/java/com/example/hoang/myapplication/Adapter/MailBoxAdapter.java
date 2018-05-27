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
import com.example.hoang.myapplication.Model.Person;
import com.example.hoang.myapplication.R;

public class MailBoxAdapter extends ArrayAdapter<Person> {
    Activity context = null;
    ArrayList<Person> myArray = null;
    int layoutId;

    public MailBoxAdapter(Activity context,
                          int layoutId,
                          ArrayList<Person> arr) {
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
            final ImageView img = (ImageView) convertView.findViewById(R.id.imgPro);
            txtname.setText(myArray.get(position).getName());
            if (myArray.get(position).getAvatar() != null && !myArray.get(position).getAvatar().isEmpty()) {
                Glide.with(context).load(myArray.get(position).getAvatar()).into(img);
            }
        }

        return convertView;
    }
}