package com.example.dx1221_week3.main.dx1221_week3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.dx1221_week3.R;

public class SplashPage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashpage);
        Thread splashThread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(3000);
                    startActivity(new Intent().setClass(SplashPage.this, MainMenu.class));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        splashThread.start();
    }
}
