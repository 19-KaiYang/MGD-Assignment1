package com.example.dx1221_week3.main.dx1221_week3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaPlayer;

import com.example.dx1221_week3.R;

import java.util.Vector;

import mgp2d.core.GameActivity;
import mgp2d.core.GameEntity;
import mgp2d.core.GameScene;

public class MainGameScene extends GameScene {

    Vector<GameEntity> _gameEntities = new Vector<>();
    private Bitmap _backgroundBitmap0;
    private Bitmap _backgroundBitmap1;
    private float _backgroundPosition;
    private int screenWidth;

    @Override
    public void onCreate() {
        super.onCreate();
        int screenHeight = GameActivity.instance.getResources().getDisplayMetrics().heightPixels;
        screenWidth = GameActivity.instance.getResources().getDisplayMetrics().widthPixels;
        Bitmap bmp = BitmapFactory.decodeResource(GameActivity.instance.getResources(), R.drawable.gamescene);
        _backgroundBitmap0 = Bitmap.createScaledBitmap(bmp, screenWidth, screenHeight, true);
        _backgroundBitmap1 = Bitmap.createScaledBitmap(bmp, screenWidth, screenHeight, true);

        //_gameEntities.add(new BackgroundEntity());
        _gameEntities.add(new PlayerEntity());

        MediaPlayer _backgroundMusic = MediaPlayer.create(GameActivity.instance.getApplicationContext(), R.raw.shinytech);
        _backgroundMusic.setLooping(true);
        _backgroundMusic.start();
    }

    @Override
    public void onUpdate(float dt) {
        _backgroundPosition = (_backgroundPosition - dt * 500f) % (float)screenWidth;

        for (GameEntity entity : _gameEntities)
            entity.onUpdate(dt);
    }

    @Override
    public void onRender(Canvas canvas) {
        canvas.drawBitmap(_backgroundBitmap0, _backgroundPosition, 0, null);
        canvas.drawBitmap(_backgroundBitmap1, _backgroundPosition + screenWidth, 0, null);

        for (GameEntity entity : _gameEntities)
            entity.onRender(canvas);
    }
}
