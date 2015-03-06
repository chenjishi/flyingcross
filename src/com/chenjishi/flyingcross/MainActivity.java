package com.chenjishi.flyingcross;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainActivity extends Activity {
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(new MyListAdapter(this));

        mWindowManager = getWindowManager();
        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.height = MATCH_PARENT;
        mWindowParams.width = MATCH_PARENT;

        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
    }

    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ViewGroup viewGroup = (ViewGroup) v.getParent();
            if (null == viewGroup) return;

            ImageView imageView = (ImageView) viewGroup.findViewById(R.id.image_view);

            rectInWindow(imageView);

            ImageView animateView = new ImageView(MainActivity.this);

            imageView.destroyDrawingCache();
            imageView.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache());
            animateView.setImageBitmap(bitmap);
            imageView.setDrawingCacheEnabled(false);

            startAnimation(animateView);
        }
    };

    private void startAnimation(final ImageView imageView) {
        final FrameLayout contentView = new FrameLayout(this);
        mWindowManager.addView(contentView, mWindowParams);

        contentView.addView(imageView, new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

        int startX = mRect.left;
        int startY = mRect.top;

        int startWidth = mRect.width();

        rectInWindow(findViewById(R.id.icon_right));

        int endWidth = mRect.width();

        int offset = (startWidth - endWidth) / 2;
        float scale = endWidth * 1.f / startWidth;
        int endX = mRect.left - offset;
        int endY = mRect.top - offset;

        ObjectAnimator animX = ObjectAnimator.ofFloat(imageView, "x", startX, endX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(imageView, "y", startY, endY);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(imageView, "scaleX", scale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imageView, "scaleY", scale);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.playTogether(animX, animY, scaleX, scaleY);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                contentView.removeView(imageView);
                mWindowManager.removeView(contentView);
            }
        });
        animatorSet.start();
    }

    private final Rect mRect = new Rect();
    private final int[] mLocation = new int[2];

    private void rectInWindow(View view) {
        view.getLocationInWindow(mLocation);

        mRect.left = mLocation[0];
        mRect.top = mLocation[1];
        mRect.right = mLocation[0] + view.getWidth();
        mRect.bottom = mLocation[1] + view.getHeight();
    }

    private class MyListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        private int[] mIcons = {R.drawable.ic_sogou, R.drawable.share_icon_sina, R.drawable.share_icon_weixin};

        public MyListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return 20;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if (null == convertView) {
                convertView = mInflater.inflate(R.layout.list_item, parent, false);
                holder = new ViewHolder();

                holder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
                holder.textView = (TextView) convertView.findViewById(R.id.text_view);
                holder.button = (Button) convertView.findViewById(R.id.button);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.imageView.setImageResource(mIcons[position % 3]);

            holder.textView.setText("ITEM " + position);
            holder.button.setOnClickListener(mClickListener);

            return convertView;
        }
    }

    private static class ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public Button button;
    }
}
