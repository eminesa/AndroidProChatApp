package com.example.eminesa.androidprochatapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

/**
 * Created by eminesa on 5.06.2018.
 */

public class ListUsersAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<QBUser> qbUserarrayList;

    public ListUsersAdapter(Context context, ArrayList<QBUser> qbUserarrayList) {
        this.context = context;
        this.qbUserarrayList = qbUserarrayList;
    }

    @Override
    public int getCount() {
        return qbUserarrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return qbUserarrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_list_item_multiple_choice, null);

            TextView textView = (TextView) view.findViewById(android.R.id.text1);

            textView.setText(qbUserarrayList.get(position).getLogin());
        }
        return view;
    }
}
