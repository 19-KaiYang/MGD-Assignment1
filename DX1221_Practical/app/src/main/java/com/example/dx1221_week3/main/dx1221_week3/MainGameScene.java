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



    private final List<Item> items = new ArrayList<>();
    private Bitmap _backgroundBitmap0;
    private Bitmap _backgroundBitmap1;
    private float cameraX = 0; // Horizontal camera offset
    private int screenWidth, screenHeight;
    private float totalWorldWidth; // Game world width
    private PlayerEntity player;
    private Joystick joystick;
    private final List<Platform> platforms = new ArrayList<>();
    private final List<PressurePlate> pressurePlates = new ArrayList<>();

    private final List<Door> doors = new ArrayList<>();

    //For Jumping

    private boolean isJumpButtonPressed = false;
    private float jumpButtonX, jumpButtonY, jumpButtonRadius;

    //Pick Up
    private float pickUpButtonX, pickUpButtonY, pickUpButtonRadius;
    private boolean isPickUpButtonPressed = false; // Tracks the previous state of the button
    private boolean wasPickUpButtonPressed = false;


    //Inventory
    private Item inventoryItem = null; // Can now store both TrashBin and Item objects

    private Bitmap inventoryIcon = null; // Current inventory item icon
    private final float inventoryX = 50; // X position for inventory UI
    private final float inventoryY = 50; // Y position for inventory UI
    private final float inventoryWidth = 100; // Width for the inventory icon
    private final float inventoryHeight = 100; // Height for the inventory icon


    //Pointers
    private int joystickPointerId = -1; // Pointer ID for joystick
    private int jumpButtonPointerId = -1; // Pointer ID for jump button
    private int pickUpButtonPointerId = -1; // Pointer ID for pick-up/drop button


    //Lives
    private int lives = 3;




    @Override
    //On Start
    public void onCreate() {
        super.onCreate();

        screenHeight = GameActivity.instance.getResources().getDisplayMetrics().heightPixels;
        screenWidth = GameActivity.instance.getResources().getDisplayMetrics().widthPixels;

        // Define world size
        totalWorldWidth = screenWidth * 2f; // Example: Game world is three screens wide

        // Load background
        Bitmap bmp = BitmapFactory.decodeResource(GameActivity.instance.getResources(), R.drawable.gamescene);
        _backgroundBitmap0 = Bitmap.createScaledBitmap(bmp, screenWidth, screenHeight, true);
        _backgroundBitmap1 = Bitmap.createScaledBitmap(bmp, screenWidth, screenHeight, true);

        // Initialize player
        player = new PlayerEntity();
        _gameEntities.add(player);

        // Initialize TrashBin
        TrashBin trashBin = new TrashBin(500, screenHeight - 300, R.drawable.trashbin, 100, 150, 10);
        trashBins.add(trashBin);

        //Initialize RecyclingBin
        RecyclingBin recyclingBin = new RecyclingBin(1600, screenHeight - 300, R.drawable.recyclingbin, 100, 150, 10);
        trashBins.add(recyclingBin);


        // Initialize joystick with stickiness
        joystick = new Joystick(screenWidth / 8f, screenHeight * 4f / 5.5f, 150, 75, true); // The `true` enables sticky mode

        // Add platforms
        platforms.add(new Platform(0, screenHeight - 200, screenWidth * 2, 100));
        platforms.add(new Platform(screenWidth / 2f, screenHeight - 400, 300, 40)); // Floating platform
        platforms.add(new Platform(screenWidth / 2f + 300, screenHeight - 700, 40, 300)); // Floating platform

        //add Pressure Plates
        pressurePlates.add(new PressurePlate(screenWidth / 2f, screenHeight - 230, 100, 30, 20));



        // Initialize jump button
        jumpButtonRadius = 100;
        jumpButtonX = screenWidth - jumpButtonRadius - 150; // Right corner of the screen
        jumpButtonY = screenHeight - jumpButtonRadius - 130;


        // Define the position and size of the pick-up button
        pickUpButtonRadius = 100;
        pickUpButtonX = screenWidth - pickUpButtonRadius - 500; // Adjust X position
        pickUpButtonY = screenHeight - pickUpButtonRadius - 130; //


         // Initialize Items (Recyclable and Non-Recyclable)
        // ADD ITEMS HERE
        // ALL RECYCLABLE AND NON RECYCLABLE
        Bitmap recyclableImage = BitmapFactory.decodeResource(GameActivity.instance.getResources(), R.drawable.bottle);
        Bitmap nonRecyclableImage = BitmapFactory.decodeResource(GameActivity.instance.getResources(), R.drawable.trashbag);

        items.add(new RecyclableObject(900, screenHeight - 500, recyclableImage, 100, 100, 20)); // Weight = 20kg
        items.add(new NonRecyclableObject(800, screenHeight - 500, nonRecyclableImage, 120, 120, 10)); // Weight = 10kg
    }

    @Override
    public void onUpdate(float dt) {
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
            float maxWeight = 100.0f; // Adjust this value for max impact
            speedFactor = Math.max(0.5f, 1.0f - (inventoryItem.getWeight() / maxWeight));
        }


        // Handle jumping
        if (isJumpButtonPressed && player.isOnPlatform()) {
            player.jump();
            isJumpButtonPressed = false; // Reset jump button
        }


        // Handle joystick movement with weight-adjusted speed
        if (joystick.isTouched() || joystick.isSticky()) {
            float adjustedSpeed = 300 * speedFactor; // Adjust base speed with weight factor
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

        for (PressurePlate pressurePlate: pressurePlates) {
            pressurePlate.onUpdate(dt);
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
        float livesX = cameraX + 50; // Offset from the left of the screen
        float livesY = 100; // Offset from the top of the screen

        // Render lives remaining
        Paint livesPaint = new Paint();
        livesPaint.setColor(Color.RED);
        livesPaint.setTextSize(80);
        canvas.drawText("Lives: " + lives, livesX, livesY, livesPaint);


        for (Platform platform : platforms) {
            platform.onRender(canvas);
        }

        for (PressurePlate pressurePlate : pressurePlates) {
            pressurePlate.onRender(canvas);
        }

        for (TrashBin trashBin : trashBins) {
            trashBin.onRender(canvas);
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
            pickUpButtonPaint.setColor(Color.GREEN); // Green for pick-up
        } else {
            pickUpButtonPaint.setColor(Color.RED); // Red for drop
        }
        pickUpButtonPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(pickUpButtonX, pickUpButtonY, pickUpButtonRadius, pickUpButtonPaint);

        // Calculate top center position for inventory
        float inventoryX = (screenWidth - inventoryWidth) / 2f; // Center horizontally
        float inventoryY = 20; // Distance from the top of the screen

        // Render inventory UI
        Paint inventoryPaint = new Paint();
        inventoryPaint.setColor(Color.DKGRAY); // Background for inventory slot
        canvas.drawRect(inventoryX, inventoryY, inventoryX + inventoryWidth, inventoryY + inventoryHeight, inventoryPaint);

        if (inventoryIcon != null) {
            // Draw the current inventory icon
            canvas.drawBitmap(inventoryIcon, null,
                    new android.graphics.RectF(inventoryX, inventoryY, inventoryX + inventoryWidth, inventoryY + inventoryHeight), null);
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
                    item.pickUp(); // Mark as picked up
                    inventoryItem = item; // Store in inventory
                    inventoryIcon = item.getIcon(); // Set inventory icon
                    return; // Exit after picking up the item
                }
            }

            // Try to pick up a trash bin
            for (TrashBin trashBin : trashBins) {
                if (!trashBin.isPickedUp() && checkCollision(player, trashBin)) {
                    trashBin.pickUp(); // Mark as picked up
                    inventoryItem = trashBin; // Store in inventory
                    inventoryIcon = trashBin.getIcon(); // Set inventory icon
                    return; // Exit after picking up the trash bin
                }
            }
        } else {
            // Inventory is full: Handle dropping the inventory item
            // Check if the player is in range of a trash bin
            for (TrashBin trashBin : trashBins) {
                if (checkCollision(player, trashBin)) {
                    if (inventoryItem instanceof RecyclableObject || inventoryItem instanceof NonRecyclableObject) {
                        trashBin.addItem(inventoryItem); // Add the item to the trash bin
                        inventoryItem = null; // Clear the inventory
                        inventoryIcon = null; // Clear the inventory icon
                        return; // Exit after dropping the item into the trash bin
                    }
                }
            }

            // Drop the inventory item near the player if not in range of a trash bin
            inventoryItem.drop(player.getPositionX(), player.getPositionY() + 100); // Drop near the player
            inventoryItem = null; // Clear the inventory
            inventoryIcon = null; // Clear the inventory icon
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

    private void handlePressurePlateCollision() {
        for (PressurePlate pressurePlate : pressurePlates) {
            for (TrashBin trashBin : trashBins)
            {
                if (pressurePlateCollision(trashBin, pressurePlate))
                {

                }
            }
        }
    }

    private boolean pressurePlateCollision(TrashBin trashBin, PressurePlate pressurePlate)
    {
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

    private void endGame()
    {
       //End Game Logic Here

    }
}
