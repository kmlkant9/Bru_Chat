package com.example.kmlkant3497.bru_chat;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ClientActivity extends AppCompatActivity {

    public ListView listView_peers;
    public static ArrayList<String> clientArray = new ArrayList<String>();
    public static ArrayAdapter<String> clientAdapter;
    public Button button_leave;

    SwipeRefreshLayout mSwipeRefreshLayout;

    String TAG = "clientActivity";

    public static String username;
    public static String myMappingNumber;

    //private final ByteBuffer buffer = ByteBuffer.allocate(16384);
    Map<String, Integer> clientNames = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("NAME");

        //starting sendNameAsyncTask
        Log.i(TAG, "Client: starting sendNameAsyncTask");
        new sendNameAsyncTask().execute();

        // Creating the UpNavigation button
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        listView_peers = (ListView) findViewById(R.id.listview_peers);
        clientAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,clientArray
        );
        listView_peers.setAdapter(clientAdapter);

        button_leave = (Button) findViewById(R.id.button_leave);
        button_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // * * * Swipe Down To Refresh * * * //
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new refreshPeerList().execute();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        //todo client
        listView_peers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String Name = clientArray.get(position);
                String thisChatReceiver = clientNames.get(Name).toString();
                Intent intent = new Intent(ClientActivity.this, ChatActivity.class);
                //String message = "abc";
                intent.putExtra("clientName", Name);
                intent.putExtra("receiver" , thisChatReceiver);
                intent.putExtra("sender", myMappingNumber);
                startActivity(intent);
            }
        });
    }

    class refreshPeerList extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            BufferedReader bufferedReader = SocketHandler.getBufferedReader();
            PrintWriter printWriter = SocketHandler.getPrintWriter();

            //ask Server for List
            printWriter.print(CMessage.getMsg(myMappingNumber, "0", "LIST"));
            printWriter.flush();

            //Server sends List
            try {
                if((CMessage.msg = bufferedReader.readLine()) != null) {
                    if(CMessage.isMessageFromServer()) {
                        CMessage.updateList(clientNames);
                        Log.i(TAG, "Updating List ");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            clientArray.clear();
            for(Map.Entry<String , Integer> entry: clientNames.entrySet()){
                clientArray.add(entry.getKey());
                clientAdapter.notifyDataSetChanged();
            }
        }
    }

    class sendNameAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            Socket socket = SocketHandler.getSocket();
            if(socket.isClosed()) {
                Log.i(TAG, "Socket closed");
            }

            //get and set Input Stream
            InputStream inputStream = null;
            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            SocketHandler.setBufferedReader(bufferedReader);

            //get and set Output Stream
            OutputStream outputStream = null;
            try {
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            PrintWriter printWriter = new PrintWriter(outputStream, true);
            SocketHandler.setPrintWriter(printWriter);

            //Send your name to Server
            printWriter.print(CMessage.getMsg("0", "0", username));
            printWriter.flush();

            //Server sends back your mappingNo
            try {
                if((CMessage.msg = bufferedReader.readLine()) != null) {
                    if(CMessage.isMessageFromServer()) {
                        myMappingNumber = CMessage.getMyMappingNumber();
                        CMessage.sender = myMappingNumber;
                        Log.i(TAG, "My mapping no: " + myMappingNumber);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            //ask Server for List
            printWriter.print(CMessage.getMsg(myMappingNumber, "0", "LIST"));
            printWriter.flush();

            //Server sends List
            try {
                if((CMessage.msg = bufferedReader.readLine()) != null) {
                    if(CMessage.isMessageFromServer()) {
                        CMessage.updateList(clientNames);
                        Log.i(TAG, "Updating List ");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            clientArray.clear();
            for(Map.Entry<String , Integer> entry: clientNames.entrySet()){
                clientArray.add(entry.getKey());
                clientAdapter.notifyDataSetChanged();
            }

        }
    }

}
