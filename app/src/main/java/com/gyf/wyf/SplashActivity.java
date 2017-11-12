package com.gyf.wyf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/28.
 */

public class SplashActivity extends AppCompatActivity {

    private int previousPointEnale = 0;
    private LinearLayout pointLayout;
    private ViewPager mViewPager;
    private MyAdapter myAdapter;
    private List<ImageView> mImageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (getFirst()) {
            new Handler().postDelayed(new Runnable(){
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }
            }, 3000);
        } else {
            setFirst(true);
            initView();
        }
    }

    public boolean setFirst(boolean first) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean("first", first);
        return editor.commit();
    }

    public boolean getFirst() {
        return true;
        //return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("first", false);
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        pointLayout = (LinearLayout) findViewById(R.id.pointLayout);

        mImageList.clear();
        pointLayout.removeAllViews();
        ImageView mImageView;
        LinearLayout.LayoutParams params;

        mImageView = new ImageView(SplashActivity.this);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mImageView.setImageResource(R.drawable.pic2);
        mImageList.add(mImageView);

        mImageView = new ImageView(SplashActivity.this);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mImageView.setImageResource(R.drawable.pic3);
        mImageList.add(mImageView);

        mImageView = new ImageView(SplashActivity.this);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mImageView.setImageResource(R.drawable.pic4);
        mImageList.add(mImageView);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        });

        for (int i = 0; i < mImageList.size(); i++) {
            View dot = new View(SplashActivity.this);
            dot.setBackgroundResource(R.drawable.point_background);
            params = new LinearLayout.LayoutParams(30,30);
            params.leftMargin = 50;
            dot.setLayoutParams(params);
            dot.setEnabled(false);
            pointLayout.addView(dot);
        }

        myAdapter = new MyAdapter();
        mViewPager.setAdapter(myAdapter);
        mViewPager.addOnPageChangeListener(new MyListener());
        pointLayout.getChildAt(0).setEnabled(true);
    }

    private class MyListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            pointLayout.getChildAt(previousPointEnale).setEnabled(false);
            pointLayout.getChildAt(arg0).setEnabled(true);
            previousPointEnale = arg0;
        }
    }

    private class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImageList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            try {
                container.addView(mImageList.get(position));
            } catch (Exception e) {
            }
            return mImageList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // container.removeView(mImageList.get(position %
            // mImageList.size()));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

}
