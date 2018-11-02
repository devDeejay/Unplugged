package com.devdelhi.unplugged;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MoreTipsActivity extends AppCompatActivity {

    private ViewPager slideViewPager;
    private LinearLayout mDotLayout;
    private MoreTipsSliderAdapter sliderAdapter;
    private TextView[] mDots;
    private Button mButton;
    private Button mNextButton;
    private Button mPrevButton;
    private int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_layout);
        mButton = findViewById(R.id.startAppButton);
        mButton.setVisibility(View.INVISIBLE);

        mNextButton = findViewById(R.id.next_icon);
        mPrevButton = findViewById(R.id.prev_icon);

        mPrevButton.setVisibility(View.INVISIBLE);
        mNextButton.setVisibility(View.VISIBLE);

        slideViewPager = findViewById(R.id.slideViewPager);
        mDotLayout = findViewById(R.id.dotsLayout);

        sliderAdapter = new MoreTipsSliderAdapter(this);
        slideViewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);
        slideViewPager.addOnPageChangeListener(viewListener);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoreTipsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideViewPager.setCurrentItem(mCurrentPage + 1);
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideViewPager.setCurrentItem(mCurrentPage - 1);
            }
        });
    }

    public void addDotsIndicator(int position) {
        mDots = new TextView[4];
        mDotLayout.removeAllViews();

        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this );
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setGravity(Gravity.CENTER);
            mDots[i].setTextColor(Color.parseColor("#AA000000"));
            mDotLayout.addView(mDots[i]);
        }

        if (mDots.length > 0) {
            mDots[position].setTextColor(Color.parseColor("#FFCC00"));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            mCurrentPage = position;

            if (position == 0) {
                mNextButton.setEnabled(true);
                mPrevButton.setEnabled(false);
                mPrevButton.setVisibility(View.INVISIBLE);

                mNextButton.setText("Next");
                mPrevButton.setText("");
            }

            else if (position == mDots.length - 1){
                mButton.setEnabled(true);
                mPrevButton.setEnabled(true);

                mPrevButton.setVisibility(View.VISIBLE);
                mButton.setVisibility(View.VISIBLE);
                mNextButton.setVisibility(View.INVISIBLE);

                mPrevButton.setText("Prev");
                mNextButton.setText("");
                mButton.setText("Finish");

            }

            else {
                mButton.setEnabled(false);
                mButton.setVisibility(View.INVISIBLE);

                mNextButton.setEnabled(true);
                mNextButton.setVisibility(View.VISIBLE);

                mPrevButton.setText("Prev");
                mNextButton.setText("Next");

                mPrevButton.setEnabled(true);
                mPrevButton.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
