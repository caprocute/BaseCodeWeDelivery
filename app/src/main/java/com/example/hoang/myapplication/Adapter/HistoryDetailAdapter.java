package com.example.hoang.myapplication.Adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.telephony.PhoneNumberUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hoang.myapplication.Model.Gift;
import com.example.hoang.myapplication.Model.Request;
import com.example.hoang.myapplication.R;
import com.example.hoang.myapplication.UI.HistoryDetail;
import com.example.hoang.myapplication.UI.MainActivity;
import com.firebase.ui.storage.images.FirebaseImageLoader;

public class HistoryDetailAdapter extends ArrayAdapter<Request> {
    private Activity context = null;
    private ArrayList<Request> myArray = null;
    private int layoutId;
    private TextView txtBusstopName, txtstatus;
    private ImageView imgTripRequest, imgConnectLineTop, imgConnectLineBellow, imgBusstop;


    public HistoryDetailAdapter(Activity context, int layoutId, ArrayList<Request> arr) {
        super(context, layoutId, arr);
        this.context = context;
        this.layoutId = layoutId;
        this.myArray = arr;
    }

    private String convertLongtoTime(long inputDate) {
        long millisecond = Long.parseLong(inputDate + "");
        // or you already have long value of date, use this instead of milliseconds variable.
        String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
        return dateString;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        convertView = inflater.inflate(layoutId, null);
        if (myArray.size() > 0 && position >= 0) {
            txtBusstopName = (TextView) convertView.findViewById(R.id.txtBusstopName);
            txtstatus = (TextView) convertView.findViewById(R.id.txtstatus);
            imgTripRequest = (ImageView) convertView.findViewById(R.id.imgTripRequest);
            imgConnectLineTop = (ImageView) convertView.findViewById(R.id.imgConnectLineTop);
            imgConnectLineBellow = (ImageView) convertView.findViewById(R.id.imgConnectLineBellow);
            imgBusstop = (ImageView) convertView.findViewById(R.id.imgBusstop);

            txtBusstopName.setText(myArray.get(position).getDestinationName());
            if (myArray.get(position).getStatus().equals("working"))
                txtstatus.setText("Đang chuyển hàng");
            else if (myArray.get(position).getStatus().equals("done"))
                txtstatus.setText("Đã xong");
            else if (myArray.get(position).getStatus().equals("return"))
                txtstatus.setText("Trả lại");
            else if (myArray.get(position).getStatus().equals("Cancel"))
                txtstatus.setText("Đơn hàng bị hủy");

            if (position == 0) {
                imgConnectLineTop.setVisibility(View.GONE);
                imgTripRequest.setVisibility(View.GONE);
                imgConnectLineBellow.setVisibility(View.VISIBLE);
                imgBusstop.setImageDrawable(context.getDrawable(R.drawable.ic_circle_green));
                txtstatus.setText("Điểm xuất phát");

            } else if (position == myArray.size() - 1) {
                imgConnectLineBellow.setVisibility(View.GONE);
                imgBusstop.setImageDrawable(context.getDrawable(R.drawable.ic_circle_red));
            } else {
                imgBusstop.setImageDrawable(context.getDrawable(R.drawable.ic_circle_red));
                imgTripRequest.setVisibility(View.VISIBLE);
                imgConnectLineTop.setVisibility(View.VISIBLE);
                imgConnectLineBellow.setVisibility(View.VISIBLE);
            }
            imgTripRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDailog(myArray.get(position), position);
                }
            });
        }

        return convertView;
    }

    private void showDailog(Request request, final int position) {
        boolean check = true;
        final Dialog dialog = new Dialog(context);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.request_dialog, null);

        Button dialogBack = (Button) layout.findViewById(R.id.btnBack);
        Button dialogConfirm = (Button) layout.findViewById(R.id.btnComfirm);

        final EditText txtReceiverName = (EditText) layout.findViewById(R.id.edtTenNguoiNhan);
        final TextView txtDialogName = (TextView) layout.findViewById(R.id.txtDialogName);
        final EditText txtReceiverPhone = (EditText) layout.findViewById(R.id.edtSoDienThoai);
        final EditText txtNote = (EditText) layout.findViewById(R.id.edtGhiChu);
        if (check) {
            dialogConfirm.setVisibility(View.GONE);
            txtDialogName.setText("Thông tin đơn hàng");
            txtNote.setEnabled(false);
            txtReceiverName.setEnabled(false);
            txtReceiverPhone.setEnabled(false);
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
                    myArray.get(position).setReceiverNumber(txtReceiverPhone.getText().toString());
                    myArray.get(position).setReceiverName(txtReceiverName.getText().toString());
                    myArray.get(position).setNote(txtNote.getText().toString());
                    notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });
        dialog.setContentView(layout);
        dialog.show();
    }
}