package com.example.hoang.myapplication.UI;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hoang.myapplication.Adapter.RecyclerListAdapter;
import com.example.hoang.myapplication.Fragment.DriverMap;
import com.example.hoang.myapplication.Fragment.UserMap;
import com.example.hoang.myapplication.Model.Account;
import com.example.hoang.myapplication.Model.Request;
import com.example.hoang.myapplication.Model.Trip;
import com.example.hoang.myapplication.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private Fragment fragment;
    private FragmentManager fragmentManager = getFragmentManager();
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private StorageReference mStorageRef;
    private CircleImageView profileImage;
    private TextView txtHeaderName;
    private final String CHILD_ACCOUNT = "ACCOUNT";
    private Account userData;
    private boolean loginMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        SharedPreferences sharedPreferences = getSharedPreferences(UserTypeActivity.LOGIN_MODE, Context.MODE_PRIVATE);
        loginMode = sharedPreferences.getBoolean(UserTypeActivity.LOGIN_MODE, true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        profileImage = (CircleImageView) header.findViewById(R.id.profileImage);
        txtHeaderName = (TextView) header.findViewById(R.id.txtheader_name);
        resetMap();
        final DatabaseReference root = FirebaseDatabase.getInstance().getReference().child(CHILD_ACCOUNT);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        DatabaseReference userRoot = root.child(user.getUid());
        userRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userData = dataSnapshot.getValue(Account.class);
                    loadData(userData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void resetMap() {
        Fragment fragment = fragmentManager.findFragmentByTag("MAP_FRAGMENT");
        if (fragment != null)
            fragmentManager.beginTransaction().remove(fragment).commit();
        if (loginMode) {
            fragment = new UserMap();
            fragmentManager.beginTransaction().replace(R.id.main_container, fragment, "MAP_FRAGMENT").commit();
        } else {
            fragment = new DriverMap();
            fragmentManager.beginTransaction().replace(R.id.main_container, fragment, "MAP_FRAGMENT").commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

    /*    if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else*/
        if (id == R.id.menu_policy) {
            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this, LauncherActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadAvatar() {
        StorageReference riversRef = mStorageRef.child("AVATAR/" + user.getUid() + ".jpg");
        Glide.with(this /* context */)
                .using(new FirebaseImageLoader())
                .load(riversRef)
                .into(profileImage);
        profileImage.setOnClickListener(this);
    }

    private void loadData(Account account) {
        txtHeaderName.setText(account.getFirst_name() + " " + account.getLast_name());
        loadAvatar();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.profileImage:
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(MainActivity.this, PersonalActivity.class);
                startActivity(intent);
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (loginMode) {
            switch (requestCode) {
                case RecyclerListAdapter.REQUEST_CODE_EXAMPLE: {

                    // resultCode được set bởi DetailActivity
                    // RESULT_OK chỉ ra rằng kết quả này đã thành công
                    if (resultCode == Activity.RESULT_OK) {
                        // Nhận dữ liệu từ Intent trả về
                        final Request result = (Request) data.getParcelableExtra(RequestActivity.EXTRA_DATA);
                        final int number = data.getIntExtra(RequestActivity.EXTRA_NUMBER, -1);
                        if (number != -1) {
                            UserMap myFragment = (UserMap) getFragmentManager().findFragmentByTag("MAP_FRAGMENT");
                            if (myFragment != null && myFragment.isVisible()) {
                                myFragment.updateRequestList(number, result);
                            }
                        }
                        // Sử dụng kết quả result bằng cách hiện Toast
                        Toast.makeText(this, "Result: " + result.getDestinationName(), Toast.LENGTH_LONG).show();
                    } else {
                        // DetailActivity không thành công, không có data trả về.
                    }

                    break;
                }
                case UserMap.REQUEST_TRIP_COMPLETE: {
                    if (resultCode == Activity.RESULT_OK) {
                        // Nhận dữ liệu từ Intent trả về
                        final Trip result = (Trip) data.getParcelableExtra("trip");
                        UserMap myFragment = (UserMap) getFragmentManager().findFragmentByTag("MAP_FRAGMENT");
                        if (myFragment != null && myFragment.isVisible()) {
                            myFragment.updateTripAndSendRequest(result);
                        }
                    } else {
                        Toast.makeText(this, "cancel", Toast.LENGTH_SHORT).show();
                        UserMap myFragment = (UserMap) getFragmentManager().findFragmentByTag("MAP_FRAGMENT");
                        if (myFragment != null && myFragment.isVisible()) {
                            myFragment.resetRequestStatus();
                        }
                    }
                    break;
                }

            }
        }
    }
    public void onRequestListItemPostionChange(){
        UserMap myFragment = (UserMap) getFragmentManager().findFragmentByTag("MAP_FRAGMENT");
        if (myFragment != null && myFragment.isVisible()) {
            myFragment.updateRequestListItemPostion();
        }
    }
}
