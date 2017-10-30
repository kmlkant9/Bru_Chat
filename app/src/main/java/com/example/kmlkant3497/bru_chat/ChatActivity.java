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
    private Bundle bundle ;

    private Handler receiveHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        //initializing xmls
        editText_message = (EditText) findViewById(R.id.editText_message);
        button_send = (Button) findViewById(R.id.button_send);
        textView_message = (TextView) findViewById(R.id.textView_chat_history);
        Log.i(TAG, "Client: starting network thread");
        Thread thd = new Thread(new ChatActivity.receiveMessages());
        thd.start();
        Log.i(TAG, "Client: started network thread");

        receiveHandler = new Handler();
        bundle = getIntent().getExtras();

        // Enable the UpNavigation button
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //label on action bar
        try {
            getActionBar().setTitle(bundle.getString("clientName"));
        }
        catch(Exception ex){Log.d(TAG,"actionbar fail "+ex.toString());}
        try {
            getSupportActionBar().setTitle(bundle.getString("clientName"));
        }
        catch(Exception ex){Log.d(TAG,"Support fail "+ex.toString());}
        Log.d(TAG, "one");

        //button ClickListener
        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CMessage.sender = bundle.getString("sender");
                CMessage.receiver = bundle.getString("receiver");
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
                 s = "you -> " + formatMessage(s);
                 textView_message.append(s+"\n");
             } catch (Exception ex){Log.d(TAG,ex.toString());}
         }

         @Override
         protected String doInBackground(String... strings) {

             Log.i(TAG, "************doInBackground()");
             String sendMessage;
             PrintWriter printWriter = SocketHandler.getPrintWriter();

             sendMessage = CMessage.getMsg();
             printWriter.print(sendMessage);       // sending to server
             printWriter.flush();                    // flush the data

             Log.d(TAG, "*/*/*/*/*/* message has been sent");

             Log.d(TAG,"Sent Message is: " + sendMessage);

             return sendMessage;
         }
     }

    public String formatMessage(String message){
        int i=0,count=0;
        while(count<2){
            if(message.charAt(i)=='_') count++;
            i++;
        }
        return message.substring(i);
    }

    class receiveMessages implements Runnable{
        private String receiveMessage;

        private String TAG = "recvMessage Thread";

        @Override
        public void run() {
            Log.d(TAG, "in receiveMessages Thread");
            BufferedReader bufferedReader = SocketHandler.getBufferedReader();
            Log.d(TAG, "before while(true)");
            while (true) {
                try {
                    Log.d(TAG, "while > try");
                    if ((receiveMessage = bufferedReader.readLine()) != null) //receive from server
                    {
                        Log.d(TAG, "while > try > if");
                        //System.out.println(receiveMessage); // displaying at DOS prompt

                        receiveHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    CMessage.msg = receiveMessage;
                                    if(CMessage.isMessageFromServer()){
                                        String newClient = CMessage.getClientName();
                                        receiveMessage = "   "+ newClient +" joined your server!!";
                                    }
                                    else{
                                        //todo call function to format received message!!
                                        receiveMessage = bundle.getString("clientName") +"-> "+ formatMessage(receiveMessage);
                                    }
                                    setReceivedMessage(receiveMessage);
                                } catch (NullPointerException ex) {
                                    Log.d(TAG, "inside run.."+ex.toString());
                                }
                            }
                        });

                        Log.d(TAG, "Received msg is: " + receiveMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
