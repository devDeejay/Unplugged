package com.devdelhi.unplugged;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MoreTipsSliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public MoreTipsSliderAdapter(Context context) {
        this.context = context;
    }

    public int[] slide_images = {
            R.drawable.checked,
            R.drawable.phone_lock,
            R.drawable.secure_usb,
            R.drawable.alarm_black
    };

    public String[] slide_headings = {
            "UnPlugged Tips",
            "Lock Your Device",
            "USB Will Perform Better",
            "Stop The Alarm"
    };

    public String[] slide_descriptions = {
            "Some Tips and Tricks By The Developer and The Community To Give You Maximum Assurance Of Safety.",
            "Lock Your Device After Turning On The Alarm. This Will Protect Your Device More Because The Thief Wont Be Able To Turn Off The Alarm By Closing The App",
            "Audio Jacks Are More Rigid And Hence Might Not Be That Sensitive As Compared To USB. Hence, If You Have A Choice, Choose USB.",
            "To Stop The Alarm You Can Either Plug Your Device Back In Or You Can Close The App. The Option To Shut The Alarm Along With The App Makes Our Protective App Less Irritating To The Users It Is Protecting",
    };

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
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
