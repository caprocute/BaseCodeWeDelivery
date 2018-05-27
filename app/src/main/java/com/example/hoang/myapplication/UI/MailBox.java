package com.example.hoang.myapplication.UI;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hoang.myapplication.Adapter.FavoriteDriverAdapter;
import com.example.hoang.myapplication.Adapter.MailBoxAdapter;
import com.example.hoang.myapplication.InstanceVariants;
import com.example.hoang.myapplication.MailBox.ChatActivity;
import com.example.hoang.myapplication.MailBox.Messages;
import com.example.hoang.myapplication.Model.Driver;
import com.example.hoang.myapplication.Model.Person;
import com.example.hoang.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MailBox extends AppCompatActivity {
    private FirebaseUser mUser;
    private MailBoxAdapter adapter;
    private ArrayList<Person> people = new ArrayList<>();
    private ListView listMess;
    private ConstraintLayout groupNoMess;
    ArrayList<String> strings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_box);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        listMess = (ListView) findViewById(R.id.listMess);
        adapter = new MailBoxAdapter(this, R.layout.item_mail_box, people);
        groupNoMess = (ConstraintLayout) findViewById(R.id.groupNoMess);
        listMess.setAdapter(adapter);
        loadListMess();

        listMess.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent sendIntent = new Intent(MailBox.this, ChatActivity.class);
                sendIntent.putExtra("receiver", strings.get(position));
                startActivity(sendIntent);
            }
        });
    }


    private void loadListMess() {
        Query query = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_MAILBOX).orderByValue().startAt(mUser.getUid() + "_");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    strings.clear();
                    groupNoMess.setVisibility(View.GONE);
                    listMess.setVisibility(View.VISIBLE);
                    strings = new ArrayList<>();
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        String key = issue.getKey();
                        String[] object = key.split("_");
                        if (!object[1].equals(mUser.getUid())) strings.add(object[1]);
                    }
                    people.clear();
                    for (int i = 0; i < strings.size(); i++) {
                        loadDetail(strings.get(i));
                    }

                } else {
                    groupNoMess.setVisibility(View.VISIBLE);
                    listMess.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void loadDetail(String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(InstanceVariants.CHILD_ACCOUNT).orderByKey().equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        Map<String, String> map = (Map<String, String>) issue.getValue();
                        String name = map.get("first_name").toString();
                        String avatar = map.get("avartar").toString();
                        people.add(new Person(name, avatar));
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
