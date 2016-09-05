package com.fypma.elvinlabs.parkme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import io.socket.client.Socket;

/**
 * Created by dileepa on 9/3/16.
 */
public class HelpActivity extends ActionBarActivity {

    private Socket mSocket;
    private String text = "test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Button clickButton = (Button) findViewById(R.id.back);
        clickButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HelpActivity.this, MapsActivity.class);
                startActivity(i);
                finish();
            }
        });

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}