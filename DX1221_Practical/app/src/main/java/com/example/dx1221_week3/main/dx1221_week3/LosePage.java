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
            // Start MainMenu activity with flags to clear the stack
            Intent intent = new Intent(this, MainMenu.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the stack
            startActivity(intent);

            // Finish LosePage so it is removed from the back stack
            finish();
        }
    }
}
