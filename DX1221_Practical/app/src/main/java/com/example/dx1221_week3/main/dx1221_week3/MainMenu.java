package com.example.dx1221_week3.main.dx1221_week3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.dx1221_week3.R;

import mgp2d.core.GameActivity;
import mgp2d.core.GameScene;

public class MainMenu extends Activity implements View.OnClickListener {

    private Button _helpButton;
    private Button _startButton;

    private Button _leaderboardButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);

        // Initialize buttons
        _helpButton = findViewById(R.id.help_btn);
        _helpButton.setOnClickListener(this);

        _startButton = findViewById(R.id.start_btn);
        _startButton.setOnClickListener(this);

        // Initialize Leaderboard button
        _leaderboardButton = findViewById(R.id.leaderboard_btn);
        _leaderboardButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == _helpButton) {
            // Start HelpPage activity
            startActivity(new Intent(this, HelpPage.class));
        } else if (v == _startButton) {
            // Start GameActivity and transition to the main game scene
            Intent intent = new Intent(this, GameActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the stack and start fresh
            startActivity(intent);

            // Now enter the main game scene after starting the GameActivity
            GameScene.enter(MainGameScene.class);

            // Finish MainMenu activity to prevent navigating back to it
            finish();
        } else if (v == _leaderboardButton) {
            // Start Leaderboard activity
            startActivity(new Intent(this, Leaderboard.class));
        }
    }
}
