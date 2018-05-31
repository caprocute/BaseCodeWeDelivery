package com.example.hoang.myapplication.MailBox;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoang.myapplication.InstanceVariants;
import com.example.hoang.myapplication.Model.Person;
import com.example.hoang.myapplication.R;
import com.example.hoang.myapplication.UI.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private LinearLayout layout;
    private RelativeLayout layout_2;
    private ImageView sendButton, imgDelete;
    private EditText messageArea;
    private ScrollView scrollView;
    private DatabaseReference reference1, reference2;
    private FirebaseUser mUser;
    private String mReceiver, mSend, mReceiverName;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mReceiver = getIntent().getStringExtra("receiver");
        loadDetail(mReceiver);

        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout) findViewById(R.id.layout2);
        sendButton = (ImageView) findViewById(R.id.sendButton);
        imgDelete = (ImageView) findViewById(R.id.imgDelete);
        messageArea = (EditText) findViewById(R.id.messageArea);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mSend = mUser.getUid();
        reference1 = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_MAILBOX).child(mSend + "_" + mReceiver);
        reference2 = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_MAILBOX).child(mReceiver + "_" + mSend);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if (!messageText.equals("")) {
                    //Map<String, Messages> map = new HashMap<String, Messages>();
                    long time = System.currentTimeMillis();
                    Messages messages = new Messages(messageText, mSend);
                    //map.put(time + "", messages);
                    reference1.child(time + "").setValue(messages);
                    reference2.child(time + "").setValue(messages);
                    messageArea.setText("");
                }
            }
        });
        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                /*Map<String, Messages> map = (Map<String, Messages>) dataSnapshot.getValue();*/
                Messages messages = dataSnapshot.getValue(Messages.class);

             /*   String message = map.get("message").toString();
                String userName = map.get("user").toString();*/
                String message = messages.getMess();
                String userName = messages.getUser();

                if (userName.equals(mSend)) {
                    addMessageBox("Bạn\n" + message, 1);
                } else {
                    addMessageBox(convertLongtoTime(Long.valueOf(dataSnapshot.getKey())) + "\n" + message, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
    }

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chú ý!");
        builder.setMessage("Xóa cuộc trò chuyện này? ");
        builder.setCancelable(false);
        builder.setPositiveButton("Đổng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mReceiver != null && !mReceiver.isEmpty()) {
                    reference1.removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            Toast.makeText(ChatActivity.this, "Đã xóa", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }
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

    private void loadDetail(String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(InstanceVariants.CHILD_ACCOUNT).orderByKey().equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        Map<String, String> map = (Map<String, String>) issue.getValue();
                        mReceiverName = map.get("first_name").toString();
                        if (mReceiverName != null && !mReceiverName.isEmpty())
                            toolbar.setTitle(mReceiverName);
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addMessageBox(String message, int type) {
        TextView textView = new TextView(ChatActivity.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if (type == 1) {
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_in);
        } else {
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.post(new Runnable() {

            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private String convertLongtoTime(long inputDate) {
        long millisecond = Long.parseLong(inputDate + "");
        // or you already have long value of date, use this instead of milliseconds variable.
        String dateString = DateFormat.format("hh:mm - MM/dd/yyyy", new Date(millisecond)).toString();
        return dateString;
    }
}
