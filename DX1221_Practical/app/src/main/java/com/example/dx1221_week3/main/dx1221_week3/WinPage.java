package com.example.dx1221_week3.main.dx1221_week3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dx1221_week3.R;

public class WinPage extends Activity implements View.OnClickListener {

    private static final int REQUEST_CODE_LEADERBOARD = 123;

    private Button _backButton;
    private EditText _nameInput;
    private int _timeLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.winpage);

        // Initialize views
        _backButton = findViewById(R.id.back_btn);
        _nameInput = findViewById(R.id.nameInput);

        // Set click listener
        _backButton.setOnClickListener(this);

        // Retrieve time left from intent
        _timeLeft = getIntent().getIntExtra("TIME_LEFT", 0);
        Log.d("MyTag", "Retrieved Time Left: " + _timeLeft);
    }

    @Override
    public void onClick(View v) {
        if (v == _backButton) {
            handleBackButtonClick();
        }
    }

    private void handleBackButtonClick() {
        String playerName = _nameInput.getText().toString().trim();
        if (!playerName.isEmpty()) {
            Log.d("MyTag", "Saving Player Name and Time: Name=" + playerName + ", Time=" + _timeLeft);
            Intent intent = new Intent(this, Leaderboard.class);
            intent.putExtra("PLAYER_NAME", playerName);
            intent.putExtra("TIME_LEFT", _timeLeft);
            startActivityForResult(intent, REQUEST_CODE_LEADERBOARD);
        } else {
            Toast.makeText(this, "Please enter your name.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LEADERBOARD && resultCode == RESULT_OK) {
            Toast.makeText(this, "Leaderboard updated!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
