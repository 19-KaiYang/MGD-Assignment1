package com.example.dx1221_week3.main.dx1221_week3;


import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;

import mgp2d.core.GameScene;
import mgp2d.core.GameActivity;

public class NonRecyclableObject extends Item {
    private float velocityY = 0;  // Vertical velocity
    private final float gravity = 500; // Gravity (pixels per second squared)
    private boolean isOnPlatform = false; // Tracks if the object is on a platform

    public NonRecyclableObject(float x, float y, Bitmap image, float desiredWidth, float desiredHeight, float weight) {
        super(x, y, Bitmap.createScaledBitmap(image, (int) desiredWidth, (int) desiredHeight, true), weight);
        this.width = desiredWidth;
        this.height = desiredHeight;
    }

    @Override
    public void onUpdate(float dt) {
        // Apply gravity if not on a platform
        if (!isOnPlatform) {
            velocityY += gravity * dt; // Accelerate downward
        }

        // Update position based on velocity
        _position.y += velocityY * dt;

        // Reset platform state
        isOnPlatform = false;

        // Check collision with platforms
        for (Platform platform : ((MainGameScene) GameScene.getCurrent()).getPlatforms()) {
            if (checkCollisionWithPlatform(platform)) {
                // Handle collision with the platform
                if (velocityY > 0 && (_position.y + getHeight()) >= platform.getY()) {
                    _position.y = platform.getY() - getHeight(); // Align with platform
                    velocityY = 0; // Stop vertical motion
                    isOnPlatform = true; // Mark as on a platform
                    break;
                }
            }
        }

        // Prevent the object from falling below the ground
        int screenHeight = GameActivity.instance.getResources().getDisplayMetrics().heightPixels;
        float groundLevel = screenHeight - getHeight();

        if (_position.y > groundLevel) {
            _position.y = groundLevel; // Align to ground level
            velocityY = 0; // Stop vertical motion
            isOnPlatform = true; // Mark as on the ground
        }
    }

    @Override
    public void onRender(Canvas canvas) {
        if (!isPickedUp) {
            // Render the item directly at its world position
            canvas.drawBitmap(itemImage, _position.x, _position.y, null);

            // Draw weight text above the object
            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE); // Text color
            textPaint.setTextSize(30); // Text size
            textPaint.setTextAlign(Paint.Align.CENTER); // Center the text

            // Calculate the position for the weight text
            float textX = _position.x + (width / 2f); // Centered horizontally
            float textY = _position.y - 10; // Slightly above the object

            // Draw the weight as text
            canvas.drawText(weight + " kg", textX, textY, textPaint);
        }
    }

    private boolean checkCollisionWithPlatform(Platform platform) {
        float tolerance = 5.0f; // Small buffer to prevent floating
        float objectBottom = _position.y + getHeight();
        float objectTop = _position.y;
        float objectLeft = _position.x;
        float objectRight = _position.x + getWidth();

        float platformTop = platform.getY();
        float platformBottom = platform.getY() + platform.getHeight();
        float platformLeft = platform.getX();
        float platformRight = platform.getX() + platform.getWidth();

        return objectBottom + tolerance >= platformTop
                && objectTop <= platformBottom
                && objectRight > platformLeft
                && objectLeft < platformRight;
    }
}