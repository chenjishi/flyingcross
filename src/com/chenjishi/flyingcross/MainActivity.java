package com.chenjishi.flyingcross;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.chenjishi.flyingcross.animation.Utils;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(new MyListAdapter(this));
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

    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ViewGroup parentView = (ViewGroup) v.getParent();
            if (null == parentView) return;

            View iconView = parentView.findViewById(R.id.image_view);
            if (null == iconView) return;

            Utils.flyingAnimation(MainActivity.this, iconView);
        }
    };
}
