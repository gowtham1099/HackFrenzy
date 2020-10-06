package com.example.hackfrenzy;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i=new Intent(Welcome.this,Login.class);
                startActivity(i);
                finish();
            }
        },3000);
    }
}