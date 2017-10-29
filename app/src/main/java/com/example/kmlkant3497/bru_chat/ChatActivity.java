package com.example.kmlkant3497.bru_chat;

import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

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
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatActivity extends AppCompatActivity {

    public EditText editText_message;
    public Button button_send;
    public TextView textView_message;
    private String TAG = "chatActivity";
    public String my_message;

    public static Socket socket;
    private final ByteBuffer buffer = ByteBuffer.allocate(16384);

    public EditText editText_sndr;
    public EditText editText_rcvr;

    private Handler receiveHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Enable the UpNavigation button

        //initializing xmls
        editText_message = (EditText) findViewById(R.id.editText_message);
        button_send = (Button) findViewById(R.id.button_send);
        textView_message = (TextView) findViewById(R.id.textView_chat_history);
        Log.i(TAG, "Client: starting network thread");
        Thread thd = new Thread(new ChatActivity.receiveMessages());
        thd.start();
        Log.i(TAG, "Client: started network thread");

        receiveHandler = new Handler();

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //label on action bar
        try {
            getActionBar().setTitle(Login_Activity.username);
        }
        catch(Exception ex){Log.d(TAG,"actionbar fail "+ex.toString());}
        try {
            getSupportActionBar().setTitle(Login_Activity.username);
        }
        catch(Exception ex){Log.d(TAG,"Support fail "+ex.toString());}

        editText_sndr = (EditText) findViewById(R.id.sender_editText);
        editText_rcvr = (EditText) findViewById(R.id.receiver_editText);

        Log.d(TAG, "one");

        //button ClickListener
        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CMessage.sender = editText_sndr.getText().toString();
                CMessage.receiver = editText_rcvr.getText().toString();
                CMessage.msg = editText_message.getText().toString();
                editText_message.setText("");
                new sendAsyncTask().execute();
            }
        });

        Log.d(TAG, "two");
    }

    public void setReceivedMessage(String value){
        textView_message.append(value+"\n");
    }

    class sendAsyncTask extends AsyncTask<String,String,String>{
         @Override
         protected void onPreExecute() {
             Log.d(TAG, "************onPreExecute()");
             super.onPreExecute();
         }

         @Override
         protected void onPostExecute(String s) {
             Log.d(TAG, "************onPostExecute()");
             super.onPostExecute(s);
             try{
             textView_message.append(s+"\n");
             } catch (Exception ex){Log.d(TAG,ex.toString());}
         }

         @Override
         protected String doInBackground(String... strings) {

             Log.i(TAG, "************doInBackground()");
             String sendMessage;
             // sending to client (pwrite object)
             OutputStream ostream = null;
             try {
                 ostream = socket.getOutputStream();
             } catch (IOException e) {
                 e.printStackTrace();
             }

             PrintWriter pwrite = new PrintWriter(ostream, true);
             sendMessage = CMessage.msg;
             sendMessage = CMessage.sender + "_" + CMessage.receiver + "_" + sendMessage;
             pwrite.print(sendMessage);       // sending to server
             pwrite.flush();                    // flush the data

             Log.d(TAG, "*/*/*/*/*/* message has been sent");

             Log.d(TAG,"Sent Message is: " + sendMessage);

             return sendMessage;
         }
     }

    class receiveMessages implements Runnable{
        private String receiveMessage;

        private String TAG = "recvMessage Thread";
        @Override
        public void run() {
            Log.d(TAG, "in receiveMessages Thread");
            try {
                socket = new Socket(Login_Activity.ip_address, 8080);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "socket made");

            InputStream istream = null;
            try {
                istream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "after try catch");
            BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));

            Log.d(TAG, "before while(true)");
            while (true) {
                try {
                    Log.d(TAG, "while > try");
                    if ((receiveMessage = receiveRead.readLine()) != null) //receive from server
                    {
                        Log.d(TAG, "while > try > if");
                        //System.out.println(receiveMessage); // displaying at DOS prompt

                        //TODO textView_message.append(CMessage.msg);
                        receiveHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    setReceivedMessage(receiveMessage);
                                } catch (NullPointerException ex) {
                                    Log.d(TAG, "inside run.."+ex.toString());
                                }
                            }
                        });

                        Log.d(TAG, "Recieved msg is: " + receiveMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
