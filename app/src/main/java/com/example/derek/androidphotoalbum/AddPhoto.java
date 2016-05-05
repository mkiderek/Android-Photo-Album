package com.example.derek.androidphotoalbum;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

public class AddPhoto extends AppCompatActivity {

    private final int SELECT_FILE_CODE = 34;


    //    private String imagePath;
    private Uri imageUri;

    private EditText photoName;
    private ImageView photoPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

//        imagePath = null;
        imageUri = null;
        photoName = (EditText) findViewById(R.id.description);
        photoPreview = (ImageView) findViewById(R.id.photoPreview);
    }

    public void save(View view) {
//        if (imagePath == null) {
        if (imageUri == null) {
            Toast.makeText(this, "Please select a image file", Toast.LENGTH_SHORT).show();
            return;
        }

//        if (BitmapFactory.decodeFile(imagePath) == null) {
//            Toast.makeText(this, "The file format is not supported", Toast.LENGTH_SHORT).show();
//            return;
//        }

        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getApplicationContext().getContentResolver().openFileDescriptor(imageUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap preview = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            if (preview == null) {
                Toast.makeText(this, "The file format is not supported", Toast.LENGTH_SHORT).show();
                return;
            }
            photoPreview.setImageBitmap(preview);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String name = photoName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "The name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
        // Check for the freshest data.
        getApplicationContext().getContentResolver().takePersistableUriPermission(imageUri, takeFlags);

        Bundle bundle = new Bundle();
        bundle.putString(Keys.PHOTO_NAME_KEY, name);
        bundle.putString(Keys.IMAGE_PATH_KEY, imageUri.toString());

        Intent intent = new Intent();
        intent.putExtras(bundle);

        setResult(RESULT_OK, intent);
        finish();
    }

    public void cancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void chooseFile(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_FILE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_FILE_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
            }

//            imagePath = data.getData().getPath();

            try {
                ParcelFileDescriptor parcelFileDescriptor =
                        getApplicationContext().getContentResolver().openFileDescriptor(imageUri, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                Bitmap preview = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                parcelFileDescriptor.close();
                if (preview == null) {
                    Toast.makeText(this, "The file format is not supported", Toast.LENGTH_SHORT).show();
                    return;
                }
                photoPreview.setImageBitmap(preview);
            } catch (IOException e) {
                e.printStackTrace();
            }

//            Bitmap preview = BitmapFactory.decodeFile(imagePath);
        }

        super.onActivityResult(requestCode, resultCode, data);
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
}
