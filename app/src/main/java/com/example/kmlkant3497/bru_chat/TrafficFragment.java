package com.example.kmlkant3497.bru_chat;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;


public class TrafficFragment extends Fragment {
    public static ListView listViewTraffic;
    public static ArrayList<String> listArray = new ArrayList<String>();
    public static ArrayAdapter<String> listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_traffic, container, false);

        listViewTraffic = (ListView) view.findViewById(R.id.listViewTraffic);

        listArray.add("Dummy1");
        listArray.add("Dummy 2");

        listAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1,
                listArray);

        listViewTraffic.setAdapter(listAdapter);

        return  view;
    }

    public void setListViewTraffic(String value){

        listArray.add(value);
        listAdapter.notifyDataSetChanged();
    }

}
