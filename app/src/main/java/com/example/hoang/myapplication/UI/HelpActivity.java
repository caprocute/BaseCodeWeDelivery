package com.example.hoang.myapplication.UI;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.hoang.myapplication.Adapter.MyArrayAdapter;
import com.example.hoang.myapplication.R;
import com.roger.catloadinglibrary.CatLoadingView;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView imgBack;
    private ListView lstHelp;
    private MyArrayAdapter adapter;
    private ArrayList<String> arr = new ArrayList<>();
    private WebView webView;
    private boolean isWebViewShowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        imgBack = (ImageView) findViewById(R.id.imgBack2);
        lstHelp = (ListView) findViewById(R.id.lst_help);
        webView = (WebView) findViewById(R.id.webView);
        webView.setVisibility(View.GONE);
        imgBack.setOnClickListener(this);
        isWebViewShowing = false;
        arr = new ArrayList<String>();
        arr.add(getString(R.string.about_us));
        arr.add(getString(R.string.quick_help));
        arr.add(getString(R.string.mail_box));
        arr.add(getString(R.string.contact_help));
        arr.add(getString(R.string.connect_soicity));
        adapter = new MyArrayAdapter(this, R.layout.item_list_help, arr);
        lstHelp.setAdapter(adapter);
        lstHelp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        webView.setVisibility(View.VISIBLE);
                        isWebViewShowing = true;
                        String uri = "";
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.setWebViewClient(new MyWebViewClient());
                        webView.loadUrl("https://hoangkhachieu96.wixsite.com/here");
                        lstHelp.setVisibility(View.GONE);
                        break;
                    case 1:
                        webView.setVisibility(View.VISIBLE);
                        isWebViewShowing = true;
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.setWebViewClient(new MyWebViewClient());
                        webView.loadUrl("https://goo.gl/forms/U9M0LJBKUrdcwByU2");
                        lstHelp.setVisibility(View.GONE);
                        break;
                    case 2:
                        webView.setVisibility(View.VISIBLE);
                        isWebViewShowing = true;
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.setWebViewClient(new MyWebViewClient());
                        webView.loadUrl("https://goo.gl/forms/c6ykqgfiQFSVsZXD3");
                        lstHelp.setVisibility(View.GONE);
                        break;
                    case 3:
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        // Send phone number to intent as data
                        intent.setData(Uri.parse("tel:" + "01636458600"));
                        // Start the dialer app activity with number
                        startActivity(intent);
                        break;
                    case 4:
                        webView.setVisibility(View.VISIBLE);
                        isWebViewShowing = true;
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.setWebViewClient(new MyWebViewClient());
                        webView.loadUrl("https://www.facebook.com/groups/202108820590920/");
                        lstHelp.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    private class MyWebViewClient extends WebViewClient {
        private CatLoadingView mView;
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //hide loading image
            mView.dismiss();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //hide loading image
            mView = new CatLoadingView();
            mView.setCanceledOnTouchOutside(false);
            mView.show(getSupportFragmentManager(), "Loading your data");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack2:
                onBackPressed();
        }

    }

    @Override
    public void onBackPressed() {
        if (!isWebViewShowing)
            super.onBackPressed();
        else {
            isWebViewShowing = false;
            webView.setVisibility(View.GONE);
            lstHelp.setVisibility(View.VISIBLE);
        }
    }
}
