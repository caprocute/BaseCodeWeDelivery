package com.example.hoang.myapplication.UI;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.hoang.myapplication.R;


public class DriverAcceptRequest extends Dialog {

    private String message;
    private String title;
    private String btYesText;
    private String btNoText;
    private int icon=0;
    private View.OnClickListener btYesListener=null;
    private View.OnClickListener btNoListener=null;

    public DriverAcceptRequest(Context context) {
        super(context);
    }

    public DriverAcceptRequest(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected DriverAcceptRequest(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialogxml);
        TextView tv = (TextView) findViewById(R.id.malertTitle);
        tv.setCompoundDrawablesWithIntrinsicBounds(icon,0,0,0);
        tv.setText(getTitle());
        TextView tvmessage = (TextView) findViewById(R.id.aleartMessage);
        tvmessage.setText(getMessage());
        Button btYes = (Button) findViewById(R.id.aleartYes);
        Button btNo = (Button) findViewById(R.id.aleartNo);
        btYes.setText(btYesText);
        btNo.setText(btNoText);
        btYes.setOnClickListener(btYesListener);
        btNo.setOnClickListener(btNoListener);

    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    public void setPositveButton(String yes, View.OnClickListener onClickListener) {
        dismiss();
        this.btYesText = yes;
        this.btYesListener = onClickListener;


    }

    public void setNegativeButton(String no, View.OnClickListener onClickListener) {
        dismiss();
        this.btNoText = no;
        this.btNoListener = onClickListener;


    }
}