package com.miscell.flyingcross;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by chenjishi on 15/3/24.
 */
public class AnimateView extends ImageView {

    private PathPoint point;

    public AnimateView(Context context) {
        super(context);
    }

    public AnimateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PathPoint getPosition() {
        return point;
    }

    public void setPosition(PathPoint point) {
        this.point = point;
        setTranslationX(point.x);
        setTranslationY(point.y);
    }
}
