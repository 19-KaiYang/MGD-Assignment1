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

    private final List<TrashBin> trashBins = new ArrayList<>();
    private Bitmap _backgroundBitmap0;
    private Bitmap _backgroundBitmap1;
    private float cameraX = 0; // Horizontal camera offset
    private int screenWidth, screenHeight;
    private float totalWorldWidth; // Game world width
    private PlayerEntity player;
    private Joystick joystick;
    private final List<Platform> platforms = new ArrayList<>();

    //For Jumping

    private boolean isJumpButtonPressed = false;
    private float jumpButtonX, jumpButtonY, jumpButtonRadius;

    //Pick Up
    private float pickUpButtonX, pickUpButtonY, pickUpButtonRadius;
    private boolean isPickUpButtonPressed = false; // Tracks the previous state of the button
    private boolean wasPickUpButtonPressed = false;


    //Inventory
    private TrashBin inventoryItem = null;


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

        // Initialize TrashBin
        trashBins.add(new TrashBin(screenWidth / 2f, screenHeight - 420, R.drawable.trashbin, 100, 150)); // Width: 100, Height: 150


        // Initialize joystick with stickiness
        joystick = new Joystick(screenWidth / 8f, screenHeight * 4f / 5.5f, 150, 75, true); // The `true` enables sticky mode

        // Add platforms
        platforms.add(new Platform(0, screenHeight - 200, screenWidth, 100));
        platforms.add(new Platform(screenWidth / 2f, screenHeight - 400, 300, 40)); // Floating platform

        // Initialize jump button
        jumpButtonRadius = 100;
        jumpButtonX = screenWidth - jumpButtonRadius - 150; // Right corner of the screen
        jumpButtonY = screenHeight - jumpButtonRadius - 130;


        // Define the position and size of the pick-up button
        pickUpButtonRadius = 100;
        pickUpButtonX = screenWidth - pickUpButtonRadius - 500; // Adjust X position
        pickUpButtonY = screenHeight - pickUpButtonRadius - 130; //
    }

    @Override
    public void onUpdate(float dt) {
        MotionEvent event = GameActivity.instance.getMotionEvent();
        if (event != null) {
            int action = event.getActionMasked();
            float touchX = event.getX();
            float touchY = event.getY();

            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                // Handle jump button
                if (Math.hypot(touchX - jumpButtonX, touchY - jumpButtonY) <= jumpButtonRadius) {
                    isJumpButtonPressed = true;
                }

                // Handle pick-up/drop button
                if (Math.hypot(touchX - pickUpButtonX, touchY - pickUpButtonY) <= pickUpButtonRadius) {
                    isPickUpButtonPressed = true;
                }

                // Handle joystick
                if (Math.hypot(touchX - joystick.getCenterX(), touchY - joystick.getCenterY()) <= joystick.getBaseRadius()) {
                    joystick.setTouched(true);
                    joystick.update(touchX, touchY);
                }
            } else if (action == MotionEvent.ACTION_UP) {
                // Reset jump button
                if (Math.hypot(touchX - jumpButtonX, touchY - jumpButtonY) <= jumpButtonRadius) {
                    isJumpButtonPressed = false;
                }

                // Reset pick-up/drop button
                if (Math.hypot(touchX - pickUpButtonX, touchY - pickUpButtonY) <= pickUpButtonRadius) {
                    isPickUpButtonPressed = false;
                }

                // Handle joystick release
                if (joystick.isTouched()) {
                    joystick.setTouched(false); // Ensure touch state is cleared
                    joystick.reset();           // Reset joystick position
                }
            }
        }

        // Handle pick-up or drop logic (trigger only once per press)
        if (isPickUpButtonPressed && !wasPickUpButtonPressed) {
            handlePickUpOrDrop(); // Execute the logic
        }

        // Update the button press state
        wasPickUpButtonPressed = isPickUpButtonPressed;

        // Handle jumping
        if (isJumpButtonPressed && player.isOnPlatform()) {
            player.jump();
            isJumpButtonPressed = false; // Reset jump button
        }

        // Handle joystick movement
        if (joystick.isTouched() || joystick.isSticky()) {
            float deltaX = joystick.getHorizontalPercentage() * 300 * dt; // Adjust speed
            player.setPositionX(player.getPositionX() + deltaX);
        }

        // Update camera and other game logic
        float playerX = player.getPositionX();
        cameraX = playerX - screenWidth / 2f;
        cameraX = Math.max(0, Math.min(cameraX, totalWorldWidth - screenWidth));

        for (GameEntity entity : _gameEntities) {
            entity.onUpdate(dt);
        }

        for (Platform platform : platforms) {
            platform.onUpdate(dt);
        }

        for (TrashBin trashBin : trashBins) {
            trashBin.onUpdate(dt);
        }
    }


    @Override
    public void onRender(Canvas canvas) {
        // Render background and game objects
        canvas.save();
        canvas.translate(-cameraX, 0);

        canvas.drawBitmap(_backgroundBitmap0, 0, 0, null);
        canvas.drawBitmap(_backgroundBitmap1, _backgroundBitmap0.getWidth(), 0, null);

        for (Platform platform : platforms) {
            platform.onRender(canvas);
        }

        for (GameEntity entity : _gameEntities) {
            entity.onRender(canvas);
        }

        for (TrashBin trashBin : trashBins) {
            trashBin.onRender(canvas);
        }

        canvas.restore();

        // Render joystick
        Paint basePaint = new Paint();
        basePaint.setColor(Color.GRAY);
        basePaint.setStyle(Paint.Style.FILL);

        Paint hatPaint = new Paint();
        hatPaint.setColor(Color.BLUE);
        hatPaint.setStyle(Paint.Style.FILL);

        joystick.draw(canvas, basePaint, hatPaint);

        // Render jump button
        Paint jumpButtonPaint = new Paint();
        jumpButtonPaint.setColor(Color.RED);
        jumpButtonPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(jumpButtonX, jumpButtonY, jumpButtonRadius, jumpButtonPaint);

        // Render pick-up/drop button
        Paint pickUpButtonPaint = new Paint();
        if (inventoryItem == null) {
            pickUpButtonPaint.setColor(Color.GREEN); // Green for pick-up
        } else {
            pickUpButtonPaint.setColor(Color.RED); // Red for drop
        }
        pickUpButtonPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(pickUpButtonX, pickUpButtonY, pickUpButtonRadius, pickUpButtonPaint);
    }


    public List<Platform> getPlatforms() {
        return platforms;
    }

    public Joystick getJoystick() {
        return joystick;
    }

    private void handlePickUpOrDrop() {
        if (inventoryItem == null) {
            // Inventory is empty: Pick up a trash bin
            for (TrashBin trashBin : trashBins) {
                if (!trashBin.isPickedUp() && checkCollision(player, trashBin)) {
                    trashBin.pickUp(); // Mark as picked up
                    inventoryItem = trashBin; // Store in inventory
                    break; // Only pick up one trash bin
                }
            }
        } else {
            // Inventory is full: Drop the current item
            inventoryItem.drop(player.getPositionX(), player.getPositionY() + 100); // Drop near the player
            inventoryItem = null; // Clear the inventory
        }
    }



    private boolean checkCollision(PlayerEntity player, TrashBin trashBin) {
        float playerLeft = player.getPositionX();
        float playerRight = player.getPositionX() + player.getWidth();
        float playerTop = player.getPositionY();
        float playerBottom = player.getPositionY() + player.getHeight();

        float trashBinLeft = trashBin.getX();
        float trashBinRight = trashBin.getX() + trashBin.getWidth();
        float trashBinTop = trashBin.getY();
        float trashBinBottom = trashBin.getY() + trashBin.getHeight();

        return playerRight > trashBinLeft && playerLeft < trashBinRight &&
                playerBottom > trashBinTop && playerTop < trashBinBottom;
    }


}
