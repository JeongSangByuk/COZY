package com.example.cozy.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.cozy.IntroThread;
import com.example.cozy.R;

public class IntroActivity extends AppCompatActivity {
    public static Activity introActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        introActivity = IntroActivity.this;

        IntroThread introThread = new IntroThread(handler);
        introThread.start();   // IntroThread의 run() 메소드 실행

    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 1) {
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    };

}