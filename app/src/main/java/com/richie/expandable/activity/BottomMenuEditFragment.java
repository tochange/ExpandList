package com.richie.expandable.activity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.richie.expandable.R;
import com.richie.expandable.Util;
import com.richie.expandable.adapter.ExpandableListAdapter;
import com.richie.expandable.adapter.GroupExpandedListener;
import com.richie.expandable.adapter.SelectedListAdapter;
import com.richie.expandable.entity.Item;

import java.util.ArrayList;
import java.util.Collections;


public class BottomMenuEditFragment extends Fragment implements View.OnClickListener {
    private static final int MAX_SELECTED_COUNT = 5;
    private SharedPreferences config;
    private final String KEY = "selected_items";
    private ArrayList<Item> mSelectedItems;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.bottom_menu_fragment, container, false);
        view.findViewById(R.id.cancel).setOnClickListener(this);
        view.findViewById(R.id.ok).setOnClickListener(this);

        final ArrayList<Util.Classify> classifies = Util.getMenus(getContext());
        mSelectedItems = restoreData(classifies);

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list_item);
        updateSelectedItemView(recyclerView, mSelectedItems);

        final ExpandableListView listView = (ExpandableListView) view.findViewById(R.id.expandable_list);
        setListListener(classifies, listView, recyclerView);
        return view;
    }

    private void setListListener(final ArrayList<Util.Classify> classifies,
                                 final ExpandableListView listView, final RecyclerView recyclerView) {
        final ExpandableListAdapter adapter = new ExpandableListAdapter(classifies);

        listView.setAdapter(adapter);
        adapter.setOnGroupExpandedListener(new GroupExpandedListener() {
            @Override
            public void onGroupExpanded(int groupPosition) {
                expandOnlyOne(listView, groupPosition);
            }
        });

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // 请务必返回 false，否则分组不会展开
                return false;
            }
        });

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Item item = classifies.get(groupPosition).items.get(childPosition);
                final boolean oldState = item.selected;
                if (!oldState && mSelectedItems.size() >= (MAX_SELECTED_COUNT)) {
                    Toast.makeText(v.getContext(), "已达到最大" + MAX_SELECTED_COUNT + "个选中", Toast.LENGTH_SHORT).show();
                    return true;
                }

                item.selected = !oldState;
                if (item.selected) {
                    mSelectedItems.add(item);
                } else {
                    mSelectedItems.remove(item);
                }

                adapter.notifyDataSetChanged();

                updateSelectedItemView(recyclerView, mSelectedItems);
                return true;
            }
        });
    }

    private void saveData(ArrayList<Item> selectedItems) {
        if (selectedItems == null) return;
        final Gson gson = new Gson();
        String s = gson.toJson(selectedItems);
        config.edit().putString(KEY, s).apply();
    }

    private ArrayList<Item> restoreData(ArrayList<Util.Classify> addItems) {
        config = getActivity().getSharedPreferences("config", 0);
        String selectedItems = config.getString(KEY, "");

        final Gson gson = new Gson();
        final ArrayList<Item> historyItems = gson.fromJson(selectedItems,
                new TypeToken<ArrayList<Item>>() {
                }.getType());

        final ArrayList<Item> ret = new ArrayList<>();
        if (historyItems == null || historyItems.isEmpty()) return ret;

        for (Util.Classify classify : addItems) {
            if (classify == null || classify.items == null) continue;
            for (Item item : classify.items) {
                if (item == null) continue;
                boolean isHistorySelected = isHistorySelected(item.name, historyItems);
                item.selected = isHistorySelected;
                if (isHistorySelected) {
                    ret.add(item);
                }
            }
        }
        return ret;
    }

    private boolean isHistorySelected(String name, ArrayList<Item> items) {
        if (items == null || items.isEmpty() || TextUtils.isEmpty(name)) return false;
        for (Item item : items) {
            if (name.equals(item.name)) return true;
        }
        return false;
    }

    private void updateSelectedItemView(RecyclerView listView, final ArrayList<Item> selected) {
        if (selected == null) return;
        listView.setLayoutManager(new LinearLayoutManager(listView.getContext()));
        SelectedListAdapter adapter = new SelectedListAdapter(listView.getContext(), selected);
        listView.setAdapter(adapter);

        bindItemClickListener(adapter, selected);
    }

    private void bindItemClickListener(final SelectedListAdapter adapter, final ArrayList<Item> selected) {
        adapter.setOnClickListener(new SelectedListAdapter.AdapterListener() {
            @Override
            public void ItemClickListener(View view, boolean isUp, int position) {
                if (isUp) {
                    if (position > 0) {
                        Collections.swap(selected, position - 1, position);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    if (position < selected.size() - 1) {
                        Collections.swap(selected, position, position + 1);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private ArrayList<Item> filterSelectedItems(ArrayList<Util.Classify> classifies) {
        ArrayList<Item> list = new ArrayList<>();
        if (classifies == null) return list;
        for (Util.Classify classify : classifies) {
            if (classify == null || classify.items == null) continue;
            for (Item item : classify.items) {
                if (item == null) continue;
                if (item.selected) {
                    list.add(item);
                }
            }
        }
        return list;
    }

    private boolean expandOnlyOne(ExpandableListView expandableListView, int expandedPosition) {
        boolean result = true;
        int groupLength = expandableListView.getExpandableListAdapter().getGroupCount();
        for (int i = 0; i < groupLength; i++) {
            if (i != expandedPosition && expandableListView.isGroupExpanded(i)) {
                result &= expandableListView.collapseGroup(i);
            }
        }
        return result;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ok) {
            saveData(mSelectedItems);
        }
        getActivity().getSupportFragmentManager().beginTransaction()
                .remove(this).commitAllowingStateLoss();
    }

    public static void addFragment(AppCompatActivity activity, int toBeReplaceLayoutId) {
        BottomMenuEditFragment fragment = new BottomMenuEditFragment();
        activity.getSupportFragmentManager().beginTransaction()
                .replace(toBeReplaceLayoutId, fragment).commitAllowingStateLoss();
    }
}
