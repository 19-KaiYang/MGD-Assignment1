package com.example.dx1221_week3.main.dx1221_week3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.util.Log;

import com.example.dx1221_week3.R;

import java.util.List;
import java.util.ArrayList;

import mgp2d.core.GameActivity;
import mgp2d.core.GameEntity;
import mgp2d.core.GameScene;

public class MainGameScene extends GameScene {

    private List<GameEntity> _gameEntities = new ArrayList<>();

    private final List<TrashBin> trashBins = new ArrayList<>();

    private final List<Item> items = new ArrayList<>();
    private Bitmap _backgroundBitmap0;
    private Bitmap _backgroundBitmap1;

    private Bitmap _winBitmap;
    private Bitmap _loseBitmap;

    private Boolean Win;

    private Boolean Lose;

    private float cameraX = 0;
    private int screenWidth, screenHeight;
    private float totalWorldWidth;
    private PlayerEntity player;
    private Joystick joystick;
    private final List<Platform> platforms = new ArrayList<>();
    private final List<PressurePlate> pressurePlates = new ArrayList<>();

    //For Jumping

    private boolean isJumpButtonPressed = false;
    private float jumpButtonX, jumpButtonY, jumpButtonRadius;

    //Pick Up
    private float pickUpButtonX, pickUpButtonY, pickUpButtonRadius;
    private boolean isPickUpButtonPressed = false;
    private boolean wasPickUpButtonPressed = false;


    //Inventory
    private Item inventoryItem = null;

    private Bitmap inventoryIcon = null; // Current inventory item icon
    private final float inventoryX = 50; // X position for inventory UI
    private final float inventoryY = 50; // Y position for inventory UI
    private final float inventoryWidth = 100;
    private final float inventoryHeight = 100;


    //Pointers
    private int joystickPointerId = -1;
    private int jumpButtonPointerId = -1;
    private int pickUpButtonPointerId = -1;

    //Lives
    private int lives = 3;

    //Timer
    private float timer = 30;
    private boolean isTimerRunning = true;

//    private boolean CollisionTest = false;
//    private float Test;

    @Override
    //On Start
    public void onCreate() {
        super.onCreate();

        Win = false;
        Lose = false;

        screenHeight = GameActivity.instance.getResources().getDisplayMetrics().heightPixels;
        screenWidth = GameActivity.instance.getResources().getDisplayMetrics().widthPixels;

        // Define world size
        totalWorldWidth = screenWidth * 2f;

        // Load background
        Bitmap bmp = BitmapFactory.decodeResource(GameActivity.instance.getResources(), R.drawable.gamescene);
        _backgroundBitmap0 = Bitmap.createScaledBitmap(bmp, screenWidth, screenHeight, true);
        _backgroundBitmap1 = Bitmap.createScaledBitmap(bmp, screenWidth, screenHeight, true);

        Bitmap win = BitmapFactory.decodeResource(GameActivity.instance.getResources(), R.drawable.win);
        _winBitmap = Bitmap.createScaledBitmap(win, screenWidth, screenHeight, true);

        Bitmap lose = BitmapFactory.decodeResource(GameActivity.instance.getResources(), R.drawable.lose);
        _loseBitmap = Bitmap.createScaledBitmap(lose, screenWidth, screenHeight, true);

        // Initialize player
        player = new PlayerEntity();
        _gameEntities.add(player);

        // Initialize TrashBin
        TrashBin trashBin = new TrashBin(500, screenHeight - 300, R.drawable.trashbin, 100, 150, 0);
        trashBins.add(trashBin);

        //Initialize RecyclingBin
        RecyclingBin recyclingBin = new RecyclingBin(1600, screenHeight - 300, R.drawable.recyclingbin, 100, 150, 0);
        trashBins.add(recyclingBin);


        // Initialize joystick with stickiness
        joystick = new Joystick(screenWidth / 8f, screenHeight * 4f / 5.5f, 150, 75, true); // True enables sticky mode

        // Add platforms
        platforms.add(new Platform(0, screenHeight - 200, screenWidth * 2, 100 , false));
        platforms.add(new Platform(screenWidth / 2f, screenHeight - 400, 300, 40, false)); // Floating platform
        platforms.add(new Platform(1700, screenHeight  - 600, 300, 40, true)); // PressurePlate1
        platforms.add(new Platform(2300, screenHeight  - 800, 1000, 40, false));

        //add Pressure Plates
        pressurePlates.add(new PressurePlate(screenWidth / 2f, screenHeight - 230, 100, 30, 20 , platforms.get(2)));


        // Initialize jump button
        jumpButtonRadius = 100;
        jumpButtonX = screenWidth - jumpButtonRadius - 150;
        jumpButtonY = screenHeight - jumpButtonRadius - 130;


        // Define the position and size of the pick-up button
        pickUpButtonRadius = 100;
        pickUpButtonX = screenWidth - pickUpButtonRadius - 500;
        pickUpButtonY = screenHeight - pickUpButtonRadius - 130;


        // Initialize Items (Recyclable and Non-Recyclable)
        // ADD ITEMS HERE
        // ALL RECYCLABLE AND NON RECYCLABLE
        Bitmap recyclableImage = BitmapFactory.decodeResource(GameActivity.instance.getResources(), R.drawable.bottle);
        Bitmap nonRecyclableImage = BitmapFactory.decodeResource(GameActivity.instance.getResources(), R.drawable.trashbag);

        items.add(new RecyclableObject(900, screenHeight - 500, recyclableImage, 100, 100, 10)); // Weight = 10kg
        items.add(new RecyclableObject(900, screenHeight - 500, recyclableImage, 100, 100, 10)); // Weight = 10kg
        items.add(new NonRecyclableObject(800, screenHeight - 500, nonRecyclableImage, 120, 120, 20)); // Weight = 20kg
        items.add(new NonRecyclableObject(2500, screenHeight  - 850, nonRecyclableImage, 120, 120, 20)); // Weight = 20kg
    }

