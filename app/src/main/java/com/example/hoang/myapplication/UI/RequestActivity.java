package com.example.hoang.myapplication.UI;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hoang.myapplication.Adapter.PlaceArrayAdapter;
import com.example.hoang.myapplication.Model.Request;
import com.example.hoang.myapplication.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.angmarch.views.NiceSpinner;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class RequestActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, Serializable {
    private static final String LOG_TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;
    public static final String EXTRA_DATA = "EXTRA_DATA";
    public static final String EXTRA_NUMBER = "EXTRA_NUMBER";
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(8.888130, 104.844789), new LatLng(21.500409, 107.598679));
    private Request request;
    private int number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        setTitle("Điểm đến");
        mGoogleApiClient = new GoogleApiClient.Builder(RequestActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompletePlaceTextView);
        mAutocompleteTextView.setThreshold(3);

        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);
        Intent intent = getIntent();
        request = (Request) intent.getParcelableExtra("request");
        number = intent.getIntExtra("number", -1);
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            if (request.isRequestFilled() | number == 0) {
                if (number != -1) {
                    // Selecting the first object buffer.
                    final Place place = places.get(0);
                    CharSequence attributions = places.getAttributions();
                    request.setDestination(place.getLatLng());
                    request.setDestinationName(place.getName() + "");
                    // set result and finish activity
                    final Intent data = new Intent();
                    // Truyền data vào intent
                    data.putExtra(EXTRA_DATA, request);
                    data.putExtra(EXTRA_NUMBER, number);
                    setResult(Activity.RESULT_OK, data);
                }
                finish();
            } else if (number != 0)
                showDailog(request, places);
        }
    };
    List<String> dataset = new LinkedList<>(Arrays.asList("Đồ nội thất","Hàng điện tử","Vật liệu xây dựng","Gói hàng nhỏ","Gói hàng lớn","Đồ ăn","Khác"));

    private void showDailog(final Request request, final PlaceBuffer postion) {
        final Dialog dialog = new Dialog(this);
        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.request_dialog, null);
        layout.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
        layout.setMinimumHeight((int) (displayRectangle.height() * 0.6f));

        Button dialogBack = (Button) layout.findViewById(R.id.btnBack);
        Button dialogConfirm = (Button) layout.findViewById(R.id.btnComfirm);
        Button dialogUpload = (Button) layout.findViewById(R.id.btnBack);
        final NiceSpinner niceSpinner = (NiceSpinner) layout.findViewById(R.id.niceSpinner);
        niceSpinner.attachDataSource(dataset);
        final EditText txtReceiverName = (EditText) layout.findViewById(R.id.edtTenNguoiNhan);
        final EditText txtReceiverPhone = (EditText) layout.findViewById(R.id.edtSoDienThoai);
        final EditText txtNote = (EditText) layout.findViewById(R.id.edtGhiChu);
        if (request.getUri() != null && !request.getUri().isEmpty())
            for (int i = 0; i < dataset.size(); i++) {
                if (dataset.get(i).equals(request.getUri()))
                    niceSpinner.setSelectedIndex(i);
            }

        if (request.isRequestFilled()) {
            txtNote.setText(request.getNote());
            txtReceiverName.setText(request.getReceiverName());
            txtReceiverPhone.setText(request.getReceiverNumber());
        }
        dialogBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialogConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PhoneNumberUtils.isGlobalPhoneNumber(txtReceiverPhone.getText().toString()))
                    txtReceiverPhone.setError("Số điện thoại không hợp lệ");
                else if (txtReceiverName.getText().toString().isEmpty())
                    txtReceiverName.setError("Không được để trống");
                else if (txtReceiverPhone.getText().toString().isEmpty())
                    txtReceiverPhone.setError("Không được để trống");
                else {
                    request.setReceiverNumber(txtReceiverPhone.getText().toString());
                    request.setReceiverName(txtReceiverName.getText().toString());
                    request.setNote(txtNote.getText().toString());
                    request.setUri(dataset.get(niceSpinner.getSelectedIndex()));
                    if (number != -1) {
                        // Selecting the first object buffer.
                        final Place place = postion.get(0);
                        CharSequence attributions = postion.getAttributions();
                        request.setDestination(place.getLatLng());
                        request.setDestinationName(place.getName() + "");
                        // set result and finish activity
                        final Intent data = new Intent();
                        // Truyền data vào intent
                        data.putExtra(EXTRA_DATA, request);
                        data.putExtra(EXTRA_NUMBER, number);
                        setResult(Activity.RESULT_OK, data);
                    }
                    finish();
                }
            }
        });
        dialog.setContentView(layout);
        dialog.show();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }

    @Override
    public void onBackPressed() {

        // đặt resultCode là Activity.RESULT_CANCELED thể hiện
        // đã thất bại khi người dùng click vào nút Back.
        // Khi này sẽ không trả về data.
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }
}