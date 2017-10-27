package com.example.kmlkant3497.bru_chat;

import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.text.format.Formatter;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class HostTabActivity extends AppCompatActivity {

    // helps in Debugging (via LOG messages)
    private static final String TAG = "HostTabActivity.java";

    private TextView serverStatus;

    // DEFAULT IP
    public static String SERVERIP;

    // DESIGNATE A PORT
    public static final int SERVERPORT = 8080;
    private final ByteBuffer buffer = ByteBuffer.allocate( 16384 );
    Map<Integer, SocketChannel> clients = new HashMap<Integer, SocketChannel>();
    Map<Integer, String> clientNames = new HashMap<>();

    Button button_disconnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_tab);

        // Get a support ActionBar to support upNavigation
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        serverStatus = (TextView) findViewById(R.id.textView_ip);

        SERVERIP = "Can't Connect";
        String interfaceName = new String();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                //Log.i(TAG, "iface-next --> " + iface);
                // filters out 127.0.0.1 and inactive interfaces
                if (iface.isLoopback() || !iface.isUp()) {continue;}

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    // *EDIT*
                    if (addr instanceof Inet6Address) {continue;}

                    SERVERIP = addr.getHostAddress();
                    interfaceName = iface.getDisplayName();
                    Log.i(TAG, "Interface Name: " + interfaceName);
                    Log.i(TAG, "Interface IP  : " + SERVERIP);
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        serverStatus.setText("Host IP: " + SERVERIP);
        Log.i(TAG, serverStatus.getText().toString());



        // Tab layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Connections"));
        tabLayout.addTab(tabLayout.newTab().setText("Traffic"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        // Disconnect button
        button_disconnect = (Button) findViewById(R.id.button_disconnect);
        button_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //starting networkThread
        Log.i(TAG, "starting network thread");
        Thread thd = new Thread(new hostThread());
        thd.start();
        Log.i(TAG, "started network thread");


    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            TextView myTextView =
//                    (TextView)findViewById(R.id.myTextView);
//            myTextView.setText("Button Pressed");
        }
    };

    class hostThread implements Runnable {
        private final String hostTag = "HostTHREAD";

        @Override
        public void run() {
            try{
                Log.i(hostTag, "on network thread");
                ServerSocketChannel ssc = ServerSocketChannel.open();
                ssc.configureBlocking( false );
                ServerSocket ss = ssc.socket();
                InetSocketAddress isa;
                try {
                    isa = new InetSocketAddress( SERVERPORT );
                    ss.bind(isa);
                }catch (Exception e) {
                    Log.i(hostTag, e.toString());
                }

                // Create a new Selector for selecting
                Selector selector = Selector.open();
                ssc.register( selector, SelectionKey.OP_ACCEPT);
                Log.i(hostTag, "Listening on port "+ SERVERPORT);
                Map<Integer, SocketChannel> clients = new HashMap<Integer, SocketChannel>();
                int mappingNo = 1;

                while(true) {
                    Log.i(hostTag, "InfiLoop");
                    // See if we've had any activity -- either
                    // an incoming connection, or incoming data on an
                    // existing connection
                    int num=0;
                    try {
                        num = selector.select();
                        Log.i(hostTag, "after selector");
                    }catch (Exception e) {
                        Log.i(hostTag, "error in selector"+e.toString());
                    }

                    if (num == 0) {
                        Log.i(hostTag, "No connections this iter");
                        continue;
                    }

                    Log.i(hostTag, "Got some connections");
                    Set keys = selector.selectedKeys();
                    Iterator it = keys.iterator();
                    while(it.hasNext()) {
                        Log.i(hostTag, "while hasNext");
                        SelectionKey key = (SelectionKey)it.next();
                        // What kind of activity is it?
                        if ((key.readyOps() & SelectionKey.OP_ACCEPT) ==
                                SelectionKey.OP_ACCEPT) {
                            Socket s = ss.accept();
                            Log.i(hostTag, "Got connection from "+s+" Assigned idx: "+mappingNo);

                            SocketChannel sc = s.getChannel();
                            sc.configureBlocking( false );

                            //store channel in Map
                            clients.put(mappingNo++, sc);

                            // Register it with the selector, for reading
                            sc.register( selector, SelectionKey.OP_READ );
                        }else if ((key.readyOps() & SelectionKey.OP_READ) ==
                                SelectionKey.OP_READ) {
                            SocketChannel sc = null;
                            try {
                                sc = (SocketChannel)key.channel();

                                boolean ok = TMessage.recvMessage(sc, buffer);

                                //Sending msg
                                TMessage.sender = TMessage.sender;
                                TMessage.receiver = TMessage.receiver;
                                TMessage.msg = TMessage.msg;
                                TMessage.sendMessage(buffer, clients);

                                // If the connection is dead, then remove it
                                // from the selector and close it
                                if (!ok) {
                                    key.cancel();

                                    Socket s = null;
                                    try {
                                        s = sc.socket();
                                        s.close();
                                    } catch( IOException ie ) {
                                        Log.e(hostTag, "Error closing socket "+s+": "+ie);
                                    }
                                }

                            } catch( IOException ie ) {

                                // On exception, remove this channel from the selector
                                key.cancel();

                                try {
                                    sc.close();
                                } catch( IOException ie2 ) { Log.e(hostTag, ie2.toString() ); }
                                Log.d(hostTag, "Closed "+sc);
                            }


                            }//elseif

                    }//while

                    // We remove the selected keys, because we've dealt
                    // with them.
                    keys.clear();
                }//while
            }catch(IOException ioe) {
                Log.e("hostThread", ioe.toString());
            }

        }//run()

    }




}

class TMessage{
    static String sender;
    static String receiver;
    static String msg;
    private final static String msgTAG="TMessage";

    private static void clear() {
        sender="";
        receiver="";
        msg="";
    }

    public static boolean sendMessage(ByteBuffer buffer, Map<Integer, SocketChannel> clients) throws IOException {
        Log.i(msgTAG, "From: "+TMessage.sender+" to: "+TMessage.receiver);

        buffer.clear();
        buffer.put((TMessage.sender+ "_" +TMessage.receiver+ "_"+TMessage.msg ).getBytes());
        buffer.flip();

        SocketChannel csc = clients.get( Integer.parseInt( TMessage.receiver ) );
        while(buffer.hasRemaining()) {
            csc.write(buffer);
        }
        return true;
    }

    public static boolean recvMessage(SocketChannel sc, ByteBuffer buffer) throws IOException {
        Log.i(msgTAG, "Receiving a message");
        buffer.clear();
        sc.read( buffer );
        buffer.flip();

        // If no data, close the connection
        if (buffer.limit()==0) {
            return false;
        }

        TMessage.clear();
        int msgSection=0;

        for(int i=0; i<buffer.limit(); i++) {
            char b=(char)(buffer.get( i ) & 0xFF);
            if(msgSection == 2) {
                TMessage.msg+=b;
            } else if(msgSection == 1){
                if(b=='_') msgSection++;
                else TMessage.receiver+=b;
            } else {
                if(b=='_') msgSection++;
                else TMessage.sender+=b;
            }
        }
        Log.i(msgTAG, TMessage.sender);
        Log.i(msgTAG, TMessage.receiver);
        Log.i(msgTAG, TMessage.msg);
        return true;
    }
}
