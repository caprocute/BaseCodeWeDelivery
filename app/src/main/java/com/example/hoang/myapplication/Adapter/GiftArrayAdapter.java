package com.example.hoang.myapplication.Adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hoang.myapplication.Model.Gift;
import com.example.hoang.myapplication.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;

public class GiftArrayAdapter extends ArrayAdapter<Gift> {
    private Activity context = null;
    private ArrayList<Gift> myArray = null;
    private int layoutId;
    private TextView txtName, txtTime;
    private ImageView imgGift;

    public GiftArrayAdapter(Activity context, int layoutId, ArrayList<Gift> arr) {
        super(context, layoutId, arr);
        this.context = context;
        this.layoutId = layoutId;
        this.myArray = arr;
    }

    private String convertLongtoTime(long inputDate) {
        long millisecond = Long.parseLong(inputDate + "");
        // or you already have long value of date, use this instead of milliseconds variable.
        String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
        return dateString;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        convertView = inflater.inflate(layoutId, null);
        if (myArray.size() > 0 && position >= 0) {
            txtName = (TextView) convertView.findViewById(R.id.txtGiftName);
            txtTime = (TextView) convertView.findViewById(R.id.txtGiftTime);
            imgGift = (ImageView) convertView.findViewById(R.id.imgGift);

            txtName.setText(myArray.get(position).getGifName());
            txtTime.setText(convertLongtoTime(myArray.get(position).getGiftTime()));
            if (myArray.get(position).getImgUrl() != null && !myArray.get(position).getImgUrl().isEmpty()) {
                Glide.with(context).load(myArray.get(position).getImgUrl()).into(imgGift);
            }

        }

        return convertView;
    }
}