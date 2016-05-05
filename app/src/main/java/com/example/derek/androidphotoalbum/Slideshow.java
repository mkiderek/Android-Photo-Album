package com.example.derek.androidphotoalbum;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Slideshow extends AppCompatActivity {

    private ViewPager slideshowPager;
    private FragmentStatePagerAdapter slideshowPagerAdapter;
//    private ListView tags;

    private String albumName;
    int initPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            Toast.makeText(this, "Cannot open slideshow", Toast.LENGTH_SHORT).show();
            finish();
        }

        albumName = bundle.getString(Keys.SELECTED_ALBUM_KEY);
        initPos = bundle.getInt(Keys.SELECTED_POSITION_KEY);

        slideshowPager = (ViewPager) findViewById(R.id.slideshow);
//        tags = (ListView) findViewById(R.id.photoTags);
        slideshowPagerAdapter = new SlideshowPagerAdapter(getSupportFragmentManager(), albumName, this);
        slideshowPager.setAdapter(slideshowPagerAdapter);
        slideshowPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Album.Photo photo = AlbumList.getInstance().getAlbum(albumName).getPhotos(null).get(position);
                getSupportActionBar().setTitle(photo.toString());
//                List<String> tagsContent = new ArrayList<>(photo.getTags().size());
//                for (Album.Photo.Tag tag : photo.getTags()) {
//                    tagsContent.add(tag.toString());
//                }
//                tags.setAdapter(new ArrayAdapter<String>(Slideshow.this, R.layout.list_text, tagsContent));
            }
        });
        slideshowPager.setCurrentItem(initPos);
        Album.Photo photo = AlbumList.getInstance().getAlbum(albumName).getPhotos(null).get(initPos);
        getSupportActionBar().setTitle(photo.toString());
//        List<String> tagsContent = new ArrayList<>(photo.getTags().size());
//        for (Album.Photo.Tag tag : photo.getTags()) {
//            tagsContent.add(tag.toString());
//        }
//        tags.setAdapter(new ArrayAdapter<String>(Slideshow.this, R.layout.list_text, tagsContent));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        Bundle bundle = data.getExtras();
//        if (bundle == null) {
//            return;
//        }

        if (requestCode == RequestCodes.ADD_TAG_CODE) {
            String tagType = bundle.getString(Keys.TAG_TYPE_KEY);
            String tagValue = bundle.getString(Keys.TAG_VALUE_KEY);
            Album.Photo photo = AlbumList.getInstance().getAlbum(albumName).getPhotos(null).get(slideshowPager.getCurrentItem());
            System.out.println(bundle.toString());
            if (tagType.equals("PERSON")) {
                photo.addTag(new Album.Photo.Tag(Album.Photo.Tag.TagType.PERSON, tagValue));
            } else if (tagType.equals("LOCATION")) {
                photo.addTag(new Album.Photo.Tag(Album.Photo.Tag.TagType.LOCATION, tagValue));
            } else {
                // should not happen
                Toast.makeText(this, "Illegal Tag Type", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_edit_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Bundle bundle = new Bundle();
                bundle.putString(Keys.SELECTED_ALBUM_KEY, albumName);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
                return true;

            case R.id.action_add:
                addTag();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addTag() {
        Bundle bundle = new Bundle();
        bundle.putString(Keys.SELECTED_ALBUM_KEY, albumName);
        String photoName = AlbumList.getInstance().getAlbum(albumName).getPhotos(null).get(slideshowPager.getCurrentItem()).toString();
        bundle.putString(Keys.SELECTED_PHOTO_KEY, photoName);
        Intent intent = new Intent(this, AddTag.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, RequestCodes.ADD_TAG_CODE);
    }

    private void showTags() {

    }

    private void editTag() {

    }
}
