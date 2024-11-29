package com.example.dx1221_week3.main.dx1221_week3;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Joystick {
    private final float centerX, centerY;
    private final float baseRadius, hatRadius;
    private float joystickX, joystickY;
    private boolean isTouched = false;



    private float lastHorizontalPercentage = 0f;
    private float lastVerticalPercentage = 0f;
    private boolean isSticky = false;


    public Joystick(float centerX, float centerY, float baseRadius, float hatRadius, boolean isSticky) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.baseRadius = baseRadius;
        this.hatRadius = hatRadius;
        this.joystickX = centerX;
        this.joystickY = centerY;
        this.isSticky = isSticky;
    }


    public void draw(Canvas canvas, Paint basePaint, Paint hatPaint) {
        canvas.drawCircle(centerX, centerY, baseRadius, basePaint);
        canvas.drawCircle(joystickX, joystickY, hatRadius, hatPaint);
    }

    public void update(float touchX, float touchY) {
        float dx = touchX - centerX;
        float dy = touchY - centerY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > baseRadius) {
            dx *= baseRadius / distance;
            dy *= baseRadius / distance;
        }

        joystickX = centerX + dx;
        joystickY = centerY + dy;

        lastHorizontalPercentage = dx / baseRadius;
        lastVerticalPercentage = dy / baseRadius;
    }

    public void reset() {
        joystickX = centerX;
        joystickY = centerY;

        if (!isSticky) {
            lastHorizontalPercentage = 0f;
            lastVerticalPercentage = 0f;
        }
        isTouched = false; // Ensure touch state is cleared
    }


    public float getHorizontalPercentage() {
        return (joystickX - centerX) / baseRadius;
    }

    public float getVerticalPercentage() {
        return (joystickY - centerY) / baseRadius;
    }

    public void setTouched(boolean touched) {
        this.isTouched = touched;
    }

    public boolean isTouched() {
        return isTouched;
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public double getBaseRadius() {
        return baseRadius;
    }

    public boolean isSticky() {
        return isSticky;
    }

}
