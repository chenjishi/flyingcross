package com.chenjishi.flyingcross.animation;

import android.animation.*;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.chenjishi.flyingcross.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by chenjishi on 15/3/24.
 */
public class Utils {

    @SuppressWarnings("NewApi")
    public static void flyingAnimation(Context context, View v) {
        Activity activity = (Activity) context;
        if (null == activity) return;

        final ImageView animateView = new ImageView(context);

        final int[] locations = new int[2];
        final Rect rect = new Rect();

        v.getLocationInWindow(locations);
        rect.left = locations[0];
        rect.top = locations[1];
        rect.right = locations[0] + v.getWidth();
        rect.bottom = locations[1] + v.getHeight();

        v.destroyDrawingCache();
        v.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());
        animateView.setImageBitmap(bitmap);
        v.setDrawingCacheEnabled(false);

        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
        windowParams.height = MATCH_PARENT;
        windowParams.width = MATCH_PARENT;

        windowParams.format = PixelFormat.TRANSLUCENT;
        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        final FrameLayout container = new FrameLayout(context);
        container.addView(animateView, new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        windowManager.addView(container, windowParams);

        int startX = rect.left;
        int startY = rect.top;
        int startWidth = rect.width();
        int startHeight = rect.height();

        View targetView = activity.findViewById(R.id.right_view);
        if (null == targetView) return;

        targetView.getLocationInWindow(locations);
        rect.left = locations[0];
        rect.top = locations[1];
        rect.right = locations[0] + targetView.getWidth();
        rect.bottom = locations[1] + targetView.getHeight();
        if (rect.left == 0) return;

        int endWidth = rect.width();
        int endHeight = rect.height();
        int offsetX = Math.abs((startWidth - endWidth) / 2);
        int offsetY = Math.abs((startHeight - endHeight) / 2);
        float widthScale = endWidth * 1.f / startWidth;
        float heightScale = endHeight * 1.f / startHeight;
        int endX = rect.left - offsetX;
        int endY = rect.top - offsetY;

        /**
         * Bezier curve point, start point(startX, startY),
         * control point 1(cx1, cy1), control point 2(cx2, cy2),
         * and end point(endX, endY)
         * you can define your own control point here
         *
         */
        final float cx1 = (startX + endX) / 4;
        final float cy1 = startY;
        final float cx2 = (startX + endX) / 2;
        final float cy2 = startY;

        PathPoint startPoint = PathPoint.curveTo(cx1, cy1, cx2, cy2, startX, startY);
        PathPoint endPoint = PathPoint.curveTo(cx1, cy1, cx2, cy2, endX, endY);

        AnimateView wrapView = new AnimateView(animateView);
        ObjectAnimator anim = ObjectAnimator.ofObject(wrapView, "Position", new TypeEvaluator<PathPoint>() {
            @Override
            public PathPoint evaluate(float t, PathPoint startValue, PathPoint endValue) {
                float x, y;
                float oneMinusT = 1 - t;
                x = oneMinusT * oneMinusT * oneMinusT * startValue.mX +
                        3 * oneMinusT * oneMinusT * t * endValue.mControl0X +
                        3 * oneMinusT * t * t * endValue.mControl1X +
                        t * t * t * endValue.mX;
                y = oneMinusT * oneMinusT * oneMinusT * startValue.mY +
                        3 * oneMinusT * oneMinusT * t * endValue.mControl0Y +
                        3 * oneMinusT * t * t * endValue.mControl1Y +
                        t * t * t * endValue.mY;

                return PathPoint.moveTo(x, y);
            }
        }, startPoint, endPoint);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(animateView, "scaleX", widthScale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(animateView, "scaleY", heightScale);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(600);
        animatorSet.playTogether(anim, scaleX, scaleY);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                container.removeView(animateView);
                windowManager.removeView(container);
            }
        });
        animatorSet.start();
    }
}
