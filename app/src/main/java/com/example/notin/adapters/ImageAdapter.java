package com.example.notin.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.notin.R;

public class ImageAdapter extends PagerAdapter {

    private Context mContext;
    private int[] mImageIds=new int[] {R.drawable.google,R.drawable.avatar};

    public ImageAdapter(Context context){
        mContext=context;
    }
    @Override
    public int getCount() {
        return mImageIds.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageview=new ImageView(mContext);
        imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageview.setImageResource(mImageIds[position]);
        container.addView(imageview,0);
        return imageview;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
    }
}
