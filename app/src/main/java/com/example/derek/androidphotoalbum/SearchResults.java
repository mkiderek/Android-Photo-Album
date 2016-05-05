package com.example.derek.androidphotoalbum;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ListView;

import java.util.List;

public class SearchResults extends AppCompatActivity {

    private GridView photoAlbumGridView;
    List<Album.Photo> photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_album);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        photoAlbumGridView = (GridView) findViewById(R.id.albumsPhotosView);

        Bundle bundle = getIntent().getExtras();
        photos = (List<Album.Photo>) bundle.getSerializable(Keys.SEARCH_RESULT_KEY);
        if (photos == null) {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_OK, new Intent());
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showPhotos() {
        Album.Photo[] data = new Album.Photo[photos.size()];
        photos.toArray(data);
        photoAlbumGridView.setAdapter(new PhotoAdapter(this, data));

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Search Result");
        ab.show();
    }
}
