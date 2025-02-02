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

public class Leaderboard extends Activity implements View.OnClickListener {

    private static final String PREFERENCES_NAME = "LeaderboardPrefs";
    private static final String LEADERBOARD_KEY = "Leaderboard";

    private Button backButton;

    private Button clearButton;

    private LinearLayout leaderboardEntriesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard);


        backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(this);

        clearButton = findViewById(R.id.clear_btn);
        clearButton.setOnClickListener(this);

        // Initialize leaderboard container
        leaderboardEntriesContainer = findViewById(R.id.leaderboardEntriesContainer);

        // Retrieve data from intent
        Intent intent = getIntent();
        String playerName = intent.getStringExtra("PLAYER_NAME");
        int timeLeft = intent.getIntExtra("TIME_LEFT", -1);


        if (playerName != null && timeLeft >= 0) {
            savePlayerData(playerName, timeLeft);
        }

        // Load then display
        loadLeaderboardData();
    }

    @Override
    public void onClick(View v) {
        if (v == backButton) {

            Intent intent = new Intent(this, MainMenu.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else if (v == clearButton) {
            clearLeaderboard();
        }
    }

    private void savePlayerData(String playerName, int timeLeft) {
        SharedPreferences prefs = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String leaderboardData = prefs.getString(LEADERBOARD_KEY, "");


        String newEntry = playerName + "|" + timeLeft + ";";
        leaderboardData += newEntry;

        // Save to SharedPreferences
        prefs.edit().putString(LEADERBOARD_KEY, leaderboardData).apply();
    }

    private void loadLeaderboardData() {
        SharedPreferences prefs = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String leaderboardData = prefs.getString(LEADERBOARD_KEY, "");

        List<LeaderboardEntry> leaderboard = parseLeaderboardData(leaderboardData);

        // Clear existing entries in the UI
        leaderboardEntriesContainer.removeAllViews();


        for (int i = 0; i < leaderboard.size(); i++) {
            LeaderboardEntry entry = leaderboard.get(i);
            int rank = i + 1;
            addPlayerEntry(rank, entry.playerName, entry.timeLeft);
        }
    }

    private List<LeaderboardEntry> parseLeaderboardData(String data) {
        List<LeaderboardEntry> leaderboard = new ArrayList<>();

        if (!data.isEmpty()) {

            String[] entries = data.split(";");
            for (String entry : entries) {
                if (!entry.isEmpty()) {

                    String[] parts = entry.split("\\|");
                    if (parts.length == 2) {
                        String playerName = parts[0];
                        int timeLeft = Integer.parseInt(parts[1]);
                        leaderboard.add(new LeaderboardEntry(playerName, timeLeft));
                    }
                }
            }
        }

        // Sort the leaderboard by ascending order
        leaderboard.sort((entry1, entry2) -> Integer.compare(entry2.timeLeft, entry1.timeLeft));

        return leaderboard;
    }

    private void addPlayerEntry(int rank, String playerName, int timeLeft) {
        // Create a new leaderboard entry
        LinearLayout entryLayout = new LinearLayout(this);
        entryLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Number Placement
        TextView rankTextView = new TextView(this);
        rankTextView.setText(rank + ". ");
        rankTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // Player Name
        TextView playerNameTextView = new TextView(this);
        playerNameTextView.setText(playerName);
        playerNameTextView.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        // Time Left
        TextView timeLeftTextView = new TextView(this);
        timeLeftTextView.setText(timeLeft + "s");
        timeLeftTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));


        entryLayout.addView(rankTextView);
        entryLayout.addView(playerNameTextView);
        entryLayout.addView(timeLeftTextView);

        // Add the entry to the leaderboard container
        leaderboardEntriesContainer.addView(entryLayout);
    }

    private void clearLeaderboard() {
        SharedPreferences prefs = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(LEADERBOARD_KEY).apply();

        leaderboardEntriesContainer.removeAllViews();

    }


    private static class LeaderboardEntry {
        String playerName;
        int timeLeft;

        LeaderboardEntry(String playerName, int timeLeft) {
            this.playerName = playerName;
            this.timeLeft = timeLeft;
        }
    }


}
