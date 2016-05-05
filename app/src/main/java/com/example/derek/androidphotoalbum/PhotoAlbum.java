package com.example.derek.androidphotoalbum;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PhotoAlbum extends AppCompatActivity
        implements ConfirmDialogFragment.ConfirmDialogListener{

    private GridView photoAlbumGridView;
    private AlbumList albumList;

    private List<Album> deleteAlbums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        Toast.makeText(this, "onCreate()", Toast.LENGTH_SHORT).show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_album);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        AlbumList.init(this, getIntent());
        albumList = AlbumList.getInstance();

        photoAlbumGridView = (GridView) findViewById(R.id.albumsPhotosView);

        handleIntent(getIntent());

        photoAlbumGridView.setOnItemClickListener((parent, view, position, id) -> {
            openAlbum(albumList.getAlbums(null).get(position));
        });

        photoAlbumGridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        photoAlbumGridView.setMultiChoiceModeListener(
                new AbsListView.MultiChoiceModeListener() {
                    @Override
                    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                        int checkedCount = photoAlbumGridView.getCheckedItemCount();
                        mode.setTitle(checkedCount + " selected");
                        ActionMenuItemView edit = (ActionMenuItemView) findViewById(R.id.action_edit);
                        if (edit != null) {
                            if (checkedCount == 1) {
                                edit.setEnabled(true);
                                edit.setVisibility(View.VISIBLE);
                            } else {
                                edit.setEnabled(false);
                                edit.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        mode.getMenuInflater().inflate(R.menu.delete_edit_menu, menu);
                        ActionMenuItemView edit = (ActionMenuItemView) findViewById(R.id.action_edit);
                        if (edit != null) {
                            edit.setTitle("Rename");
                        }
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_delete:
                                deleteSelectedAlbums();
                                mode.finish();
                                return true;

                            case R.id.action_edit:
                                SparseBooleanArray sba = photoAlbumGridView.getCheckedItemPositions();
                                for (int i = 0; i < sba.size(); i++) {
                                    if (sba.valueAt(i)) {
                                        renameAlbum((Album) photoAlbumGridView.getItemAtPosition(sba.keyAt(i)));
                                    }
                                }
                                mode.finish();
                                return true;

                            default:
                                return false;
                        }
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {

                    }
                }
        );
    }

    private void handleIntent(Intent intent) {

//        Toast.makeText(this, "handleIntent()", Toast.LENGTH_SHORT).show();


        showAlbums();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

//        Toast.makeText(this, "onActivityResult()", Toast.LENGTH_SHORT).show();

        if (resultCode == RESULT_CANCELED) {
            return;
        }

        Bundle bundle = data.getExtras();
//        if (bundle == null) {
//            return;
//        }

        if (requestCode == RequestCodes.ADD_ALBUM_CODE) { // add album
            String name = bundle.getString(Keys.ALBUM_NAME_KEY);
            try {
                albumList.addAlbum(name);
                albumList.save();
            } catch (DuplicateElementException e) {
                Toast.makeText(this, "Album already exists", Toast.LENGTH_SHORT).show();
            }
            showAlbums(); // display new data
        } else if (requestCode == RequestCodes.RENAME_ALBUM_CODE) {
            String oldName = bundle.getString(Keys.SELECTED_ALBUM_KEY);
            String newName = bundle.getString(Keys.ALBUM_NAME_KEY);
            try {
                albumList.renameAlbum(oldName, newName);
                albumList.save();
            } catch (DuplicateElementException e) {
                Toast.makeText(this, "Album already exists", Toast.LENGTH_SHORT).show();
            }
            showAlbums();
        } else if (requestCode == RequestCodes.SHOW_ALBUM_CODE) {
//            Toast.makeText(this, "return from album", Toast.LENGTH_SHORT).show();
            showAlbums();
        } else if (requestCode == RequestCodes.SEARCH_PHOTO_CODE) {
            String tagType = bundle.getString(Keys.TAG_TYPE_KEY);
            String tagValue = bundle.getString(Keys.TAG_VALUE_KEY);
            Album.Photo.Tag.TagType type;
            if (tagType.equals("PERSON")) {
                type = Album.Photo.Tag.TagType.PERSON;
            } else {
                type = Album.Photo.Tag.TagType.LOCATION;
            }
            Album.Photo.Tag target = new Album.Photo.Tag(type, tagValue);
            ArrayList<Album.Photo> results = new ArrayList<>();
            List<Album> albums = AlbumList.getInstance().getAlbums(null);
            for (Album album : albums) {
                List<Album.Photo> photos = album.getPhotos(null);
                for (Album.Photo photo : photos) {
                    List<Album.Photo.Tag> tags = photo.getTags();
                    for (Album.Photo.Tag tag : tags) {
                        if (tag.equals(target)) {
                            results.add(photo);
                        }
                    }
                }
            }
            Bundle args = new Bundle();
            args.putSerializable(Keys.SEARCH_RESULT_KEY, results);
            Intent intent = new Intent(this, SearchResults.class);
            intent.putExtras(args);
            startActivityForResult(intent, RequestCodes.SHOW_SEARCH_RESULT_CODE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);

        // Configure the search info and save any event listeners...


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                addAlbum();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addAlbum() {
        Intent intent = new Intent(this, AddEditAlbum.class);
        startActivityForResult(intent, RequestCodes.ADD_ALBUM_CODE);
    }

    private void doDelete() {
        for (Album album : deleteAlbums) {
            AlbumList.getInstance().removeAlbum(album.toString());
        }
        albumList.save();
        showAlbums();
    }

    private void deleteSelectedAlbums() {
        SparseBooleanArray sba = photoAlbumGridView.getCheckedItemPositions();
        deleteAlbums = new ArrayList<>();
        for (int i = 0; i < sba.size(); i++) {
            if (sba.valueAt(i)) {
                Album album = (Album) photoAlbumGridView.getItemAtPosition(sba.keyAt(i));
                deleteAlbums.add(album);
            }
        }
        confirmRemoveAlbum("Remove selected album(s)?");
    }

    private void renameAlbum(Album album) {
        Bundle bundle = new Bundle();
        bundle.putString(Keys.SELECTED_ALBUM_KEY, album.toString());
        Intent intent = new Intent(this, AddEditAlbum.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, RequestCodes.RENAME_ALBUM_CODE);
    }

    private void showAlbums() {

//        Toast.makeText(this, "showAlbums()", Toast.LENGTH_SHORT).show();

        List<Album> albums = albumList.getAlbums(null);
        Album[] data = new Album[albums.size()];
        albums.toArray(data);
        photoAlbumGridView.setAdapter(new AlbumAdapter(this, data));

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setTitle("");
            ab.show();
        }
    }

    private void confirmRemoveAlbum(String message) {
        DialogFragment dialogFragment = ConfirmDialogFragment.newInstance(message);
        dialogFragment.show(getFragmentManager(), "remove_album");
    }

    @Override
    public void onDialogPositiveClick(ConfirmDialogFragment dialog) {
        doDelete();
    }

    @Override
    public void onDialogNegativeClick(ConfirmDialogFragment dialog) {
        // operation canceled
    }

    private void openAlbum(Album album) {
        Intent intent = new Intent(this, Photos.class);
        Bundle bundle = new Bundle();
        bundle.putString(Keys.SELECTED_ALBUM_KEY, album.toString());
        intent.putExtras(bundle);
        startActivityForResult(intent, RequestCodes.SHOW_ALBUM_CODE);
    }

    private void searchPhotos() {
        Intent intent = new Intent(this, AddTag.class);
        startActivityForResult(intent, RequestCodes.SEARCH_PHOTO_CODE);
    }
}
