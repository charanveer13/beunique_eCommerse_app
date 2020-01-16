package com.smartit.beunique.adapters;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smartit.beunique.R;

import java.util.ArrayList;

public class DashboardViewPagerBannerAdapter extends PagerAdapter {

    private ArrayList<Integer> IMAGES;
    private Context context;

    public DashboardViewPagerBannerAdapter(Context context, ArrayList<Integer> IMAGES) {
        this.context = context;
        this.IMAGES = IMAGES;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, int position) {
        View imageLayout = LayoutInflater.from(context).inflate(R.layout.dashboard_viewpager_banner_layout, view, false);
        //assert imageLayout != null;
        final ImageView imageView = imageLayout.findViewById(R.id.image);
        imageView.setImageResource(IMAGES.get(position));
        view.addView(imageLayout);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}
