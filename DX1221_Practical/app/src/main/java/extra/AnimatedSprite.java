
package extra;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class AnimatedSprite {
    private final int _col;
    private final int _width;
    private final int _height;
    private int _currentFrame = 0;
    private int _startFrame = 0;
    private int _endFrame;
    private final float _timePerFrame;
    private float _timeAccumulated = 0f;
    private final Bitmap _bmp;
    private boolean _isLooping = true;

    private final Rect _src;
    private final Rect _dst;

    public AnimatedSprite(Bitmap bitmap, int row, int col, int fps) {
        _bmp = bitmap;
        _col = col;
        _width = _bmp.getWidth() / _col;
        _height = _bmp.getHeight() / row;
        _timePerFrame = 1f / fps;
        _endFrame = (_col * row) - 1;
        _src = new Rect();
        _dst = new Rect();
    }

    public AnimatedSprite(Bitmap bitmap, int row, int col, int fps, int startFrame, int endFrame) {
        this(bitmap, row, col, fps);
        _startFrame = startFrame;
        _endFrame = endFrame;
    }

    public void setLooping(boolean shouldLoop) { _isLooping = shouldLoop; }

    public boolean hasFinished() {
        if (_isLooping) return false;
        return _currentFrame == _endFrame;
    }

    public void update(float dt) {
        if (hasFinished()) return;

        _timeAccumulated += dt;
        if (_timeAccumulated > _timePerFrame) {
            _timeAccumulated -= _timePerFrame;

            // Increment frame and reset
            _currentFrame++;
            if (_currentFrame > _endFrame) {
                if (_isLooping) {
                    _currentFrame = _startFrame;
                } else {
                    _currentFrame = _endFrame;
                }
            }
        }
    }


    public void render(Canvas canvas, int x, int y, Paint paint) {
        int frameX = _currentFrame % _col;
        int frameY = _currentFrame / _col;

        // Calculate source rectangle for the current frame
        int srcX = frameX * _width;
        int srcY = frameY * _height;

        x -= (int) (0.5f * _width);
        y -= (int) (0.5f * _height);

        _src.left = srcX;
        _src.top = srcY;
        _src.right = srcX + _width;
        _src.bottom = srcY + _height;

        _dst.left = x;
        _dst.top = y;
        _dst.right = x + _width;
        _dst.bottom = y + _height;

        canvas.drawBitmap(_bmp, _src, _dst, paint);
    }

    public int getWidth() {
        return _width; // Width of a single frame
    }

    public int getHeight() {
        return _height; // Height of a single frame
    }



}
