package com.example.dx1221_week3.main.dx1221_week3;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;

import mgp2d.core.GameEntity;
import mgp2d.core.GameScene;


public abstract class Item extends GameEntity {
    protected Bitmap itemImage; // Item's visual representation
    protected float width, height; // Item's dimensions
    protected boolean isPickedUp = false; // Pick-up state

    public abstract void onRender(Canvas canvas);

    public Item(float x, float y, Bitmap image) {
        _position.x = x;
        _position.y = y;
        this.itemImage = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    // Methods common to all items
    public boolean isPickedUp() {
        return isPickedUp;
    }

    public void pickUp() {
        isPickedUp = true;
    }

    public void drop(float x, float y) {
        _position.x = x;
        _position.y = y;
        isPickedUp = false;
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

    public Bitmap getIcon() {
        return itemImage; // Return the item's image as its icon
    }



}
