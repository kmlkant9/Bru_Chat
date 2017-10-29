package com.example.kmlkant3497.bru_chat;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ClientActivity extends AppCompatActivity {

    public ListView listView_peers;
    public Button button_leave;

    String TAG = "clientActivity";

    public static String username;
    public static String myMappingNumber;

    //private final ByteBuffer buffer = ByteBuffer.allocate(16384);
    Map<Integer, String> clientNames = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        Bundle bundle = getIntent().getExtras();
        username = bundle.getString(username);

        //starting sendNameAsyncTask
        Log.i(TAG, "Client: starting sendNameAsyncTask");
        new sendNameAsyncTask().execute();


//        Thread thd = new Thread(new clientThread());
//        thd.start();


        //Send Username to Host on Creation
//        CMessage.sender = Login_Activity.sndr;
//        CMessage.receiver = Login_Activity.rcvr;
//        CMessage.msg = usrname;
//        try {
//            CMessage.sendMessage(l_socket.getChannel(),buffer);
//            Log.d(TAG,"Sending Message1");
//            CMessage.msg = "Gulson";
//            CMessage.sendMessage(l_socket.getChannel(),buffer);
//            Log.d(TAG,"Sending Message gulson");
//        }catch (IOException ex){
//            Log.d(TAG,"Error in Sending Message");
//        }
//        Toast.makeText(getApplicationContext(), "Usename_Sent!..", Toast.LENGTH_SHORT).show();
//        Log.d(TAG,"Username_Sent!..");

        // Creating the UpNavigation button
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        listView_peers = (ListView) findViewById(R.id.listview_peers);

        button_leave = (Button) findViewById(R.id.button_leave);
        button_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listView_peers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(ClientActivity.this, ChatActivity.class);
                //String message = "abc";
                //intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            }
        });
    }

    /*class clientThread implements Runnable {
        private final String clientTag = "clientTHREAD";
        boolean finished = false;
        @Override
        public void run() {
            try {
                // Socket and Selector initialisation
                InetAddress serverIPAddress = InetAddress.getByName(Login_Activity.ip_address);
                int port = 8080;
                Log.d(TAG, "Port Created");
                InetSocketAddress serverAddress = new InetSocketAddress(
                        serverIPAddress, port);
                selector = Selector.open();
                channel = SocketChannel.open();
                Log.d(TAG, "Channel open");
                channel.configureBlocking(false);
                channel.connect(serverAddress);
                int operations = SelectionKey.OP_CONNECT | SelectionKey.OP_READ
                        | SelectionKey.OP_WRITE;
                channel.register(selector, operations);
                Log.d(TAG, "registered");

                while (!finished) {
                    Log.d(TAG, "inside while");
                    if (selector.select() > 0) {
                        Log.d(TAG, "select>0");
                        boolean doneStatus = clientProcessReadySet(selector.selectedKeys(), channel, selector);
                        if (doneStatus) {
                            break;
                        }
                    }
                }
                //channel.close();

            } catch (UnknownHostException ex) {
                Log.d(TAG, ex.toString());
            } catch (IOException ex) {
                Log.d(TAG, ex.toString());
            } catch (Exception ex) {
                Log.d(TAG, ex.toString());
            }

        }


    public boolean clientProcessReadySet(Set readySet, SocketChannel channel, Selector selector) throws Exception {
        Iterator iterator = readySet.iterator();
        while (iterator.hasNext()) {
            SelectionKey key = (SelectionKey)
                    iterator.next();
            iterator.remove();
            if (key.isConnectable()) {
                boolean connected = processConnect(key);
                Log.d(TAG, "connected = " + connected);
                if (!connected) {
                    return true; // Exit
                }
            }
            if (key.isReadable()) {
                boolean ok = CMessage.recvMessage(channel,buffer);
                Log.d(TAG,"[Server]: " + ok);
            }
            if (key.isWritable()) {
                System.out.print("Please enter a message(Bye to quit):");
                Log.d(TAG, "Message1");
//                String msg = userInputReader.readLine();
                CMessage.msg = Login_Activity.username;
                //CMessage.sender = Login_Activity.sndr;
                //CMessage.receiver = Login_Activity.rcvr;
//                if (msg.equalsIgnoreCase("bye")) {
//                    return true; // Exit
//                }
                SocketChannel sChannel = (SocketChannel) key.channel();
                CMessage.sendMessage(sChannel, buffer);
                //ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                //sChannel.write(buffer);

                //
                int interestSet = SelectionKey.OP_CONNECT | SelectionKey.OP_READ;
                channel.register(selector, interestSet);

                //TODO needs to finish after receiving its index code
                finished = true;



            }
        }
        return false; // Not done yet
    }

    public boolean processConnect(SelectionKey key) throws Exception {
        SocketChannel channel = (SocketChannel) key.channel();
        while (channel.isConnectionPending()) {
            channel.finishConnect();
        }
        return true;
    }

    public String processRead(SelectionKey key) throws Exception {
        SocketChannel sChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        sChannel.read(buffer);
        buffer.flip();
        Charset charset = Charset.forName("UTF-8");
        CharsetDecoder decoder = charset.newDecoder();
        CharBuffer charBuffer = decoder.decode(buffer);
        String msg = charBuffer.toString();
        return msg;
    }
} */

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
        }
    }
}


/*class CMessage {
    public static String sender;
    public static String receiver;
    public static String msg;
    private final static String msgTAG="CMessage";

    private static void clear() {
        sender="";
        receiver="";
        msg="";
    }

    public static String getMsg(String sender, String receiver, String msg) {
        String message = (sender+"_"+receiver+"_"+msg);
        Log.i("Cmessage", message);
        return message;
    }

    public static String getMsg() {
        String message = (sender+"_"+receiver+"_"+msg);
        Log.i("Cmessage", message);
        return message;
    }
    public static boolean sendMessage(SocketChannel sc, ByteBuffer buffer) throws IOException {
        Log.i(msgTAG, "From: " + CMessage.sender + " to: " + CMessage.receiver);
        String tempMessage = (CMessage.sender + "_" + CMessage.receiver + "_" + CMessage.msg );
        Log.d(msgTAG, tempMessage);

        // Writing 'message' to 'buffer'
        buffer.clear();
        buffer.put((tempMessage).getBytes());
        buffer.flip();

        Log.d(msgTAG, "still working");
        // Writing 'buffer' to 'socket'
        while(buffer.hasRemaining()) {
            sc.write(buffer);
        }
        return true;
    }

    public static boolean recvMessage(SocketChannel sc, ByteBuffer buffer) throws IOException {
        Log.i(msgTAG, "Receiving a message");
        // Retrieving 'message' into 'buffer'
        buffer.clear();
        sc.read( buffer );
        buffer.flip();

        // If 'buffer' is empty
        if (buffer.limit()==0) {
            return false;
        }

        CMessage.clear();
        int msgSection=0;

        // If 'buffer' is non-empty
        // Read the 'buffer'
        for(int i=0; i<buffer.limit(); i++) {
            // Reading the 'i-th' byte in buffer
            char b=(char)(buffer.get( i ) & 0xFF);

            // Parsing the message
            if(msgSection == 2) {
                CMessage.msg+=b;
            } else if(msgSection == 1){
                if(b=='_') msgSection++;
                else CMessage.receiver+=b;
            } else {
                if(b=='_') msgSection++;
                else CMessage.sender+=b;
            }
        }

        // Displaying LOGs
        Log.i(msgTAG, CMessage.sender);
        Log.i(msgTAG, CMessage.receiver);
        Log.i(msgTAG, CMessage.msg);

        return true;
    }
}*/
