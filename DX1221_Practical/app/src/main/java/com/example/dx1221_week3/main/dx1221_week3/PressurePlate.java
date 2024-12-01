package com.example.dx1221_week3.main.dx1221_week3;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import mgp2d.core.GameActivity;
import mgp2d.core.GameEntity;
import mgp2d.core.GameScene;

public class PressurePlate extends GameEntity
{
    protected float width, height; // Item's dimensions

    protected float weightReq;

    public PressurePlate(float x, float y, float width, float height, float weightReq) {
        _position.x = x;
        _position.y = y;
        this.width = width;
        this.height = height;
        this.weightReq = weightReq;
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

    @Override
    public void onUpdate(float dt) {

    }

    @Override
    public void onRender(Canvas canvas) {
        Paint platformPaint = new Paint();
        platformPaint.setColor(Color.BLUE); // Green color for the platform
        canvas.drawRect(_position.x, _position.y, _position.x + width, _position.y + height, platformPaint);

        // Draw weight text above the object
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE); // Text color
        textPaint.setTextSize(30); // Text size
        textPaint.setTextAlign(Paint.Align.CENTER); // Center the text

        float textX = _position.x + (width / 2f); // Centered horizontally
        float textY = _position.y - 10; // Slightly above the object

        canvas.drawText(weightReq + " kg", textX, textY, textPaint);
    }
}
