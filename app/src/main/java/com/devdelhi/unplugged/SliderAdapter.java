package com.devdelhi.unplugged;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    public int[] slide_images = {
            R.drawable.smartphone_hand,
            R.drawable.theft_black,
            R.drawable.usb_black,
            R.drawable.earphones_black,
            R.drawable.alarm_black,
            R.drawable.checked
    };

    public String[] slide_headings = {
            "Welcome To UnPlugged",
            "Protection Against Theft",
            "Simple Connect Your USB",
            "Or Your Earphones",
            "Alarm Will Be Set Off",
            "You Are Good To Go",
    };

    public String[] slide_descriptions = {
            "Simple Works! That's what we believe in! And we created this Simple App To Protect Your Device against thefts.",
            "When you are sleeping, travelling or just leaving your phone plugged in a public place protect your device from being stolen by plugging it in.",
            "Simply Plug in your device to a charger, power bank, or a computer. Its alarm will go off when it is disconnected.",
            "Simply Plug in your earphones, keep your device in your pocket. Its alarm will go off if earphones get disconnected in an event of theft from your pocket.",
            "A Loud Alarm Will be set off which will get your attention instantly to your device in such an event of theft",
            "Just Remember To Plug In Your Device and then Turn On The Alarms. This app is made to get your attention to your device while some one unplugs it.",
    };

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (RelativeLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout_file, container, false);

        ImageView slideImageView = view.findViewById(R.id.slide_image);
        TextView slideHeadingTextView = view.findViewById(R.id.slide_heading);
        TextView slideDescTextView  = view.findViewById(R.id.slide_description);

        slideImageView.setImageResource(slide_images[position]);
        slideHeadingTextView.setText(slide_headings[position]);
        slideDescTextView.setText(slide_descriptions[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}
