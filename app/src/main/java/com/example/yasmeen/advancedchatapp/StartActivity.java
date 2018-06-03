package com.example.yasmeen.advancedchatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {
    private Button mRegButton;
    private Button mLogButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mRegButton =(Button)findViewById(R.id.RegCreateButton);
        mRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        mLogButton =(Button)findViewById(R.id.haveAccount);
        mLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(StartActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
