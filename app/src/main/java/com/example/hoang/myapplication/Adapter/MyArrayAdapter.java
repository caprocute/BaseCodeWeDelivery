package com.example.hoang.myapplication.Adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hoang.myapplication.R;

public class MyArrayAdapter extends ArrayAdapter<String> {
    Activity context = null;
    ArrayList<String> myArray = null;
    int layoutId;

    public MyArrayAdapter(Activity context,
                          int layoutId,
                          ArrayList<String> arr) {
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
            final TextView txtdisplay = (TextView)
                    convertView.findViewById(R.id.txtHelpItemName);
            final String emp = myArray.get(position);
            txtdisplay.setText(emp.toString());

        }

        return convertView;
    }
}