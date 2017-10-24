package com.example.kmlkant3497.bru_chat;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public Button button_join;
    public Button button_host;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_join = (Button) findViewById(R.id.button_join);
        button_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this,Login_Activity.class);
                startActivity(myIntent);
            }
        });

        button_host = (Button) findViewById(R.id.button_host);
        button_host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent1 = new Intent(MainActivity.this, HostTabActivity.class);
                startActivity(myIntent1);
            }
        });
    }
}
