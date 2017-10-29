package com.example.kmlkant3497.bru_chat;

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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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
    private final ByteBuffer buffer = ByteBuffer.allocate(16384);

    private Handler receiveHandler=new Handler();
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
        Thread thd = new Thread(new ChatActivity.clientThread());
        thd.start();
        Log.i(TAG, "Client: started network thread");

        //button ClickListener
        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                my_message = editText_message.getText().toString();
                editText_message.setText("");
                new sendAsyncTask().execute();
            }
        });

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

    }

    public void setReceivedMessage(String value){
        textView_message.append(value+"\n");
    }

     class sendAsyncTask extends AsyncTask<String,String,String>{
         @Override
         protected void onPreExecute() {
             super.onPreExecute();
         }

         @Override
         protected void onPostExecute(String s) {
             super.onPostExecute(s);
             try{
             textView_message.append(CMessage.msg+"\n");}
             catch (Exception ex){Log.d(TAG,ex.toString());}
         }

         @Override
         protected String doInBackground(String... strings) {
             if(my_message.isEmpty()) my_message = "No_message";
             CMessage.msg = my_message;
             CMessage.sender = Login_Activity.sndr;
             CMessage.receiver = Login_Activity.rcvr;
             Log.d(TAG,my_message);
             try{CMessage.sendMessage(ClientActivity.channel, buffer);}
             catch (IOException ex){Log.d(TAG,ex.toString());}
             catch (Exception ex) {Log.d(TAG,ex.toString());}
             return null;
         }
     }

    class clientThread implements Runnable {

        @Override
        public void run() {
            try {
                Selector selector = ClientActivity.selector;
                SocketChannel channel = ClientActivity.channel;
                Log.d(TAG, "Channel open"+channel.socket());
                //channel.configureBlocking(false);
                int operations = SelectionKey.OP_CONNECT | SelectionKey.OP_READ;
                channel.register(selector, operations);
                Log.d(TAG, "registered");

                while (true) {
                    if (selector.select() > 0) {
                        Log.d(TAG, "select>0");
                        boolean doneStatus = processReadySet(selector.selectedKeys(), channel, selector);
                        if (doneStatus) {
                            break;
                        }
                    }
                }
                channel.close();

            } catch (UnknownHostException ex) {
                Log.d(TAG, ex.toString());
            } catch (IOException ex) {
                Log.d(TAG, ex.toString());
            } catch (Exception ex) {
                Log.d(TAG, ex.toString());
            }

        }


        public boolean processReadySet(Set readySet, SocketChannel channel, Selector selector) throws Exception {
            Iterator iterator = readySet.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = (SelectionKey)
                        iterator.next();
                iterator.remove();
                if (key.isReadable()) {
                    try {
                        boolean ok = CMessage.recvMessage(channel, buffer);
                        Log.d(TAG, "Received above message");
                        //TODO textView_message.append(CMessage.msg);
                        receiveHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String composite = CMessage.sender + " -> " + CMessage.receiver + ": " + CMessage.msg;
                                    setReceivedMessage(composite);
                                } catch (NullPointerException ex) {
                                    Log.d(TAG, "inside run.."+ex.toString());
                                }
                            }
                        });
                    }catch (Exception ex){Log.d(TAG,"outside run.."+ex.toString());}
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
    }
}
