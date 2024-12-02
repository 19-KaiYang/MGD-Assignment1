package com.example.dx1221_week3.main.dx1221_week3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.dx1221_week3.R;

public class LosePage extends Activity implements View.OnClickListener {

    private Button _backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.losepage);

        // Initialize the back button
        _backButton = findViewById(R.id.back_btn);
        // Set click listener for the back button
        _backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == _backButton) {
            // Start MainMenu activity
            Intent intent = new Intent(LosePage.this, MainMenu.class);
            startActivity(intent);

            // Optionally, finish LosePage so user cannot return to it
            finish();
        }
    }
}
