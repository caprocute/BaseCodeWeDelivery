package com.example.hoang.myapplication.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hoang.myapplication.R;

public class NotiFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    public NotiFragment() {
    }


    public static NotiFragment newInstance(int sectionNumber) {
        NotiFragment fragment = new NotiFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_information, container, false);
        return rootView;
    }
}