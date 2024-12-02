package com.example.dx1221_week3.main.dx1221_week3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.dx1221_week3.R;

import extra.AnimatedSprite;
import mgp2d.core.GameActivity;
import mgp2d.core.GameEntity;
import mgp2d.core.GameScene;

public class PlayerEntity extends GameEntity {

    private final AnimatedSprite _animatedSprite;
    private float velocityY = 0;

    private float velocityX = 0;
    private final float gravity = 500;
    private final float jumpVelocity = -500;
    private boolean isOnPlatform = false;

    //private float collisionOffsetX = -3; //  (left/right)
    //private float collisionOffsetY = 10;  //  (top/bottom)

    private float baseSpeed = 300f;
    private float currentSpeed = baseSpeed;

    public void updateSpeedBasedOnWeight(float weight) {
        // Reduce speed by 1% for every 1kg of weight (example logic)
        currentSpeed = baseSpeed * Math.max(0.5f, 1.0f - (weight / 10.0f));
    }

    public float getCurrentSpeed() {
        return currentSpeed;
    }


    public PlayerEntity() {
        _position.x = GameActivity.instance.getResources().getDisplayMetrics().widthPixels / 4f;
        _position.y = GameActivity.instance.getResources().getDisplayMetrics().heightPixels / 2f;

        Bitmap bmp = BitmapFactory.decodeResource(GameActivity.instance.getResources(), R.drawable.runfrog);
        Bitmap spriteSheet = Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth() * 1.5f), (int) (bmp.getHeight() * 1.5f), true);

        _animatedSprite = new AnimatedSprite(spriteSheet, 1, 12, 24);
    }

    public void jump() {
        if (isOnPlatform) {
            velocityY = jumpVelocity;
            isOnPlatform = false;
        }
    }

    @Override
    public void onUpdate(float dt) {
        // Horizontal movement
        Joystick joystick = ((MainGameScene) GameScene.getCurrent()).getJoystick();
        if (joystick.isTouched()) {
            // Calculate deltaX based on joystick input
            float deltaX = joystick.getHorizontalPercentage() * 300 * dt;

            // Update position
            _position.x += deltaX;

            // Calculate velocityX (remove dt since deltaX already factors it in)
            velocityX = joystick.getHorizontalPercentage() * 300;
        } else {
            // No joystick input, velocity is zero
            velocityX = 0;
        }

        // Apply gravity
        if (!isOnPlatform) {
            velocityY += gravity * dt; // Accelerate downward
        }
        _position.y += velocityY * dt; // Update vertical position

        // Reset platform state
        isOnPlatform = false;

        // Platform collision detection
        for (Platform platform : ((MainGameScene) GameScene.getCurrent()).getPlatforms()) {
            if (checkCollisionWithPlatform(platform)) {
                // Y-Axis Collision (Vertical)
                if (velocityY > 0 && (_position.y + getHeight()) >= platform.getY()) {
                    // Falling onto a platform
                    _position.y = platform.getY() - 62;
                    velocityY = 0;
                    isOnPlatform = true;
                }
            }
        }
        // Prevent falling below the ground
        int screenHeight = GameActivity.instance.getResources().getDisplayMetrics().heightPixels;
        float groundLevel = screenHeight - getHeight(); // Ground level

        if (_position.y > groundLevel) {
            _position.y = groundLevel;
            velocityY = 0;
            isOnPlatform = true;
        }


        // Update animation frames
        _animatedSprite.update(dt);
    }



    @Override
    public void onRender(Canvas canvas) {
        _animatedSprite.render(canvas, (int) _position.x, (int) _position.y, null);

        Paint debugPaint = new Paint();
        debugPaint.setColor(Color.RED);
        debugPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(
                _position.x - (_animatedSprite.getWidth() / 1.8f),
                _position.y - (_animatedSprite.getHeight() / 1.8f),
                _position.x + (_animatedSprite.getWidth() /1.8f),
                _position.y + (_animatedSprite.getHeight() / 1.8f),
                debugPaint
        );

        Paint platformPaint = new Paint();
        platformPaint.setColor(Color.BLACK);
        platformPaint.setStyle(Paint.Style.STROKE);

        // Loop through all platforms in the current scene
        for (Platform platform : ((MainGameScene) GameScene.getCurrent()).getPlatforms()) {
            canvas.drawRect(
                    platform.getX(),
                    platform.getY(),
                    platform.getX() + platform.getWidth(),
                    platform.getY() + platform.getHeight(),
                    platformPaint
            );
        }
    }

    private boolean checkCollisionWithPlatform(Platform platform) {
        if (platform.isDisabled()) {
            return false;  // No collision with disabled platforms
        }

        float tolerance = 5.0f; // Tolerance value
        float playerLeft = _position.x - (getWidth() / 1.8f);
        float playerRight = _position.x + (getWidth() / 1.8f);
        float playerTop = _position.y - (getHeight() / 1.8f);
        float playerBottom = _position.y + (getHeight() / 2.2f);

        return playerBottom + tolerance >= platform.getY()
                && playerTop <= platform.getY() + platform.getHeight()
                && playerRight > platform.getX()
                && playerLeft < platform.getX() + platform.getWidth();
    }

    public float getWidth() {
        return _animatedSprite.getWidth();
    }

    public float getHeight() {
        return _animatedSprite.getHeight();
    }

    public void setPositionX(float x) {
        _position.x = x;
    }


    public float getPositionX() {
        return _position.x;
    }

    public float getPositionY() {
        return _position.y;
    }

    public boolean isOnPlatform() {
        return isOnPlatform;
    }

}
