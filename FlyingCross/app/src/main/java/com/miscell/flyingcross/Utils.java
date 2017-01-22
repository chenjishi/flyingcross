package com.miscell.flyingcross;

import android.animation.*;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by chenjishi on 15/3/24.
 */
public class Utils {

    public static void flyingAnimation(Context context, View v) {
        Activity activity = (Activity) context;
        if (null == activity) return;

        final AnimateView animateView = new AnimateView(context);

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

        PathPoint p0 = new PathPoint(rect.left, rect.top);
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
        float scaleX = endWidth * 1.f / startWidth;
        float scaleY = endHeight * 1.f / startHeight;
        PathPoint p3 = new PathPoint(rect.left - offsetX, rect.top - offsetY);

        /**
         * Bezier curve point, start point(p0),
         * control point 1(p1), control point 2(p2),
         * and end point(p3)
         * you can define your own control point here
         *
         */
        final PathPoint p1 = new PathPoint((p0.x + p3.x) / 4, p0.y);
        final PathPoint p2 = new PathPoint((p0.x + p3.x) / 2, p0.y);

        ObjectAnimator curve;
        if (Build.VERSION.SDK_INT >= 21) {
            Path path = new Path();
            path.moveTo(p0.x, p0.y);
            path.cubicTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
            curve = ObjectAnimator.ofFloat(animateView, "x", "y", path);
        } else {
            curve = ObjectAnimator.ofObject(animateView, "Position", new TypeEvaluator<PathPoint>() {

                @Override
                public PathPoint evaluate(float t, PathPoint startValue, PathPoint endValue) {
                    float x = startValue.x * (1 - t) * (1 - t) * (1 - t) +
                            3 * p1.x * t * (1 - t) * (1 - t) +
                            3 * p2.x * t * t * (1 - t) + endValue.x * t * t * t;
                    float y = startValue.y * (1 - t) * (1 - t) * (1 - t) +
                            3 * p1.y * t * (1 - t) * (1 - t) +
                            3 * p2.y * t * t * (1 - t) + endValue.y * t * t * t;
                    return new PathPoint(x, y);
                }
            }, p0, p3);
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(600);
        animatorSet.playTogether(curve,
                ObjectAnimator.ofFloat(animateView, "scaleX", scaleX),
                ObjectAnimator.ofFloat(animateView, "scaleY", scaleY));
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
