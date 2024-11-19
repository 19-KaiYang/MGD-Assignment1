
// README
/*
################ Introduction ################

MGP2D is a very simple and light-weight game development framework for Android studio.

MGP2D "Core" is designed to have a small API footprint. It is only opinionated in managing the
lifecycle of "GameScenes", but is non-opinionated in how resources should be managed (e.g. player
input, loading bitmap and audio resources, getting display metrics, etc). This should leave little
room for errors on the part of the framework developers. Hence, instead of "learning the framework"
(or "blaming the framework"), Android Studio's official documentation is still the go-to resource
for help and support.

Given the light-weight and non-opinionated nature of MGP2D, it should be CLOSED FROM MODIFICATION!
This is to avoid the chance of introducing unnecessary breaking changes.

################ Architecture ################

"GameActivity" hosts a single SurfaceView and worker thread for frame renders and updates. On every
frame, GameActivity will call the update(deltaTime) and render() methods of the current active
GameScene. In turn, the current GameScene can call the onUpdate(deltaTime) and onRender() methods
of the GameEntity objects kept by it.

All GameScene child classes contain a list of overridable lifecycle methods, namely:
- onCreate()            <-- only ever gets triggered once in the scene's lifetime
- onEnter()             <-- triggered when we enter this scene. Runs after 'onCreate'!
- onUpdate(deltaTime)   <-- triggered on every frame update. Only for updating data
- onRender()            <-- triggered after frame update. Only for rendering visuals on screen
- onExit()              <-- triggered when we are changing to another scene.
- onDestroy()           <-- // TODO: To be implemented

All dynamic/static actors/objects in a GameScene is a GameEntity (e.g. background, player, etc).
The method or data structure used by the GameScene to store all GameEntities is up to developers.
Each GameEntity contains these methods:
- destroy()             <-- sets a flag that signals that an entity is ready to be destroyed.
- canDestroy()          <-- can be called by a GameScene to check if an entity should be removed from it's collection.
- onUpdate(deltaTime)   <-- triggered on every frame update. Only for updating data.
- onRender()            <-- triggered on every frame update. Only for rendering.
- getOrdinal()          <-- helper method to get _ordinal which you can use for sorting purposes.
                            You may set _ordinal when constructing the class. Since it is 0
                            by default, you do not need to use it if you prefer to use your own
                            strategy to sort game entities in your scenes instead.

################ Usage and API ################

== startActivity(new Intent(this, GameActivity.class)); // enter GameActivity from any other Activity
== GameScene.enter(ExampleGameScene.class);             // switch to another GameScene any time
== GameActivity.instance.finish();                      // "stop/pause" the game any time by exiting GameActivity
== GameActivity.instance.getResources();                // helpful for getting resource-related methods like loadBitmap(), getDisplayMetrics(), etc
== GameActivity.instance.setTimeScale(float timeScale); // sets scale at which time passes (slow motion / speed up). Set to 0 to pause game
== GameActivity.instance.getMotionEvent();              // getting the last motion event retrieved from onTouchEvent()

 */

package mgp2d.core;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

public class GameActivity extends FragmentActivity {

    private static class UpdateThread extends Thread {
        public boolean _isRunning = true;
        public void terminate() { _isRunning = false; }
        public boolean isRunning() { return _isRunning; }
        private final SurfaceHolder _surfaceHolder;

        public UpdateThread(SurfaceView surfaceView) {
            _surfaceHolder = surfaceView.getHolder();
            _surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                @Override public void surfaceCreated(@NonNull SurfaceHolder holder) { if (!isAlive()) start(); }
                @Override public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {}
                @Override public void surfaceDestroyed(@NonNull SurfaceHolder holder) { terminate(); }
            });
        }

        @Override
        public void run()
        {
            long prevTime = System.nanoTime();
            _isRunning = true;

            while(_isRunning) {
                // Calculating deltaTime
                long currentTime = System.nanoTime();
                float deltaTime = (currentTime-prevTime)/1000000000.0f;
                prevTime = currentTime;

                if (GameScene.getNext() != GameScene.getCurrent())
                    GameScene.enter(GameScene.getNext());

                if (GameScene.getCurrent() == null)
                    continue;

                // Update current game scene
                GameScene.getCurrent().onUpdate(deltaTime * _timeScale);

                // Render current game scene
                Canvas canvas = _surfaceHolder.lockCanvas(null);
                if (canvas != null) {
                    synchronized (_surfaceHolder) {
                        canvas.drawColor(Color.BLACK); // reset canvas
                        if (GameScene.getCurrent() != null)
                            GameScene.getCurrent().onRender(canvas);
                    }
                    _surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    public static GameActivity instance = null;

    private static float _timeScale = 1;
    public void setTimeScale(float timeScale) { _timeScale = timeScale; }

    private static MotionEvent _motionEvent = null;
    public MotionEvent getMotionEvent() { return _motionEvent; }

    private UpdateThread _updateThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        SurfaceView surfaceView = new SurfaceView(this);
        setContentView(surfaceView);
        _updateThread = new UpdateThread(surfaceView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        _motionEvent = event;
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!_updateThread.isRunning())
            _updateThread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        _updateThread.terminate();
        GameScene.exitCurrent();
    }

    @Override
    protected void onPause() {
        super.onPause();
        _updateThread.terminate();
        GameScene.exitCurrent();
    }
}
