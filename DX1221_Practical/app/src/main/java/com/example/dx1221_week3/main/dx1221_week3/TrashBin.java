package com.example.dx1221_week3.main.dx1221_week3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import mgp2d.core.GameActivity;
import mgp2d.core.GameEntity;
import mgp2d.core.GameScene;


public class TrashBin extends GameEntity {
    private Bitmap trashBinImage;
    private float width, height;

    private float velocityY = 0;  // Vertical velocity
    private final float gravity = 500; // Gravity (pixels per second squared)
    private boolean isOnPlatform = false; // Tracks if the trash bin is on a platform

    private boolean isPickedUp = false;



    public TrashBin(float x, float y, int resId, float desiredWidth, float desiredHeight) {
        _position.x = x;
        _position.y = y;

        // Load the trash bin image
        Bitmap bmp = BitmapFactory.decodeResource(GameActivity.instance.getResources(), resId);

        // Scale the image to the desired size
        trashBinImage = Bitmap.createScaledBitmap(bmp, (int) desiredWidth, (int) desiredHeight, true);

        // Update width and height
        width = desiredWidth;
        height = desiredHeight;
    }

    public void jump() {
        if (isOnPlatform) {
            velocityY = -500; // Set upward velocity for the jump
            isOnPlatform = false; // The trash bin leaves the platform
        }
    }


    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getX() {
        return _position.x;
    }

    public float getY() {
        return _position.y;
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
            canvas.drawBitmap(trashBinImage, _position.x, _position.y, null);

            // Optional: Draw debug box for collision testing
            Paint debugPaint = new Paint();
            debugPaint.setColor(0xFFFF0000); // Red color for the bounding box
            debugPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(_position.x, _position.y, _position.x + width, _position.y + height, debugPaint);
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

    public boolean isPickedUp() {
        return isPickedUp;
    }

    public void pickUp() {
        isPickedUp = true; // Mark the trash bin as picked up
    }

    public void drop(float x, float y) {
        _position.x = x; // Set the drop position
        _position.y = y;
        isPickedUp = false; // Mark as no longer picked up
    }


}
