package mgp2d.core;

import static java.lang.Math.sqrt;

public class Vector2 {
    public float x, y;

    public Vector2(float x, float y) { this.x = x; this.y = y; }

    public Vector2 add(Vector2 b) { return new Vector2(x + b.x, y + b.y); }

    public Vector2 multiply(float scale) { return new Vector2(x * scale, y * scale); }

    public Vector2 subtract(Vector2 b) { return this.add(b.multiply(-1)); }

    public float getMagnitude() { return (float)sqrt(x * x + y * y); }

    public Vector2 normalize() { return new Vector2(x /= getMagnitude(), y /= getMagnitude()); }

    public Vector2 copy() { return new Vector2(x, y); }

    public boolean equals(Vector2 other) { return x == other.x && y == other.y; }

    public Vector2 limit(float maxMagnitude) {
        float length = getMagnitude();
        float multiplier = length <= maxMagnitude ? 1 : maxMagnitude / length;
        return new Vector2(x * multiplier, y * multiplier);
    }

}
