package com.example.dx1221_week3.main.dx1221_week3;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.example.dx1221_week3.R;

public class Options extends Activity implements View.OnClickListener {

    private Switch accelerometerSwitch;
    private boolean isAccelerometerEnabled;
    private SharedPreferences preferences;
    private static final String PREFS_NAME = "GamePreferences";
    private static final String ACCELEROMETER_KEY = "accelerometer_enabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options);


        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        isAccelerometerEnabled = preferences.getBoolean(ACCELEROMETER_KEY, false);

        // Initialize UI elements
        accelerometerSwitch = findViewById(R.id.accelerometerSwitch);
        Button backButton = findViewById(R.id.backButton);


        accelerometerSwitch.setChecked(isAccelerometerEnabled);


        accelerometerSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isAccelerometerEnabled = isChecked;


            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(ACCELEROMETER_KEY, isChecked);
            editor.apply();
        });


        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            Intent intent = new Intent(Options.this, MainMenu.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
