package com.example.dx1221_week3.main.dx1221_week3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.example.dx1221_week3.R;

import extra.AnimatedSprite;
import mgp2d.core.GameActivity;
import mgp2d.core.GameEntity;
import mgp2d.core.GameScene;

public class PlayerEntity extends GameEntity {

    private final AnimatedSprite _animatedSprite;

    public PlayerEntity() {
        // Set initial position at the center of the screen
        _position.x = GameActivity.instance.getResources().getDisplayMetrics().widthPixels / 2f;
        _position.y = GameActivity.instance.getResources().getDisplayMetrics().heightPixels / 2f;

        // Load the sprite sheet and create the AnimatedSprite
        Bitmap bmp = BitmapFactory.decodeResource(GameActivity.instance.getResources(), R.drawable.player_heli_body);
        Bitmap spriteSheet = Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth() * 1.5f), (int) (bmp.getHeight() * 1.5f), true);

        _animatedSprite = new AnimatedSprite(spriteSheet, 1, 7, 24); // 1 row, 7 frames, 24 FPS
    }

    @Override
    public void onUpdate(float dt) {
        // Update joystick and movement logic
        Joystick joystick = ((MainGameScene) GameScene.getCurrent()).getJoystick();
        if (joystick.isTouched()) {
            float deltaX = joystick.getHorizontalPercentage() * 300 * dt;

            // Allow horizontal movement only (no clamping)
            _position.x += deltaX;
        }

        // Update animation frames
        _animatedSprite.update(dt);
    }

    @Override
    public void onRender(Canvas canvas) {
        // Render the animated sprite at the current position
        _animatedSprite.render(canvas, (int) _position.x, (int) _position.y, null);
    }

    public float getPositionX() {
        return _position.x;
    }

    public float getPositionY() {
        return _position.y;
    }
}
