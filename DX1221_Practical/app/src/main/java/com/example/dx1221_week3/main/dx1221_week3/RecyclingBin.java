package com.example.dx1221_week3.main.dx1221_week3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;

import mgp2d.core.GameActivity;
import mgp2d.core.GameEntity;
import mgp2d.core.GameScene;

public class RecyclingBin extends TrashBin {

    private float velocityY = 0;  // Vertical velocity
    private final float gravity = 500; // Gravity (pixels per second squared)
    private boolean isOnPlatform = false; // Tracks if the trash bin is on a platform
    private float currentWeight = 0;

    public RecyclingBin(float x, float y, int resId, float desiredWidth, float desiredHeight, float weight) {
        super(x, y, resId, desiredWidth, desiredHeight, weight); // Pass the int resource ID
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

        // Prevent the trash bin from falling below the ground
        int screenHeight = GameActivity.instance.getResources().getDisplayMetrics().heightPixels;
        float groundLevel = screenHeight - getHeight();

        if (_position.y > groundLevel) {
            _position.y = groundLevel; // Align to ground level
            velocityY = 0; // Stop vertical movement
            isOnPlatform = true; // Mark as on the ground
        }
    }

    @Override
    public void onRender(Canvas canvas) {
        if (!isPickedUp) {
            // Draw the trash bin image
            canvas.drawBitmap(itemImage, _position.x, _position.y, null);

            // Draw the weight text above the trash bin
            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(30);
            canvas.drawText(currentWeight + " kg", _position.x, _position.y - 10, textPaint); // Display weight
        }
    }


    private boolean checkCollisionWithPlatform(Platform platform) {
        float tolerance = 5.0f; // Small buffer to prevent floating
        float trashBinBottom = _position.y + getHeight();
        float trashBinTop = _position.y;
        float trashBinLeft = _position.x;
        float trashBinRight = _position.x + getWidth();

        float platformTop = platform.getY();
        float platformBottom = platform.getY() + platform.getHeight();
        float platformLeft = platform.getX();
        float platformRight = platform.getX() + platform.getWidth();

        return trashBinBottom + tolerance >= platformTop
                && trashBinTop <= platformBottom
                && trashBinRight > platformLeft
                && trashBinLeft < platformRight;
    }

    public boolean isOnPlatform() {
        return isOnPlatform;
    }

    public void addItem(Item item) {
        currentWeight += item.getWeight(); // Add item's weight regardless of correctness
        if (!(item instanceof RecyclableObject)) {
            // Wrong item placed in recycling bin
            ((MainGameScene) GameScene.getCurrent()).loseLife();
        }
        item.setTrashed(true); // Mark the item as trashed
    }



    public float getCurrentWeight() {
        return currentWeight;
    }

    @Override
    public void pickUp() {
        isPickedUp = true; // Mark the trash bin as picked up
    }

    @Override
    public void drop(float x, float y) {
        _position.x = x;
        _position.y = y;
        isPickedUp = false; // Mark the trash bin as dropped
    }


}
