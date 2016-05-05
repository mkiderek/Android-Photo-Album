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
public class PhotoAdapter extends BaseAdapter {
    private Context context;
    private Album.Photo[] data;

    public PhotoAdapter(Context context, Album.Photo[] data) {
        this.context = context;
        this.data = data;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return data.length;
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Album.Photo getItem(int position) {
        return data[position];
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
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
        Bitmap image = data[position].getImage(this.context);
        if (image == null) {
//            Toast.makeText(this.context, "Cannot get image of " + data[position].toString(), Toast.LENGTH_SHORT).show();
            thumbnail.setImageDrawable(new ColorDrawable(Color.GRAY));
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) this.context).getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;
            thumbnail.getLayoutParams().width = screenWidth / 2;
            thumbnail.getLayoutParams().height = screenWidth * 9 / 32;
        } else {
            thumbnail.setImageBitmap(data[position].getImage(this.context));
        }

        TextView photoName = (TextView) view.findViewById(R.id.description);
        photoName.setText(data[position].toString());

        return view;
    }
}
