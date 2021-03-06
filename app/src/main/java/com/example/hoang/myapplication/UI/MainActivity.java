package com.example.hoang.myapplication.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.example.hoang.myapplication.Model.Driver;
import com.example.hoang.myapplication.Model.Request;
import com.example.hoang.myapplication.Model.Trip;
import com.example.hoang.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        navigationView.setItemIconTintList(null);
        View header = navigationView.getHeaderView(0);
        profileImage = (CircleImageView) header.findViewById(R.id.profileImage);
        txtHeaderName = (TextView) header.findViewById(R.id.txtheader_name);
        resetMap();
        checkIfRequesredDriver();
        final DatabaseReference root = FirebaseDatabase.getInstance().getReference().child(CHILD_ACCOUNT);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        DatabaseReference userRoot = root.child(user.getUid());
        userRoot.addValueEventListener(new ValueEventListener() {
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
        hideItem();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void hideItem() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        if (!loginMode) {
            nav_Menu.findItem(R.id.menu_new).setVisible(false);
            nav_Menu.findItem(R.id.menu_contact).setVisible(false);
        } else {
            nav_Menu.findItem(R.id.menu_driver_working).setVisible(false);
        }
    }

    Driver driver;

    private void checkIfRequesredDriver() {
        Intent intent = getIntent();
        driver = (Driver) intent.getParcelableExtra("package");
        if (driver != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Đang kết nối...");
            progressDialog.show();
            new CountDownTimer(3000, 1000) {
                public void onFinish() {
                    UserMap myFragment = (UserMap) fragmentManager.findFragmentByTag("MAP_FRAGMENT");
                    if (myFragment != null && myFragment.isVisible()) {
                        myFragment.requestWithDriver(driver);
                    }
                    progressDialog.dismiss();
                }

                public void onTick(long millisUntilFinished) {
                    // millisUntilFinished    The amount of time until finished.
                }
            }.start();

        }
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
        switch (id) {
            case R.id.menu_logout:
                showAlertDialog();
                break;
            case R.id.menu_new:
                resetMap();
                break;
            case R.id.menu_policy:
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_noti:
                intent = new Intent(MainActivity.this, InformationActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_contact:
                intent = new Intent(MainActivity.this, FavoriteDriverActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_history:
                intent = new Intent(MainActivity.this, HistoryUserActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_wallet:
                Toast.makeText(MainActivity.this, "Chức năng này sẽ được cập nhật trong các phiên bản tới", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_mail:
                intent = new Intent(MainActivity.this, MailBox.class);
                startActivity(intent);
                break;
            case R.id.menu_driver_working:
                intent = new Intent(MainActivity.this, DriverWorkingActivity.class);
                startActivity(intent);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadAvatar(Account account) {
        if (account.getAvartar() != null)
            Glide.with(this /* context */)
                    .load(account.getAvartar())
                    .into(profileImage);
        profileImage.setOnClickListener(this);
    }

    private void loadData(Account account) {
        txtHeaderName.setText(account.getFirst_name());
        loadAvatar(account);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.profileImage:
                if (loginMode) {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(MainActivity.this, PersonalActivity.class);
                    startActivity(intent);
                } else {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(MainActivity.this, DriverSettingsActivity.class);
                    startActivity(intent);
                }

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

    public void onRequestListItemPostionChange() {
        UserMap myFragment = (UserMap) getFragmentManager().findFragmentByTag("MAP_FRAGMENT");
        if (myFragment != null && myFragment.isVisible()) {
            myFragment.updateRequestListItemPostion();
        }
    }

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chú ý!");
        builder.setMessage("Đăng xuất khỏi tài khoản hiện tại? ");
        builder.setCancelable(false);
        builder.setPositiveButton("Đổng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!loginMode) {
                    DriverMap myFragment = (DriverMap) getFragmentManager().findFragmentByTag("MAP_FRAGMENT");
                    if (myFragment != null && myFragment.isVisible()) {
                        myFragment.onLogOut();
                    }
                }
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LauncherActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                finish();
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}
