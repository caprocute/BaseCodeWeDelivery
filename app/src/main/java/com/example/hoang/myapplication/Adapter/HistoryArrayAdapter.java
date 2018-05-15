package com.example.hoang.myapplication.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hoang.myapplication.Model.Trip;
import com.example.hoang.myapplication.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryArrayAdapter extends BaseAdapter implements Filterable {

    private List<Trip> originalData = null;
    private int layoutId;
    private List<Trip> filteredData = null;
    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();
    private Context context;

    public HistoryArrayAdapter(Context context, int layoutId, List<Trip> data) {
        this.filteredData = data;
        this.originalData = data;
        this.layoutId = layoutId;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return filteredData.size();
    }

    public Object getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        // A ViewHolder keeps references to children views to avoid unnecessary calls
        // to findViewById() on each row.
        ViewHolder holder;

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        /*    if (convertView == null) {*/
        convertView = mInflater.inflate(layoutId, null);

        // Creates a ViewHolder and store references to the two children views
        // we want to bind data to.
        holder = new ViewHolder();
        holder.txtTimePickUp = (TextView) convertView.findViewById(R.id.txtTimePickUp);
        holder.txtCountStop = (TextView) convertView.findViewById(R.id.txtCountStop);
        holder.txtDestiationPoit = (TextView) convertView.findViewById(R.id.txtDestiationPoit);
        holder.txtStartPoitn = (TextView) convertView.findViewById(R.id.txtStartPoitn);
        holder.txtStatus = (TextView) convertView.findViewById(R.id.txtStatus);
        holder.img = (ImageView) convertView.findViewById(R.id.imgStatus);
        holder.back = (ConstraintLayout) convertView.findViewById(R.id.backgroundHis);

        // Bind the data efficiently with the holder.

        convertView.setTag(holder);
      /*  } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }*/

        // If weren't re-ordering this you could rely on what you set last time
        holder.txtStatus.setText(filteredData.get(position).getStatus());
        if (filteredData.get(position).getStatus().equals("working")) {
            holder.img.setImageDrawable(context.getDrawable(R.drawable.ic_status_loading));
        } else if (filteredData.get(position).getStatus().equals("done")) {
            holder.img.setImageDrawable(context.getDrawable(R.drawable.ic_status_done));
        } else if (filteredData.get(position).getStatus().equals("cancel")) {
            holder.img.setImageDrawable(context.getDrawable(R.drawable.ic_cancel_status));
        }
        holder.txtStartPoitn.setText(filteredData.get(position).getStartPointName() + "");
        holder.txtDestiationPoit.setText(filteredData.get(position).getDestinationPointNam() + "");
        holder.txtCountStop.setText(filteredData.get(position).getRequestCount()+" điểm dừng");
        holder.txtTimePickUp.setText(convertTime(filteredData.get(position).getPickupTime()));
        if (position % 2 != 0) holder.back.setBackgroundColor(Color.WHITE);
        else holder.back.setBackgroundColor(context.getColor(R.color.grey_100));
        return convertView;
    }

    private String convertTime(long number) {
        // or you already have long value of date, use this instead of milliseconds variable.
        String dateString = DateFormat.format("yyyy-MM-dd hh:mm:ss a", new Date(number)).toString();
        return dateString;
    }

    static class ViewHolder {
        TextView txtTimePickUp, txtStartPoitn, txtCountStop, txtDestiationPoit, txtStatus;
        ImageView img;
        ConstraintLayout back;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();
            if (constraint == "") {
                results.values = originalData;
                results.count = originalData.size();
                return results;
            }
            final List<Trip> list = originalData;

            int count = list.size();
            final ArrayList<Trip> nlist = new ArrayList<Trip>(count);

            Trip filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.getStatus().equals(filterString)) {
                    nlist.add(filterableString);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Trip>) results.values;
            notifyDataSetChanged();
        }

    }
}

//in your Activity or Fragment where of Adapter is instantiated :
/*

editTxt.addTextChangedListener(new TextWatcher() {

@Override
public void onTextChanged(CharSequence s, int start, int before, int count) {
        System.out.println("Text ["+s+"]");

        mSearchableAdapter.getFilter().filter(s.toString());
        }

@Override
public void beforeTextChanged(CharSequence s, int start, int count,
        int after) {

        }

@Override
public void afterTextChanged(Editable s) {
        }
        });*/
