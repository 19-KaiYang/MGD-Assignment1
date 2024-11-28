package com.example.dx1221_week3.main.dx1221_week3;

import android.graphics.Canvas;
import android.graphics.Paint;

import mgp2d.core.GameEntity;

public class Platform extends GameEntity {
    private final float width, height;

    public Platform(float x, float y, float width, float height) {
        _position.x = x;
        _position.y = y;
        this.width = width;
        this.height = height;
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
    public void onUpdate(float dt) {}

    @Override
    public void onRender(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(0xFF00FF00);
        canvas.drawRect(_position.x, _position.y, _position.x + width, _position.y + height, paint);
    }
}
