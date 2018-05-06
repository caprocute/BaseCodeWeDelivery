package com.example.hoang.myapplication.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoang.myapplication.Model.Request;
import com.example.hoang.myapplication.R;
import com.example.hoang.myapplication.UI.MainActivity;
import com.example.hoang.myapplication.UI.RequestActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {
    public static final int REQUEST_CODE_EXAMPLE = 0x9345;
    private List<Request> mItems = new ArrayList<>();
    private Context context;
    private final OnStartDragListener mDragStartListener;
    private boolean check = false;

    public RecyclerListAdapter(Context context, OnStartDragListener dragStartListener, List<Request> mItems) {
        if (dragStartListener == null) check = true;
        mDragStartListener = dragStartListener;
        this.mItems = mItems;
        this.context = context;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bus_stop, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        Log.d("hieuhk", "onBindViewHolder: " + position + " " + mItems.get(position).getId());
        holder.txtBusstopName.setText(mItems.get(holder.getAdapterPosition()).getId().toString());
        if (mItems.get(position).isRequestFilled())
            holder.imgTripRequest.setImageDrawable(context.getDrawable(R.drawable.ic_trip_request_yellow));
        else
            holder.imgTripRequest.setImageDrawable(context.getDrawable(R.drawable.ic_trip_request_gray));
        if (holder.getAdapterPosition() == 0) {
            if (mItems.get(position).getDestinationName() != null && !mItems.get(position).getDestinationName().isEmpty())
                holder.txtBusstopName.setText(mItems.get(position).getDestinationName());
            else
                holder.txtBusstopName.setText(context.getString(R.string.chosse_start_point));
            holder.imgConnectLineTop.setVisibility(View.GONE);
            holder.imgTripRequest.setVisibility(View.GONE);
            holder.imgConnectLineBellow.setVisibility(View.VISIBLE);
            holder.imgBusstop.setImageDrawable(context.getDrawable(R.drawable.ic_circle_green));
        } else if (holder.getAdapterPosition() == getItemCount() - 1) {
            holder.imgConnectLineBellow.setVisibility(View.GONE);
            holder.imgBusstop.setImageDrawable(context.getDrawable(R.drawable.ic_circle_red));
            if (mItems.get(position).getDestinationName() != null && !mItems.get(position).getDestinationName().isEmpty())
                holder.txtBusstopName.setText(mItems.get(position).getDestinationName());
            else
                holder.txtBusstopName.setText(context.getString(R.string.destination_point));
        } else {
            holder.imgBusstop.setImageDrawable(context.getDrawable(R.drawable.ic_circle_red));
            if (mItems.get(position).getDestinationName() != null && !mItems.get(position).getDestinationName().isEmpty())
                holder.txtBusstopName.setText(mItems.get(position).getDestinationName());
            else
                holder.txtBusstopName.setText(context.getString(R.string.stop_poit));
            holder.imgTripRequest.setVisibility(View.VISIBLE);
            holder.imgConnectLineTop.setVisibility(View.VISIBLE);
            holder.imgConnectLineBellow.setVisibility(View.VISIBLE);
        }
        holder.txtBusstopName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RequestActivity.class);
                Request request = mItems.get(position);
                intent.putExtra("request", request);
                intent.putExtra("number", position);
                ((MainActivity) context).startActivityForResult(intent, REQUEST_CODE_EXAMPLE);
            }
        });
        // Start a drag whenever the handle view it touched
        if (check)
            holder.imgBusstop.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(holder);
                    }
                    return false;
                }
            });
        if (mItems.get(position).isRequestFilled()) {
            holder.imgTripRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDailog(mItems.get(position), position);
                }
            });
        }
    }

    private void showDailog(final Request request, final int postion) {
        final Dialog dialog = new Dialog(context);
        Rect displayRectangle = new Rect();
        Window window = ((MainActivity) context).getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.request_dialog, null);
        layout.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
        layout.setMinimumHeight((int) (displayRectangle.height() * 0.6f));

        Button dialogBack = (Button) layout.findViewById(R.id.btnBack);
        Button dialogConfirm = (Button) layout.findViewById(R.id.btnComfirm);
        Button dialogUpload = (Button) layout.findViewById(R.id.btnUploadAnh);

        final EditText txtReceiverName = (EditText) layout.findViewById(R.id.edtTenNguoiNhan);
        final TextView txtDialogName = (TextView) layout.findViewById(R.id.txtDialogName);
        final EditText txtReceiverPhone = (EditText) layout.findViewById(R.id.edtSoDienThoai);
        final EditText txtNote = (EditText) layout.findViewById(R.id.edtGhiChu);
        if (check) {
            dialogConfirm.setVisibility(View.GONE);
            dialogUpload.setVisibility(View.GONE);
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
        ImageView imgPic = (ImageView) layout.findViewById(R.id.imgAnh);
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
                    mItems.get(postion).setReceiverNumber(txtReceiverPhone.getText().toString());
                    mItems.get(postion).setReceiverName(txtReceiverName.getText().toString());
                    mItems.get(postion).setNote(txtNote.getText().toString());
                    notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });
        dialog.setContentView(layout);
        dialog.show();
    }

    @Override
    public void onItemDismiss(int position) {
        if (mItems.size() > 1) {
            mItems.remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
        } else Toast.makeText(context, "cannot delete", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemChanged(fromPosition);
        notifyItemChanged(toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
     * "handle" view that initiates a drag event when touched.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public final TextView txtBusstopName;
        public final ImageView imgBusstop, imgConnectLineTop, imgConnectLineBellow, imgTripRequest;

        public ItemViewHolder(View itemView) {
            super(itemView);
            txtBusstopName = (TextView) itemView.findViewById(R.id.txtBusstopName);
            imgBusstop = (ImageView) itemView.findViewById(R.id.imgBusstop);
            imgConnectLineTop = (ImageView) itemView.findViewById(R.id.imgConnectLineTop);
            imgConnectLineBellow = (ImageView) itemView.findViewById(R.id.imgConnectLineBellow);
            imgTripRequest = (ImageView) itemView.findViewById(R.id.imgTripRequest);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(Color.WHITE);
        }
    }
}
