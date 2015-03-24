package com.chenjishi.flyingcross.animation;

import android.widget.ImageView;

/**
 * Created by chenjishi on 15/3/24.
 */
public class AnimateView {

    private final ImageView mImageView;

    public AnimateView(ImageView imageView) {
        mImageView = imageView;
    }

    @SuppressWarnings("NewApi")
    public void setPosition(PathPoint point) {
        mImageView.setTranslationX(point.mX);
        mImageView.setTranslationY(point.mY);
    }
}
