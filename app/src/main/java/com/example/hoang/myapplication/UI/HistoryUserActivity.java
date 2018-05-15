package com.example.hoang.myapplication.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hoang.myapplication.Adapter.HistoryArrayAdapter;
import com.example.hoang.myapplication.InstanceVariants;
import com.example.hoang.myapplication.Model.Driver;
import com.example.hoang.myapplication.Model.Trip;
import com.example.hoang.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class HistoryUserActivity extends AppCompatActivity {
    private ConstraintLayout groupNotFound, groupHistory;
    private Button btnNotFound;
    private NiceSpinner niceSpinner;
    private ListView listView;
    private ArrayList<Trip> trips = new ArrayList<>();
    private ArrayList<String> tripid = new ArrayList<>();
    private HistoryArrayAdapter adapter;
    private String TAG = "hiehk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        listView = (ListView) findViewById(R.id.listHistory);
        groupNotFound = (ConstraintLayout) findViewById(R.id.groupNotFound);
        groupHistory = (ConstraintLayout) findViewById(R.id.groupHistory);
        btnNotFound = (Button) findViewById(R.id.btnNew);
        niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);

        List<String> dataset = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.array_name)));
        niceSpinner.attachDataSource(dataset);

        adapter = new HistoryArrayAdapter(this, R.layout.item_history_user, trips);
        adapter.getFilter().filter("");
        listView.setAdapter(adapter);
        loadHistory();
        btnNotFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryUserActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        adapter.getFilter().filter("");
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "onItemSelected: " + position);
                        check();
                        break;
                    case 1:
                        adapter.getFilter().filter("working");
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "onItemSelected: " + position);
                        check();
                        break;
                    case 2:
                        adapter.getFilter().filter("done");
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "onItemSelected: " + position);
                        check();
                        break;
                    case 3:
                        adapter.getFilter().filter("cancel");
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "onItemSelected: " + position);
                        check();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HistoryUserActivity.this, HistoryDetail.class);
                intent.putExtra("package", trips.get(position));
                startActivity(intent);
            }
        });
    }

    private void check() {
        if (adapter.getCount() == 0) {
            groupHistory.setVisibility(View.GONE);
            groupNotFound.setVisibility(View.VISIBLE);
        } else {
            groupHistory.setVisibility(View.VISIBLE);
            groupNotFound.setVisibility(View.GONE);
        }
    }

    private void loadHistory() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference = reference.child(InstanceVariants.CHILD_HISTORY).child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && dataSnapshot.getChildrenCount() > 0) {
                    groupHistory.setVisibility(View.VISIBLE);
                    groupNotFound.setVisibility(View.GONE);
                    ArrayList<String> strings = new ArrayList<>();
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        strings.add(issue.getKey());
                    }
                    trips.clear();

                    for (String item : strings) {
                        loadHistoryDetail(item);
                    }
                } else {
                    groupHistory.setVisibility(View.GONE);
                    groupNotFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadHistoryDetail(final String id) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child(InstanceVariants.CHILD_TRIPS).child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Trip trip = dataSnapshot.getValue(Trip.class);
                            trips.add(trip);
                            Log.d("hieuhk", "onDataChange: add " + trip.getId());
                            adapter.notifyDataSetChanged();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
/*        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

}
