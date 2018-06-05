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

import com.example.hoang.myapplication.Model.Request;
import com.example.hoang.myapplication.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListRequestAdapter extends BaseAdapter implements Filterable {

    private List<Request> originalData = null;
    private int layoutId;
    private List<Request> filteredData = null;
    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();
    private Context context;

    public ListRequestAdapter(Context context, int layoutId, List<Request> data) {
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
        holder.txtAdd = (TextView) convertView.findViewById(R.id.txtAdd);
        holder.txtRecei = (TextView) convertView.findViewById(R.id.txtRecei);
        holder.txtPhone = (TextView) convertView.findViewById(R.id.txtPhone);
        holder.txtId = (TextView) convertView.findViewById(R.id.txtID);
        holder.back = (ConstraintLayout) convertView.findViewById(R.id.back);


        // Bind the data efficiently with the holder.

        convertView.setTag(holder);
      /*  } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }*/

        // If weren't re-ordering this you could rely on what you set last time
        holder.txtAdd.setText(filteredData.get(position).getDestinationName());
        holder.txtRecei.setText(filteredData.get(position).getReceiverName() + "");
        holder.txtPhone.setText(filteredData.get(position).getReceiverNumber() + "");
        holder.txtId.setText("Mã xử lý đơn hàng: "+filteredData.get(position).getTripID() + "");
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
        TextView txtAdd, txtRecei, txtPhone, txtId;
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
            final List<Request> list = originalData;

            int count = list.size();
            final ArrayList<Request> nlist = new ArrayList<Request>(count);

            Request filterableString;

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
            filteredData = (ArrayList<Request>) results.values;
            notifyDataSetChanged();
        }

    }
}