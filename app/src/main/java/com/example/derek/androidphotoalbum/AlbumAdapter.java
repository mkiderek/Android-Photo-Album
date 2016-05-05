package com.example.derek.androidphotoalbum;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Derek on 4/28/16.
 */
public class AlbumAdapter extends BaseAdapter {
    private Context context;
    private Album[] data;

    public AlbumAdapter(Context context, Album[] data) {
        this.context = context;
        this.data = data;
    }


    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Album getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) this.context).getLayoutInflater();
            view = inflater.inflate(R.layout.thumbnail, null);
        } else {
            view = convertView;
        }



        ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnail);

        Bitmap preview = data[position].getPreview(this.context, null);
        if (preview == null) {
//            Toast.makeText(this.context, "Cannot get preview of " + data[position].toString(), Toast.LENGTH_SHORT).show();
            thumbnail.setImageDrawable(new ColorDrawable(Color.WHITE));
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) this.context).getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;
            thumbnail.getLayoutParams().width = screenWidth / 2;
            thumbnail.getLayoutParams().height = screenWidth * 9 / 32;
        } else {
            thumbnail.setImageBitmap(data[position].getPreview(this.context, null));
        }
        TextView albumName = (TextView) view.findViewById(R.id.description);
        albumName.setText(data[position].toString());

        return view;

//        ImageView imageView;
//        if (convertView == null) {
//            // if it's not recycled, initialize some attributes
//            imageView = new ImageView(context);
//            imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setPadding(8, 8, 8, 8);
//        } else {
//            imageView = (ImageView) convertView;
//        }
//
//        Bitmap preview = data[position].getPreview(this.context, null);
//        if (preview == null) {
//            Toast.makeText(this.context, "Cannot get preview of " + data[position].toString(), Toast.LENGTH_SHORT).show();
//            imageView.setImageDrawable(new PaintDrawable(Color.BLACK));
//        } else {
//            imageView.setImageBitmap(preview);
//        }
//
//        return imageView;
    }
}
