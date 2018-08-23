package com.richie.expandable.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.richie.expandable.R;
import com.richie.expandable.entity.Item;

import java.util.ArrayList;

public class SelectedListAdapter extends RecyclerView.Adapter<SelectedListAdapter.ViewHolder> {
    private final LayoutInflater mInflater;
    private final ArrayList<Item> mItems;
    private AdapterListener mClickListener;

    public interface AdapterListener {
        void ItemClickListener(View view, boolean isUp, int position);
    }

    public SelectedListAdapter(Context context, ArrayList<Item> ItemInfoList) {
        this.mItems = ItemInfoList;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = mInflater.inflate(R.layout.bottom_menu_selected_list_item, null);
        return new ViewHolder(convertView, mClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.index.setText(String.valueOf(position));
        holder.name.setText(mItems.get(position).name);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setOnClickListener(AdapterListener listener) {
        mClickListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        AdapterListener listener;

        TextView name;
        TextView index;
        ImageView down;
        ImageView up;

        private ViewHolder(View convertView, AdapterListener listener) {
            super(convertView);

            this.listener = listener;
            name = (TextView) convertView
                    .findViewById(R.id.name);
            index = (TextView) convertView
                    .findViewById(R.id.index);
            up = (ImageView) convertView
                    .findViewById(R.id.up);
            down = (ImageView) convertView
                    .findViewById(R.id.down);
            up.setOnClickListener(this);
            down.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getLayoutPosition();
            boolean isUp = view.getId() == R.id.up;
            listener.ItemClickListener(view, isUp, pos);
        }
    }


}