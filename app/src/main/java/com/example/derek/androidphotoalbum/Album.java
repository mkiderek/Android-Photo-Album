package com.example.derek.androidphotoalbum;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * Created by Derek on 4/6/16.
 */
public class Album implements Serializable, Comparable<Album> {

//    public static final int THUMBNAIL_SIZE = 180;

    static final long serialVersionUID = 18L;

    String albumName;
    private HashMap<String, Photo> photos;

    public static class Photo implements Serializable, Comparable<Photo> {
        private String photoName;
        private File imageFile;
        private transient Bitmap image;
//        private transient ImageView thumbnail;

        private ArrayList<Tag> tags;

        private Photo(String photoName, File imageFile) {
            this.photoName = photoName;
            this.imageFile = imageFile;
            try {
                this.image = BitmapFactory.decodeStream(new FileInputStream(this.imageFile));
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println(e.getMessage());
            }
            tags = new ArrayList<>();
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

        public Bitmap getImage() {
            return this.image;
        }

//        public ImageView getThumbnail() {
//            return this.thumbnail;
//        }

        public String toString() {
            return this.photoName;
        }

        public ArrayList<Tag> getTags() {
            return this.tags;
        }

        public void removeTag(Tag tag) {
            if (!tags.contains(tag)) {
                throw new IllegalArgumentException("The tag does not exist.");
            }
            this.tags.remove(tag);
        }
    }

    public Album(String albumName) {
        this.albumName = albumName;
        this.photos = new HashMap<>();
    }

    public void addPhoto(Photo photo)
            throws IllegalArgumentException {
        if (this.containsPhoto(photo.photoName)) {
            throw new IllegalArgumentException("The photo is already in the album");
        }
        photos.put(photo.photoName, photo);
    }

    public void addPhoto(String photoName, File imageFile)
            throws IllegalArgumentException {
        if (photos.containsKey(photoName)) {
            throw new IllegalArgumentException("The photo is already in the album.");
        }
        photos.put(photoName, new Photo(photoName, imageFile));
    }

    public void changePhotoName(String olaName, String newName)
            throws NoSuchElementException, IllegalArgumentException {
        if (!photos.containsKey(olaName)) {
            throw new NoSuchElementException("The old photo does not exist.");
        }
        if (photos.containsKey(newName)) {
            throw new IllegalArgumentException("The new photo already exists.");
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
            throws IllegalArgumentException {
        if (!photos.containsKey(photoName)) {
            throw new IllegalArgumentException("The photo does not exist.");
        }
        return photos.get(photoName);
    }

    public ArrayList<Photo> getPhotos(Comparator<Photo> comparator) {
        ArrayList<Photo> photoList = new ArrayList<>();
        photoList.addAll(this.photos.values());
        Collections.sort(photoList, comparator);
        return photoList;
    }

    public void loadImages() {
        for (Photo photo : this.photos.values()) {
            try {
                photo.image = BitmapFactory.decodeStream(new FileInputStream(photo.imageFile));
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println(e.getMessage());
            }
        }
    }

//    public void loadThumbnails() {
//        this.photos.forEach((s, photo) -> {
//            try {
//                photo.thumbnail = new ImageView(new Image(new FileInputStream(photo.imageFile)));
//                photo.thumbnail.setPreserveRatio(true);
//                photo.thumbnail.setFitWidth(Album.THUMBNAIL_SIZE);
//            } catch (IOException e) {
//                e.printStackTrace();
//                System.err.println(e.getMessage());
//            }
//        });
//    }

    public void removePhoto(String photoName)
            throws NoSuchElementException {
        if (!this.photos.containsKey(photoName)) {
            throw new NoSuchElementException("The photo does not exist");
        }
        this.photos.remove(photoName);
    }

    public String toString() {
        return this.albumName;
    }

}