    @Override
    public void onUpdate(float dt) {

        if (!Win || !Lose) {
            //Timer
            if (!Win) {
                if (isTimerRunning) {
                    timer -= dt;
                    if (timer <= 0) {
                        isTimerRunning = false;
                        timer = 0;

                        Lose = true;
                    }
                }
            }

            checkWin();

            //Check Pressure plate Collisions
            handlePressurePlateCollision();

            MotionEvent event = GameActivity.instance.getMotionEvent();
            if (event != null) {
                int action = event.getActionMasked();
                int pointerIndex = event.getActionIndex();
                int pointerId = event.getPointerId(pointerIndex);
                float touchX = event.getX(pointerIndex);
                float touchY = event.getY(pointerIndex);

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        // Handle joystick
                        if (Math.hypot(touchX - joystick.getCenterX(), touchY - joystick.getCenterY()) <= joystick.getBaseRadius() && joystickPointerId == -1) {
                            joystickPointerId = pointerId;
                            joystick.setTouched(true);
                            joystick.update(touchX, touchY);
                        }
                        // Handle jump button
                        else if (Math.hypot(touchX - jumpButtonX, touchY - jumpButtonY) <= jumpButtonRadius && jumpButtonPointerId == -1) {
                            jumpButtonPointerId = pointerId;
                            isJumpButtonPressed = true;
                        }
                        // Handle pick-up/drop button
                        else if (Math.hypot(touchX - pickUpButtonX, touchY - pickUpButtonY) <= pickUpButtonRadius && pickUpButtonPointerId == -1) {
                            pickUpButtonPointerId = pointerId;
                            if (!wasPickUpButtonPressed) {
                                isPickUpButtonPressed = true;
                                handlePickUpOrDrop(); // Execute pick-up/drop logic
                                wasPickUpButtonPressed = true; // Prevent spamming
                            }
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        for (int i = 0; i < event.getPointerCount(); i++) {
                            int movePointerId = event.getPointerId(i);
                            float moveTouchX = event.getX(i);
                            float moveTouchY = event.getY(i);

                            if (movePointerId == joystickPointerId) {
                                joystick.update(moveTouchX, moveTouchY);
                            }
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        if (pointerId == joystickPointerId) {
                            joystickPointerId = -1;
                            joystick.setTouched(false);
                            joystick.reset();
                        } else if (pointerId == jumpButtonPointerId) {
                            jumpButtonPointerId = -1;
                            isJumpButtonPressed = false;
                        } else if (pointerId == pickUpButtonPointerId) {
                            pickUpButtonPointerId = -1;
                            isPickUpButtonPressed = false;
                            wasPickUpButtonPressed = false;
                        }
                        break;
                }
            }


            // Calculate speed factor based on inventory weight
            float speedFactor = 1.0f;
            if (inventoryItem != null) {
                float maxWeight = 100.0f;
                speedFactor = Math.max(0.5f, 1.0f - (inventoryItem.getWeight() / maxWeight));
            }


            // Handle jumping
            if (isJumpButtonPressed && player.isOnPlatform()) {
                player.jump();
                isJumpButtonPressed = false;
            }


            // Handle joystick movement with weight-adjusted speed
            if (joystick.isTouched() || joystick.isSticky()) {
                float adjustedSpeed = 300 * speedFactor;
                float deltaX = joystick.getHorizontalPercentage() * adjustedSpeed * dt;
                player.setPositionX(player.getPositionX() + deltaX);
            }

            // Update other game logic
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

            for (Item item : items) {
                item.onUpdate(dt);
            }

            for (PressurePlate pressurePlate : pressurePlates) {
                pressurePlate.onUpdate(dt);
            }
        }
    }


    @Override
    public void onRender(Canvas canvas) {
        // Render background and game objects
        canvas.save();
        canvas.translate(-cameraX, 0);

        canvas.drawBitmap(_backgroundBitmap0, 0, 0, null);
        canvas.drawBitmap(_backgroundBitmap1, _backgroundBitmap0.getWidth(), 0, null);


        // Position lives relative to the camera
        float livesX = cameraX + 50;
        float livesY = 100;

        // Render lives remaining
        Paint livesPaint = new Paint();
        livesPaint.setTextSize(80);
        livesPaint.setColor(Color.RED);
        canvas.drawText("Lives: " + lives, livesX, livesY, livesPaint);

        //For Testing
//        if (CollisionTest)
//        {
//            livesPaint.setColor(Color.BLUE);
//            canvas.drawText("Weight: " + Test, cameraX + 50, 200, livesPaint);
//        }
//        else
//        {
//
//        }

        for (Platform platform : platforms) {
            if (!platform.isDisabled()) {
                platform.onRender(canvas);
            }
        }

        for (TrashBin trashBin : trashBins) {
            trashBin.onRender(canvas);
        }

        for (PressurePlate pressurePlate : pressurePlates) {
            pressurePlate.onRender(canvas);
        }

        // Render items
        for (Item item : items) {
            item.onRender(canvas);
        }

        for (GameEntity entity : _gameEntities) {
            entity.onRender(canvas);
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
            pickUpButtonPaint.setColor(Color.GREEN);
        } else {
            pickUpButtonPaint.setColor(Color.RED);
        }
        pickUpButtonPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(pickUpButtonX, pickUpButtonY, pickUpButtonRadius, pickUpButtonPaint);

        // Calculate top center position for inventory
        float inventoryX = (screenWidth - inventoryWidth) / 2f;
        float inventoryY = 20;

        // Render inventory UI
        Paint inventoryPaint = new Paint();
        inventoryPaint.setColor(Color.DKGRAY); // Background for inventory slot
        canvas.drawRect(inventoryX, inventoryY, inventoryX + inventoryWidth, inventoryY + inventoryHeight, inventoryPaint);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(80);
        canvas.drawText("Time: " + String.format("%.0f", timer) + "s", 1850, 100, textPaint);

        if (inventoryIcon != null) {
            // Draw the current inventory icon
            canvas.drawBitmap(inventoryIcon, null,
                    new android.graphics.RectF(inventoryX, inventoryY, inventoryX + inventoryWidth, inventoryY + inventoryHeight), null);
        }

        if (Win) {
            canvas.drawBitmap(_winBitmap, 0, 0, null);
        }

        if (Lose) {
            canvas.drawBitmap(_loseBitmap, 0, 0, null);
        }
    }

    public List<Platform> getPlatforms() {
        return platforms;
    }

    public Joystick getJoystick() {
        return joystick;
    }


    private void handlePickUpOrDrop() {
        if (inventoryItem == null) {
            // Inventory is empty: Try to pick up an item
            for (Item item : items) {
                if (!item.isPickedUp() && checkCollision(player, item)) {
                    item.pickUp();
                    inventoryItem = item;
                    inventoryIcon = item.getIcon();
                    return;
                }
            }

            // Try to pick up a trash bin
            for (TrashBin trashBin : trashBins) {
                if (!trashBin.isPickedUp() && checkCollision(player, trashBin)) {
                    trashBin.pickUp();
                    inventoryItem = trashBin;
                    inventoryIcon = trashBin.getIcon();
                    return;
                }
            }
        } else {
            // Inventory is full: Handle dropping the inventory item
            // Check if the player is in range of a trash bin
            for (TrashBin trashBin : trashBins) {
                if (checkCollision(player, trashBin)) {
                    if (inventoryItem instanceof RecyclableObject || inventoryItem instanceof NonRecyclableObject) {
                        trashBin.addItem(inventoryItem);
                        inventoryItem = null;
                        inventoryIcon = null;
                        return;
                    }
                }
            }

            // Drop the inventory item near the player if not in range of a trash bin
            inventoryItem.drop(player.getPositionX(), player.getPositionY() + 100); // Drop near the player
            inventoryItem = null;
            inventoryIcon = null;
        }
    }


    private boolean checkCollision(PlayerEntity player, Item item) {

        float playerLeft = player.getPositionX();
        float playerRight = player.getPositionX() + player.getWidth();
        float playerTop = player.getPositionY();
        float playerBottom = player.getPositionY() + player.getHeight();

        float itemLeft = item.getX();
        float itemRight = item.getX() + item.getWidth();
        float itemTop = item.getY();
        float itemBottom = item.getY() + item.getHeight();

        return playerRight > itemLeft && playerLeft < itemRight &&
                playerBottom > itemTop && playerTop < itemBottom;
    }

    private void handlePressurePlateCollision()
    {
        for (PressurePlate pressurePlate : pressurePlates)
        {
            for (TrashBin trashBin : trashBins)
            {
                if (pressurePlateCollision(trashBin, pressurePlate))
                {
//                    CollisionTest = true;
//                    Test = pressurePlate.currentWeight;

                    if (trashBin.getCurrentWeight() >= pressurePlate.weightReq)
                    {
                        enablePlatform(pressurePlate.relatedPlatform);
                    }
                }
            }
        }
    }


    private void disablePlatform(Platform platformToDisable) {
        if (platforms.contains(platformToDisable)) {
            platformToDisable.setDisabled(true);
            System.out.println("Platform at " + platformToDisable.getX() + ", " + platformToDisable.getY() + " is now disabled.");
        } else {
            System.out.println("Platform not found in the list.");
        }
    }

    // Method to enable a platform by passing the platform object
    public void enablePlatform(Platform platformToEnable) {
        if (platforms.contains(platformToEnable)) {
            platformToEnable.setDisabled(false);  // Enable the platform
            System.out.println("Platform at " + platformToEnable.getX() + ", " + platformToEnable.getY() + " is now enabled.");
        } else {
            System.out.println("Platform not found in the list.");
        }
    }

    private boolean pressurePlateCollision(TrashBin trashBin, PressurePlate pressurePlate) {
        float trashBinLeft = trashBin.getX();
        float trashBinRight = trashBin.getX() + trashBin.getWidth();
        float trashBinTop = trashBin.getY();
        float trashBinBottom = trashBin.getY() + trashBin.getHeight();

        float pressurePlateLeft = pressurePlate.getX();
        float pressurePlateRight = pressurePlate.getX() + pressurePlate.getWidth();
        float pressurePlateTop = pressurePlate.getY();
        float pressurePlateBottom = pressurePlate.getY() + pressurePlate.getHeight();

        return pressurePlateRight > trashBinLeft && pressurePlateLeft < trashBinRight &&
                pressurePlateBottom > trashBinTop && pressurePlateTop < trashBinBottom;
    }


    public void loseLife() {
        lives--;
        System.out.println("Lives remaining: " + lives);
        if (lives <= 0) {
            endGame();
        }
    }

    public void checkWin()
    {
        boolean allTrashed = true;

        for (Item item : items) {
            if (!item.isTrashed()) {
                allTrashed = false;
                break;
            }
        }

        if (allTrashed) {
            System.out.println("You win! All items have been trashed.");
            Win = true;
        }
    }

    private void endGame() {
        //End Game Logic Here
        Lose = true;
    }
}
