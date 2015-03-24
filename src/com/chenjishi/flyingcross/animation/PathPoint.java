package com.chenjishi.flyingcross.animation;

/**
 * Created by chenjishi on 15/3/23.
 */
public class PathPoint {
    float mX, mY;

    float mControl0X, mControl0Y;

    float mControl1X, mControl1Y;

    private PathPoint(float x, float y) {
        mX = x;
        mY = y;
    }

    private PathPoint(float c0X, float c0Y, float c1X, float c1Y, float x, float y) {
        mControl0X = c0X;
        mControl0Y = c0Y;
        mControl1X = c1X;
        mControl1Y = c1Y;
        mX = x;
        mY = y;
    }

    public static PathPoint curveTo(float c0X, float c0Y, float c1X, float c1Y, float x, float y) {
        return new PathPoint(c0X, c0Y, c1X, c1Y, x, y);
    }

    public static PathPoint moveTo(float x, float y) {
        return new PathPoint(x, y);
    }
}
