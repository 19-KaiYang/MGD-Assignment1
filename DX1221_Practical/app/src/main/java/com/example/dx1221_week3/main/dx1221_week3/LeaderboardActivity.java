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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LeaderboardActivity extends Activity implements View.OnClickListener {

    private static final String PREFERENCES_NAME = "LeaderboardPrefs";
    private static final String PLAYER_NAMES_KEY = "PlayerNames";
    private static final String PLAYER_TIMES_KEY = "PlayerTimes";

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

        // Debug logging
        android.util.Log.d("MyTag", "Received Player Name: " + playerName);
        android.util.Log.d("MyTag", "Received Time Left: " + timeLeft);

        // Validate data and add to leaderboard
        if (playerName != null && timeLeft != -1) {
            savePlayerData(playerName, timeLeft);
        } else {
            android.util.Log.d("MyTag", "Intent Extras Missing: PLAYER_NAME or TIME_LEFT is invalid.");
        }

        // Load leaderboard data and display it
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
        SharedPreferences.Editor editor = prefs.edit();

        // Get existing data
        Set<String> playerNames = prefs.getStringSet(PLAYER_NAMES_KEY, new HashSet<>());
        Set<String> playerTimes = prefs.getStringSet(PLAYER_TIMES_KEY, new HashSet<>());

        // Add new data
        playerNames.add(playerName);
        playerTimes.add(String.valueOf(timeLeft));

        // Save back to SharedPreferences
        editor.putStringSet(PLAYER_NAMES_KEY, playerNames);
        editor.putStringSet(PLAYER_TIMES_KEY, playerTimes);
        editor.apply();

        android.util.Log.d("MyTag", "Saved Player Data: Name=" + playerName + ", Time=" + timeLeft);
    }

    private void loadLeaderboardData() {
        SharedPreferences prefs = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        Set<String> playerNames = prefs.getStringSet(PLAYER_NAMES_KEY, new HashSet<>());
        Set<String> playerTimes = prefs.getStringSet(PLAYER_TIMES_KEY, new HashSet<>());

        List<String> namesList = new ArrayList<>(playerNames);
        List<String> timesList = new ArrayList<>(playerTimes);

        for (int i = 0; i < namesList.size(); i++) {
            String name = namesList.get(i);
            String time = timesList.size() > i ? timesList.get(i) : "0";
            addPlayerEntry(name, Integer.parseInt(time));
        }

        android.util.Log.d("MyTag", "Loaded Leaderboard Data: " + namesList + " " + timesList);
    }

    private void addPlayerEntry(String playerName, int timeLeft) {
        // Create a leaderboard entry
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

        android.util.Log.d("MyTag", "Added Player Entry: " + playerName + ", Time: " + timeLeft);
    }
}

