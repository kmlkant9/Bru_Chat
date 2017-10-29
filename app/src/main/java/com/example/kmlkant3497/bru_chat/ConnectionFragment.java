package com.example.kmlkant3497.bru_chat;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import static com.example.kmlkant3497.bru_chat.TrafficFragment.listAdapter;


public class ConnectionFragment extends Fragment {
    public static ListView listViewConnection;
    public static ArrayList<String> connectionArray = new ArrayList<String>();
    public static ArrayAdapter<String> connectionAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connection, container, false);
        listViewConnection = (ListView) view.findViewById(R.id.listViewConnection);

        connectionAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1,
                connectionArray);

        listViewConnection.setAdapter(connectionAdapter);

        return view;
    }

    public void setListViewConnection(String value){

        connectionArray.add(value);
        connectionAdapter.notifyDataSetChanged();
    }
}
