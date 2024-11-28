package com.example.dx1221_week3.main.dx1221_week3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.view.MotionEvent;

import com.example.dx1221_week3.R;

import java.util.List;
import java.util.ArrayList;

import mgp2d.core.GameActivity;
import mgp2d.core.GameEntity;
import mgp2d.core.GameScene;

public class MainGameScene extends GameScene {

    private List<GameEntity> _gameEntities = new ArrayList<>();
    private Bitmap _backgroundBitmap0;
    private Bitmap _backgroundBitmap1;
    private float cameraX = 0; // Horizontal camera offset
    private int screenWidth, screenHeight;
    private float totalWorldWidth; // Game world width
    private PlayerEntity player;
    private Joystick joystick;
    private final List<Platform> platforms = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        screenHeight = GameActivity.instance.getResources().getDisplayMetrics().heightPixels;
        screenWidth = GameActivity.instance.getResources().getDisplayMetrics().widthPixels;

        // Define world size
        totalWorldWidth = screenWidth * 3f; // Example: Game world is three screens wide

        // Load background
        Bitmap bmp = BitmapFactory.decodeResource(GameActivity.instance.getResources(), R.drawable.gamescene);
        _backgroundBitmap0 = Bitmap.createScaledBitmap(bmp, screenWidth, screenHeight, true);
        _backgroundBitmap1 = Bitmap.createScaledBitmap(bmp, screenWidth, screenHeight, true);

        // Initialize player
        player = new PlayerEntity();
        _gameEntities.add(player);

        // Initialize joystick
        joystick = new Joystick(screenWidth / 8f, screenHeight * 4f / 5.5f, 150, 75);

        // Add platforms
        platforms.add(new Platform(0, screenHeight - 200, screenWidth, 100)); // Ground platform
        platforms.add(new Platform(screenWidth / 2f, screenHeight - 400, 300, 40)); // Floating platform
    }

    @Override
    public void onUpdate(float dt) {
        MotionEvent event = GameActivity.instance.getMotionEvent();
        if (event != null) {
            int action = event.getActionMasked();
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                float touchX = event.getX();
                float touchY = event.getY();
                if (joystick.isTouched() || Math.hypot(touchX - joystick.getCenterX(), touchY - joystick.getCenterY()) <= joystick.getBaseRadius()) {
                    joystick.setTouched(true);
                    joystick.update(touchX, touchY);
                } else {
                    joystick.setTouched(false);
                }
            } else if (action == MotionEvent.ACTION_UP) {
                joystick.reset();
            }
        }

        // Update camera to follow the player horizontally
        float playerX = player.getPositionX();
        cameraX = playerX - screenWidth / 2f;

        // Clamp camera position to the world bounds
        cameraX = Math.max(0, Math.min(cameraX, totalWorldWidth - screenWidth));

        // Update all entities
        for (GameEntity entity : _gameEntities) {
            entity.onUpdate(dt);
        }

        // Update platforms
        for (Platform platform : platforms) {
            platform.onUpdate(dt);
        }
    }

    @Override
    public void onRender(Canvas canvas) {
        // Translate canvas to reflect camera position
        canvas.save();
        canvas.translate(-cameraX, 0);

        // Render background
        canvas.drawBitmap(_backgroundBitmap0, 0, 0, null);
        canvas.drawBitmap(_backgroundBitmap1, _backgroundBitmap0.getWidth(), 0, null);

        // Render platforms
        for (Platform platform : platforms) {
            platform.onRender(canvas);
        }

        // Render all entities (e.g., player)
        for (GameEntity entity : _gameEntities) {
            entity.onRender(canvas);
        }

        canvas.restore();

        // Render joystick (fixed to screen position)
        Paint basePaint = new Paint();
        basePaint.setColor(Color.GRAY);
        basePaint.setStyle(Paint.Style.FILL);

        Paint hatPaint = new Paint();
        hatPaint.setColor(Color.BLUE);
        hatPaint.setStyle(Paint.Style.FILL);

        joystick.draw(canvas, basePaint, hatPaint);
    }

    public Joystick getJoystick() {
        return joystick;
    }
}
