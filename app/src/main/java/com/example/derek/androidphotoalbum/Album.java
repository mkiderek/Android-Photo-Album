package com.example.derek.androidphotoalbum;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Derek on 4/6/16.
 */
public class Album implements Serializable, Comparable<Album> {

    public static class Photo implements Serializable, Comparable<Photo> {

        public static class Tag implements Serializable {

            public enum TagType {
                PERSON, LOCATION;
            }

            static final long serialVersionUID = 20L;
            private TagType tagType;
            private String value;

            public Tag(TagType tagType, String value) {
                this.tagType = tagType;
                this.value = value;
            }

            public TagType getTagType() {
                return this.tagType;
            }

            public String getValue() {
                return this.value;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj != null && obj instanceof Tag) {
                    Tag o = (Tag) obj;
                    return this.value.equals(o.value) && this.tagType == o.tagType;
                }
                return false;
            }

            public String toString() {
                return this.tagType.toString() + ":" + this.value;
            }

        }

        static final long serialVersionUID = 19L;
        private String photoName;
        private String imagePath;
        private List<Tag> tags;
        transient private Bitmap image;

        private Photo(String photoName, String imagePath) {
            this.photoName = photoName;
            this.imagePath = imagePath;
            this.tags = new ArrayList<>();
        }

        public void addTag(Tag tag) {
            if (tags.contains(tag)) {
                throw new IllegalArgumentException("The tag already exists.");
            }
            this.tags.add(tag);
        }

        @Override
        public int compareTo(Photo o) {
            return this.toString().compareTo(o.toString());
        }

        @Override
        public boolean equals(Object o) {
            if (o != null && o instanceof Photo) {
                Photo other = (Photo) o;
                return this.photoName.equals(other.photoName)
                        || this.imagePath.equals(other.imagePath);
            }
            return false;
        }

        public Bitmap getImage(Context context) {
            if (image == null) {
                Uri uri = Uri.parse(imagePath);
                context.getApplicationContext().getContentResolver().takePersistableUriPermission(uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                try {
                    ParcelFileDescriptor parcelFileDescriptor =
                            context.getApplicationContext().getContentResolver().openFileDescriptor(uri, "r");
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                    image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                    parcelFileDescriptor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    image = null;
                }
            }
            return image;
        }

        public String toString() {
            return this.photoName;
        }

        public List<Tag> getTags() {
            return this.tags;
        }

        public void removeTag(Tag tag)
                throws NoSuchElementException {
            if (!tags.contains(tag)) {
                throw new NoSuchElementException("The tag does not exist.");
            }
            this.tags.remove(tag);
        }
    }

    static final long serialVersionUID = 18L;
    private String albumName;
    private HashMap<String, Photo> photos;

    public Album(String albumName) {
        this.albumName = albumName;
        this.photos = new HashMap<>();
    }

    public void addPhoto(Photo photo)
            throws DuplicateElementException {
        if (this.containsPhoto(photo.photoName)) {
            throw new DuplicateElementException("The photo is already in the album");
        }
        photos.put(photo.photoName, photo);
    }

    public void addPhoto(String photoName, String imagePath)
            throws DuplicateElementException {
        if (photos.containsKey(photoName)) {
            throw new DuplicateElementException("The photo is already in the album.");
        }
        photos.put(photoName, new Photo(photoName, imagePath));
    }

    public void changePhotoName(String olaName, String newName)
            throws NoSuchElementException, DuplicateElementException {
        if (!photos.containsKey(olaName)) {
            throw new NoSuchElementException("The old photo does not exist.");
        }
        if (photos.containsKey(newName)) {
            throw new DuplicateElementException("The new photo already exists.");
        }
        Photo photo = photos.get(olaName);
        photo.photoName = newName;
        photos.remove(olaName);
        photos.put(newName, photo);
    }

    @Override
    public int compareTo(Album o) {
        return this.toString().compareTo(o.toString());
    }

    public boolean containsPhoto(String photoName) {
        return photos.containsKey(photoName);
    }

    public Photo getPhoto(String photoName)
            throws NoSuchElementException {
        if (!photos.containsKey(photoName)) {
            throw new NoSuchElementException("The photo does not exist.");
        }
        return photos.get(photoName);
    }

    public ArrayList<Photo> getPhotos(Comparator<Photo> comparator) {
        ArrayList<Photo> photoList = new ArrayList<>();
        photoList.addAll(this.photos.values());
        Collections.sort(photoList, comparator);
        return photoList;
    }

    public Bitmap getPreview(Context context, Comparator<Photo> comparator) {
        if (this.photos.isEmpty()) {
            return null;
        }
        return this.getPhotos(comparator).get(0).getImage(context);
    }

    public void removePhoto(String photoName)
            throws NoSuchElementException {
        if (!this.photos.containsKey(photoName)) {
            throw new NoSuchElementException("The photo does not exist");
        }
        this.photos.remove(photoName);
    }

    public void setAlbumName(String newName) {
        this.albumName = newName;
    }

    public int size() {
        return this.photos.size();
    }

    public String toString() {
        return this.albumName;
    }

}
