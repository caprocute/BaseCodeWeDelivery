package com.example.hoang.myapplication.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hoang.myapplication.Model.Gift;
import com.example.hoang.myapplication.Model.Request;
import com.example.hoang.myapplication.R;

public class InformationDetailActivity extends AppCompatActivity {
    private Gift gift;
    private TextView txtName, txtTime;
    private ImageView imgGift, imgBack;
    private Button btnDe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_detail);
        Intent intent = getIntent();
        gift = (Gift) intent.getParcelableExtra("package");

        txtName = (TextView) findViewById(R.id.txtNameIn);
        txtTime = (TextView) findViewById(R.id.txtContentIn);
        imgGift = (ImageView) findViewById(R.id.imgIn);
        imgBack = (ImageView) findViewById(R.id.imgBackDe);
        btnDe = (Button) findViewById(R.id.btnLink);

        txtName.setText(gift.getGifName());
        txtTime.setText(gift.getGiftContent());
        if (gift.getImgUrl() != null && !gift.getImgUrl().isEmpty())
            Glide.with(this).load(gift.getImgUrl()).into(imgGift);

        btnDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
    }
}

