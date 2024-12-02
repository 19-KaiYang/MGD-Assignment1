package com.example.dx1221_week3.main.dx1221_week3;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;

import mgp2d.core.GameEntity;

public class Platform extends GameEntity {
    private final float width, height;
    protected boolean isDisabled;  // New field to track if the platform is disabled

    public Platform(float x, float y, float width, float height, boolean isdisabled) {
        _position.x = x;
        _position.y = y;
        this.width = width;
        this.height = height;
        this.isDisabled = isdisabled;  // Initially, the platform is not disabled
    }

    public float getX() {
        return _position.x;
    }

    public float getY() {
        return _position.y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    // Method to disable the platform
    public void setDisabled(boolean disabled) {
        this.isDisabled = disabled;
    }

    // Method to check if the platform is disabled
    public boolean isDisabled() {
        return isDisabled;
    }

    @Override
    public void onUpdate(float dt) {
        // Update logic for platform (could be extended later)
    }

    @Override
    public void onRender(Canvas canvas) {
        // If the platform is disabled, do not render it, or render in a different way
        if (isDisabled) {
            return;  // If disabled, skip rendering
        }

        // Otherwise, render the platform
        Paint platformPaint = new Paint();
        platformPaint.setColor(Color.GREEN); // Green color for the platform
        canvas.drawRect(_position.x, _position.y, _position.x + width, _position.y + height, platformPaint);

        // Draw bounding box for collision debugging
        Paint boundingBoxPaint = new Paint();
        boundingBoxPaint.setColor(Color.RED); // Red color for collision box
        boundingBoxPaint.setStyle(Paint.Style.STROKE); // Outline only
        canvas.drawRect(_position.x, _position.y, _position.x + width, _position.y + height, boundingBoxPaint);
    }
}
