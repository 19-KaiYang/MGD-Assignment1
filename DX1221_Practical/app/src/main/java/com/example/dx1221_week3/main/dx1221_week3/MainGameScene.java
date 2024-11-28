package com.example.dx1221_week3.main.dx1221_week3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.view.MotionEvent;

import com.example.dx1221_week3.R;

import java.util.Vector;

import mgp2d.core.GameActivity;
import mgp2d.core.GameEntity;
import mgp2d.core.GameScene;

public class MainGameScene extends GameScene {

    private Vector<GameEntity> _gameEntities = new Vector<>();
    private Bitmap _backgroundBitmap0;
    private Bitmap _backgroundBitmap1;
    private float _backgroundPosition;
    private int screenWidth;

    private Joystick joystick;

    @Override
    public void onCreate() {
        super.onCreate();
        int screenHeight = GameActivity.instance.getResources().getDisplayMetrics().heightPixels;
        screenWidth = GameActivity.instance.getResources().getDisplayMetrics().widthPixels;

        Bitmap bmp = BitmapFactory.decodeResource(GameActivity.instance.getResources(), R.drawable.gamescene);
        _backgroundBitmap0 = Bitmap.createScaledBitmap(bmp, screenWidth, screenHeight, true);
        _backgroundBitmap1 = Bitmap.createScaledBitmap(bmp, screenWidth, screenHeight, true);

        joystick = new Joystick(screenWidth / 6f, screenHeight * 4f / 5f, 150, 75);

        _gameEntities.add(new PlayerEntity());

        MediaPlayer _backgroundMusic = MediaPlayer.create(GameActivity.instance.getApplicationContext(), R.raw.shinytech);
        _backgroundMusic.setLooping(true);
        _backgroundMusic.start();
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

        for (GameEntity entity : _gameEntities) {
            entity.onUpdate(dt);
        }
    }

    @Override
    public void onRender(Canvas canvas) {
        Paint basePaint = new Paint();
        basePaint.setColor(Color.GRAY);
        basePaint.setStyle(Paint.Style.FILL);

        Paint hatPaint = new Paint();
        hatPaint.setColor(Color.BLUE);
        hatPaint.setStyle(Paint.Style.FILL);

        joystick.draw(canvas, basePaint, hatPaint);

        for (GameEntity entity : _gameEntities) {
            entity.onRender(canvas);
        }
    }

    public Joystick getJoystick() {
        return joystick;
    }
}
