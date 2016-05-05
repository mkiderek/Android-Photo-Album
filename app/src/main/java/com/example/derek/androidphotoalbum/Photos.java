package com.example.derek.androidphotoalbum;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.NoSuchElementException;

public class Photos extends AppCompatActivity
        implements SelectAlbumDialogFragment.SelectAlbumDialogListener, ConfirmDialogFragment.ConfirmDialogListener {

    private GridView photoAlbumGridView;
    private AlbumList albumList;
    private Album currentAlbum;

    private Album.Photo movedPhoto;

    List<Album.Photo> deletePhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_album);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        photoAlbumGridView = (GridView) findViewById(R.id.albumsPhotosView);
        albumList = AlbumList.getInstance();

        handleIntent(getIntent());

        photoAlbumGridView.setOnItemClickListener((parent, view, position, id) -> {
            // open slide show
            openSlideshow(position);
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
                            edit.setTitle("Move");
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
                                deleteSelectedPhotos();
                                mode.finish();
                                return true;

                            case R.id.action_edit:
                                SparseBooleanArray sba = photoAlbumGridView.getCheckedItemPositions();
                                for (int i = 0; i < sba.size(); i++) {
                                    if (sba.valueAt(i)) {
                                        movedPhoto = (Album.Photo) photoAlbumGridView.getItemAtPosition(sba.keyAt(i));
                                        movePhoto();
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
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            if (currentAlbum == null) {
                Toast.makeText(this, "2 null", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                showPhotos();
            }
        } else {
            try {
                currentAlbum = albumList.getAlbum(bundle.getString(Keys.SELECTED_ALBUM_KEY));
                showPhotos();
            } catch (NoSuchElementException e) {
                Toast.makeText(this, "Cannot open album", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
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

        if (requestCode == RequestCodes.ADD_PHOTO_CODE) { // save photo
            String name = bundle.getString(Keys.PHOTO_NAME_KEY);
            String uri = bundle.getString(Keys.IMAGE_PATH_KEY);
            try {
                currentAlbum.addPhoto(name, uri);
                albumList.save();
            } catch (DuplicateElementException e) {
                Toast.makeText(this, "This album contains same photo", Toast.LENGTH_SHORT).show();
            }
            showPhotos(); // display new data
        } else if (requestCode == RequestCodes.OPEN_SLIDESHOW_CODE) {
//            Toast.makeText(this, "Return from slideshow", Toast.LENGTH_SHORT).show();
            showPhotos(); // display new data
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
                addPhoto();
                return true;

            case android.R.id.home:
                setResult(RESULT_OK, new Intent());
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addPhoto() {
        Intent intent = new Intent(this, AddPhoto.class);
        startActivityForResult(intent, RequestCodes.ADD_PHOTO_CODE);
    }

    private void doDelete() {
        for (Album.Photo photo : deletePhotos) {
            currentAlbum.removePhoto(photo.toString());
        }
        albumList.save();
        showPhotos();
    }

    private void deleteSelectedPhotos() {
        SparseBooleanArray sba = photoAlbumGridView.getCheckedItemPositions();
        deletePhotos = new ArrayList<>();
        for (int i = 0; i < sba.size(); i++) {
            if (sba.valueAt(i)) {
                Album.Photo photo = (Album.Photo) photoAlbumGridView.getItemAtPosition(sba.keyAt(i));
                deletePhotos.add(photo);
            }
        }
        confirmRemovePhoto("Remove selected photo(s)?");
    }

    private void confirmRemovePhoto(String message) {
        DialogFragment dialogFragment = ConfirmDialogFragment.newInstance(message);
        dialogFragment.show(getFragmentManager(), "remove_photo");
    }

    @Override
    public void onDialogPositiveClick(ConfirmDialogFragment dialog) {
        doDelete();
    }

    @Override
    public void onDialogNegativeClick(ConfirmDialogFragment dialog) {
        // operation canceled
    }

    private void doMove(Album targetAlbum) {
        try {
            targetAlbum.addPhoto(movedPhoto);
            currentAlbum.removePhoto(movedPhoto.toString());
        } catch (DuplicateElementException e) {
            Toast.makeText(this, "target album contains same image", Toast.LENGTH_SHORT).show();
        }
        albumList.save();
        showPhotos();
    }

    private void movePhoto() {
        DialogFragment dialogFragment = new SelectAlbumDialogFragment();
        dialogFragment.show(getFragmentManager(), "select_album");
    }

    @Override
    public void onDialogPositiveClick(SelectAlbumDialogFragment dialog) {
        doMove(AlbumList.getInstance().getAlbums(null).get(dialog.index));
    }

    @Override
    public void onDialogNegativeClick(SelectAlbumDialogFragment dialog) {
        // operation canceled
    }

    private void openSlideshow(int initPos) {
        Bundle bundle = new Bundle();
        bundle.putString(Keys.SELECTED_ALBUM_KEY, currentAlbum.toString());
        bundle.putInt(Keys.SELECTED_POSITION_KEY, initPos);
        Intent intent = new Intent(this, Slideshow.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, RequestCodes.OPEN_SLIDESHOW_CODE);
    }

    private void showPhotos() {
        Album.Photo[] data = new Album.Photo[currentAlbum.size()];
        currentAlbum.getPhotos(null).toArray(data);
        photoAlbumGridView.setAdapter(new PhotoAdapter(this, data));

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(currentAlbum.toString());
        ab.show();
    }

    private void searchPhotos() {
        Intent intent = new Intent(this, AddTag.class);
        startActivityForResult(intent, RequestCodes.SEARCH_PHOTO_CODE);
    }
}
