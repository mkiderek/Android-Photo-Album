package com.example.derek.androidphotoalbum;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.NoSuchElementException;

public class AddEditAlbum extends AppCompatActivity {

    private EditText albumName;

    private String selectedAlbumName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_album);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        albumName = (EditText) findViewById(R.id.albumName);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            selectedAlbumName = bundle.getString(Keys.SELECTED_ALBUM_KEY);
            if (selectedAlbumName != null) {
                albumName.setText(selectedAlbumName);
                getSupportActionBar().setTitle("Rename Album");
//                Toast.makeText(this, "oldName:" + selectedAlbumName, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void save(View view) {
        String name = albumName.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Album name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (AlbumList.getInstance().containsAlbum(name)) {
            Toast.makeText(this, "Album already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString(Keys.ALBUM_NAME_KEY, name);
        if (selectedAlbumName != null) {
            bundle.putString(Keys.SELECTED_ALBUM_KEY, selectedAlbumName);
        }

        Intent intent = new Intent();
        intent.putExtras(bundle);

        setResult(RESULT_OK, intent);
        finish();
    }

    public void cancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
