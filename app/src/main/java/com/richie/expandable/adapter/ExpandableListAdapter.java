package com.richie.expandable.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.richie.expandable.Util;
import com.richie.expandable.R;
import com.richie.expandable.entity.Item;

import java.util.ArrayList;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private ArrayList<Util.Classify> data;
    private GroupExpandedListener mOnGroupExpandedListener;

    public ExpandableListAdapter(ArrayList<Util.Classify> data) {
        this.data = data;
    }

    public void setOnGroupExpandedListener(GroupExpandedListener onGroupExpandedListener) {
        mOnGroupExpandedListener = onGroupExpandedListener;
    }

    @Override
    public int getGroupCount() {
        return data.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return data.get(groupPosition).items.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return data.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return data.get(groupPosition).items.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View
            convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_menu_expand_item_group, parent, false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.label_group_normal);
            groupViewHolder.selectedToAll = (TextView) convertView.findViewById(R.id.selected_to_all);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        groupViewHolder.tvTitle.setText(data.get(groupPosition).classify);
        groupViewHolder.selectedToAll.setText(getClassifyIndexToAllText(data.get(groupPosition).items));
        return convertView;
    }

    private String getClassifyIndexToAllText(ArrayList<Item> items) {
        if (items == null) return "";
        int selectedCount = 0;
        for (Item entity : items) {
            if (entity != null && entity.selected) {
                selectedCount++;
            }
        }
        return "(" + selectedCount + "/" + items.size() + ")";
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View
            convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_menu_expand_item_child, parent, false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.label_expand_child);
            childViewHolder.itemIcon = (ImageView) convertView.findViewById(R.id.child_icon);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        childViewHolder.tvTitle.setText(data.get(groupPosition).items.get(childPosition).name);
        childViewHolder.itemIcon.setVisibility(data.get(groupPosition).items.get(childPosition)
                .selected ? View.VISIBLE : View.GONE);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        if (mOnGroupExpandedListener != null) {
            mOnGroupExpandedListener.onGroupExpanded(groupPosition);
        }
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
    }

    private static class GroupViewHolder {
        TextView tvTitle;
        TextView selectedToAll;
    }

    private static class ChildViewHolder {
        TextView tvTitle;
        ImageView itemIcon;
    }
}
