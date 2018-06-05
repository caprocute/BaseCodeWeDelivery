package com.example.hoang.myapplication.UI;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.hoang.myapplication.Fragment.DriverMap;
import com.example.hoang.myapplication.Fragment.DriverWorkingMap;
import com.example.hoang.myapplication.Fragment.UserMap;
import com.example.hoang.myapplication.R;

public class DriverWorkingActivity extends AppCompatActivity {
    private FragmentManager fragmentManager = getFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_working);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        resetMap();
    }

    public void resetMap() {
        Fragment fragment = fragmentManager.findFragmentByTag("MAP_FRAGMENT");
        if (fragment != null) fragmentManager.beginTransaction().remove(fragment).commit();
        fragment = new DriverWorkingMap();
        fragmentManager.beginTransaction().replace(R.id.main_container, fragment, "MAP_FRAGMENT").commit();
    }
}
