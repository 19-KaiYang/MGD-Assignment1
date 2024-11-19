package com.example.dx1221_week3.main.dx1221_week3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.example.dx1221_week3.R;

import extra.AnimatedSprite;
import mgp2d.core.GameActivity;
import mgp2d.core.GameEntity;

public class PlayerEntity extends GameEntity {

    private final AnimatedSprite _animatedSprite;

    public PlayerEntity() {
        _position.x = (float) GameActivity.instance.getResources().getDisplayMetrics().widthPixels / 2;
        _position.y = (float) GameActivity.instance.getResources().getDisplayMetrics().heightPixels / 2;

        Bitmap bmp = BitmapFactory.decodeResource(GameActivity.instance.getResources(), R.drawable.player_heli_body);
        Bitmap sprite = Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth() * 1.5f), (int) (bmp.getHeight() * 1.5f), true);

        _animatedSprite = new AnimatedSprite(sprite, 1, 7, 24);
    }
    //private boolean _isHolding = true;

    private int _currentPointerId = -1;
    @Override
    public void onUpdate(float dt)
    {
        super.onUpdate(dt);
        _animatedSprite.update(dt);

        MotionEvent motionEvent = GameActivity.instance.getMotionEvent();
        if (motionEvent == null) return;

        int action = motionEvent.getActionMasked();
        int actionIndex = motionEvent.getActionIndex();
        int pointerId = motionEvent.getPointerId(actionIndex);

        if(_currentPointerId == -1 &&
                (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN)){
            _currentPointerId = pointerId;
        }
        else if(_currentPointerId != -1 &&
                (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP)){
            _currentPointerId = -1;
        }

        if (_currentPointerId != -1)
        {
            for (int i = 0; i< motionEvent.getPointerCount(); i++)
            {
                if (motionEvent.getPointerId(i) != _currentPointerId) continue;

                _position.x = motionEvent.getX(i);
                _position.y = motionEvent.getY(i);
            }
        }
    }

    @Override
    public void onRender(Canvas canvas) {
        _animatedSprite.render(canvas, (int) _position.x, (int) _position.y, null);
    }
}
