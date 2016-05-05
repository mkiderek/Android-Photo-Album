package com.example.derek.androidphotoalbum;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Derek on 5/4/16.
 */
public class SlideshowPagerAdapter extends FragmentStatePagerAdapter {

    public static class SlideshowObjectFragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.photo_detail, container, false);
//            View rootView = inflater.inflate(R.layout.activity_slideshow, container, false);
            Bundle args = getArguments();
            TextView name = (TextView) rootView.findViewById(R.id.photoName);
            ImageView image = (ImageView) rootView.findViewById(R.id.photoImage);
            ListView tags = (ListView) rootView.findViewById(R.id.photoTags);

            Album currentAlbum = AlbumList.getInstance().getAlbum(args.getString(Keys.CURRENT_ALBUM_KEY));
            String photoName = args.getString(Keys.CURRENT_PHOTO_KEY);
            Album.Photo photo = currentAlbum.getPhoto(photoName);
            name.setText(photo.toString());
            image.setImageBitmap(photo.getImage(getContext()));
            List<String> tagsContent = new ArrayList<>(photo.getTags().size());
            for (Album.Photo.Tag tag : photo.getTags()) {
                tagsContent.add(tag.toString());
            }
            tags.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.list_text, tagsContent));
            return rootView;
        }
    }

    Context context;
    String albumName;

    public SlideshowPagerAdapter(FragmentManager fm, String albumName, Context context) {
        super(fm);
        this.albumName = albumName;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new SlideshowObjectFragment();
        Bundle args = new Bundle();
        args.putString(Keys.CURRENT_ALBUM_KEY, this.albumName);
        String photoName = AlbumList.getInstance().getAlbum(this.albumName).getPhotos(null).get(position).toString();
        args.putString(Keys.CURRENT_PHOTO_KEY, photoName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return AlbumList.getInstance().getAlbum(this.albumName).size();
    }
}
