package com.example.hoang.myapplication.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hoang.myapplication.Model.TripRequest;
import com.example.hoang.myapplication.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private List<TripRequest> mItems = new ArrayList<>();
    private Context context;
    private final OnStartDragListener mDragStartListener;

    public RecyclerListAdapter(Context context, OnStartDragListener dragStartListener, List<TripRequest> mItems) {
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
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        Log.d("hieuhk", "onBindViewHolder: " + position + " " + mItems.get(position).getId());
        holder.txtBusstopName.setText(mItems.get(holder.getAdapterPosition()).getId().toString());
        if (holder.getAdapterPosition() == 0) {
            holder.imgConnectLineTop.setVisibility(View.GONE);
            holder.imgTripRequest.setVisibility(View.GONE);
            holder.imgBusstop.setImageDrawable(context.getDrawable(R.drawable.ic_circle_green));
        } else {
            holder.imgTripRequest.setVisibility(View.VISIBLE);
            holder.imgConnectLineTop.setVisibility(View.VISIBLE);
            holder.imgBusstop.setImageDrawable(context.getDrawable(R.drawable.ic_circle_red));
        }
        if (holder.getAdapterPosition() == getItemCount() - 1) {
            holder.imgConnectLineBellow.setVisibility(View.GONE);
        } else {
            holder.imgConnectLineBellow.setVisibility(View.VISIBLE);
        }
        // Start a drag whenever the handle view it touched
        holder.imgBusstop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public void onItemDismiss(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
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
