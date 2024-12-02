package com.example.dx1221_week3.main.dx1221_week3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.dx1221_week3.R;

public class WinPage extends Activity implements View.OnClickListener {

    private Button _backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.winpage);
        _backButton = findViewById(R.id.back_btn);
        _backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == _backButton) {
            startActivity(new Intent().setClass(this, MainMenu.class));
        }
    }
}
