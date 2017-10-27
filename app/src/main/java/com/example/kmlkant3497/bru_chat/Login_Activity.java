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

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.example.kmlkant3497.bru_chat.R.id.button_join;
import static com.example.kmlkant3497.bru_chat.R.id.useLogo;

public class Login_Activity extends AppCompatActivity {

    // helps in Debugging (via LOG messages)
    private static final String TAG = "Login_Activity.java";

    public Button button_join;
    public EditText editText_ip;
    String ip_address = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        editText_ip = (EditText) findViewById(R.id.editText_ip);

        button_join = (Button) findViewById(R.id.button_join);
        button_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the IP address written in editText
                ip_address = editText_ip.getText().toString();
                new MyAsyncTask().execute(ip_address);
                // Check if the IP address entered is correct or not
//                if (isCorrect(ip_address)){
//                    // Open next corresponding activity
//                    Log.d(TAG, "IP-" + ip_address + " is Correct");
//                    Toast.makeText(getApplicationContext(), "Welcome !..", Toast.LENGTH_SHORT).show();
//                    Intent myIntent = new Intent(Login_Activity.this,ClientActivity.class);
//                    startActivity(myIntent);
//                }
//                else {
//                    Log.d(TAG, "IP-" + ip_address + " is InCorrect");
//                    Toast.makeText(getApplicationContext(), "Incorrect IP !..", Toast.LENGTH_SHORT).show();
//                }
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
                        return true;
                    }
                } catch (UnknownHostException ex) {
                    Log.d(TAG, ex.toString());
                }
            }
            return false;
        }
    }

}
