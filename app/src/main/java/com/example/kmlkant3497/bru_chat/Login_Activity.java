package com.example.kmlkant3497.bru_chat;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import static com.example.kmlkant3497.bru_chat.R.id.button_join;
import static com.example.kmlkant3497.bru_chat.R.id.useLogo;

public class Login_Activity extends AppCompatActivity {

    // helps in Debugging (via LOG messages)
    private static final String TAG = "Login_Activity.java";

    public static String username,sndr,rcvr,ip_address;
    private boolean connected = false;
    public Button button_join;
    public EditText editText_ip;
    public EditText editText_usrname;

    public EditText editText_sndr;
    public EditText editText_rcvr;

    public static Socket socket;
    private final ByteBuffer buffer = ByteBuffer.allocate( 16384 );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        editText_ip = (EditText) findViewById(R.id.editText_ip);
        editText_usrname = (EditText) findViewById(R.id.editText_name);
        editText_sndr = (EditText) findViewById(R.id.editText_sender);
        editText_rcvr = (EditText) findViewById(R.id.editText_receiver);


        button_join = (Button) findViewById(R.id.button_join);
        button_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the IP address written in editText
                ip_address = editText_ip.getText().toString();
                username = editText_usrname.getText().toString();
                sndr = editText_sndr.getText().toString();
                rcvr = editText_rcvr.getText().toString();
                new MyAsyncTask().execute(ip_address);
            }
        });
    }



    public class MyAsyncTask extends AsyncTask<String,String,Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            Log.d(TAG,"inside onPostExecute"+ s);
            if (s){
                // Open next corresponding activity
                Log.d(TAG, "IP-" + ip_address + " is Correct");
                Toast.makeText(getApplicationContext(), "Welcome !..", Toast.LENGTH_SHORT).show();



                Intent myIntent = new Intent(Login_Activity.this,ClientActivity.class);
                startActivity(myIntent);
            }
            else {
                Log.d(TAG, "IP-" + ip_address + " is InCorrect");
                Toast.makeText(getApplicationContext(), "Incorrect IP !..", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            return isCorrect(params[0]);
        }

        private boolean isCorrect(String ip_address) {
            // write code to check correctness of given IP address
            // Just a dummy code
            if (ip_address.isEmpty()) {
                return false;
            }else {
                try {
                    InetAddress address = InetAddress.getByName(ip_address);
                    if (address instanceof Inet6Address || address instanceof Inet4Address) {
                        // It's ipv4

                        Log.d("LoginActivity", "C: Connecting...");
                        //Socket socket = new Socket(serverAddr, ServerActivity.SERVERPORT);
                        socket = new Socket(address, 8080);
                        socket.close();
                        Log.d("LoginActivity", socket.toString());
                        return true;
                    }
                } catch (UnknownHostException ex) {
                    Log.d(TAG, ex.toString());
                } catch (IOException e) {
                    Log.e("ClientActivity", "C: Error", e);
                    Toast.makeText(getApplicationContext(), "Connection Refused", Toast.LENGTH_SHORT).show();
                    connected = false;
                    e.printStackTrace();
                } catch (Exception e){
                    Log.e("ClientActivity", "C: Error", e);
                }finally {
                    try{socket.close();
                    }catch (IOException e){}
                }
            }
            return false;
        }
    }

}
