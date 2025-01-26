package com.example.dx1221_week3.main.dx1221_week3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dx1221_week3.R;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends Activity implements View.OnClickListener {

    private static final String PREFERENCES_NAME = "LeaderboardPrefs";
    private static final String LEADERBOARD_KEY = "Leaderboard";

    private Button backButton;
    private LinearLayout leaderboardEntriesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard);

        // Initialize back button
        backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(this);

        // Initialize leaderboard container
        leaderboardEntriesContainer = findViewById(R.id.leaderboardEntriesContainer);

        // Retrieve data from intent
        Intent intent = getIntent();
        String playerName = intent.getStringExtra("PLAYER_NAME");
        int timeLeft = intent.getIntExtra("TIME_LEFT", -1);

        // Add new player to the leaderboard if valid data is provided
        if (playerName != null && timeLeft >= 0) {
            savePlayerData(playerName, timeLeft);
        }

        // Load and display leaderboard data
        loadLeaderboardData();
    }

    @Override
    public void onClick(View v) {
        if (v == backButton) {
            // Navigate back to MainMenu
            Intent intent = new Intent(this, MainMenu.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void savePlayerData(String playerName, int timeLeft) {
        SharedPreferences prefs = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String leaderboardData = prefs.getString(LEADERBOARD_KEY, "");

        // Append the new player's data in the format: "PlayerName|TimeLeft;"
        String newEntry = playerName + "|" + timeLeft + ";";
        leaderboardData += newEntry;

        // Save back to SharedPreferences
        prefs.edit().putString(LEADERBOARD_KEY, leaderboardData).apply();
    }

    private void loadLeaderboardData() {
        SharedPreferences prefs = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String leaderboardData = prefs.getString(LEADERBOARD_KEY, "");

        // Parse the leaderboard data
        List<LeaderboardEntry> leaderboard = parseLeaderboardData(leaderboardData);

        // Clear existing entries in the UI
        leaderboardEntriesContainer.removeAllViews();

        // Display leaderboard entries
        for (LeaderboardEntry entry : leaderboard) {
            addPlayerEntry(entry.playerName, entry.timeLeft);
        }
    }

    private List<LeaderboardEntry> parseLeaderboardData(String data) {
        List<LeaderboardEntry> leaderboard = new ArrayList<>();

        if (!data.isEmpty()) {
            // Split the data into individual entries
            String[] entries = data.split(";");
            for (String entry : entries) {
                if (!entry.isEmpty()) {
                    // Split each entry into name and time
                    String[] parts = entry.split("\\|");
                    if (parts.length == 2) {
                        String playerName = parts[0];
                        int timeLeft = Integer.parseInt(parts[1]);
                        leaderboard.add(new LeaderboardEntry(playerName, timeLeft));
                    }
                }
            }
        }

        return leaderboard;
    }

    private void addPlayerEntry(String playerName, int timeLeft) {
        // Create a new leaderboard entry
        LinearLayout entryLayout = new LinearLayout(this);
        entryLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView playerNameTextView = new TextView(this);
        playerNameTextView.setText(playerName);
        playerNameTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView timeLeftTextView = new TextView(this);
        timeLeftTextView.setText(timeLeft + "s");
        timeLeftTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        entryLayout.addView(playerNameTextView);
        entryLayout.addView(timeLeftTextView);

        leaderboardEntriesContainer.addView(entryLayout);
    }

    // Inner class to represent a leaderboard entry
    private static class LeaderboardEntry {
        String playerName;
        int timeLeft;

        LeaderboardEntry(String playerName, int timeLeft) {
            this.playerName = playerName;
            this.timeLeft = timeLeft;
        }
    }
}
