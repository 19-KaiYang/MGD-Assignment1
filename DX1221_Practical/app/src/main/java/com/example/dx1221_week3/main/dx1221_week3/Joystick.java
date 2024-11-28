package com.example.dx1221_week3.main.dx1221_week3;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Joystick {
    private final float centerX, centerY;
    private final float baseRadius, hatRadius;
    private float joystickX, joystickY;
    private boolean isTouched = false;

    public Joystick(float centerX, float centerY, float baseRadius, float hatRadius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.baseRadius = baseRadius;
        this.hatRadius = hatRadius;
        this.joystickX = centerX;
        this.joystickY = centerY;
    }

    public void draw(Canvas canvas, Paint basePaint, Paint hatPaint) {
        canvas.drawCircle(centerX, centerY, baseRadius, basePaint);
        canvas.drawCircle(joystickX, joystickY, hatRadius, hatPaint);
    }

    public void update(float touchX, float touchY) {
        float dx = touchX - centerX;
        float dy = touchY - centerY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance < baseRadius) {
            joystickX = touchX;
            joystickY = touchY;
        } else {
            joystickX = centerX + dx / distance * baseRadius;
            joystickY = centerY + dy / distance * baseRadius;
        }
    }

    public void reset() {
        joystickX = centerX;
        joystickY = centerY;
        isTouched = false;
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
}
