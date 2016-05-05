package com.example.derek.androidphotoalbum;

import android.content.Context;
import android.content.Intent;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * Created by Derek on 4/23/16.
 */
public class AlbumList implements Serializable {

    private static AlbumList albumList = null;
    private static Context currentContext;

    private HashMap<String, Album> albums;

    private final int takeFlags;

    private AlbumList(Intent intent) {
        albums = new HashMap<>();
        takeFlags = intent.getFlags()
                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    }

    public static void init(Context context, Intent intent) {
        currentContext = context;
        loadObject(intent);
    }

    public static AlbumList getInstance() {
        return albumList;
    }

    public void addAlbum(String albumName) throws DuplicateElementException {
        if (this.albums.containsKey(albumName)) {
            throw new DuplicateElementException("The album already exists.");
        }
        this.albums.put(albumName, new Album(albumName));
    }

    public boolean containsAlbum(String albumName) {
        return this.albums.containsKey(albumName);
    }

    public Album getAlbum(String albumName) throws NoSuchElementException {
        if (!this.albums.containsKey(albumName)) {
            throw new NoSuchElementException("The album does not exist.");
        }
        return this.albums.get(albumName);
    }

    public ArrayList<Album> getAlbums(Comparator<Album> comparator) {
        ArrayList<Album> albumList = new ArrayList<>();
        albumList.addAll(this.albums.values());
        Collections.sort(albumList, comparator);
        return albumList;
    }

    public int getTakeFlags() {
        return this.takeFlags;
    }

    private static void loadObject(Intent intent) {
        try {
            FileInputStream fis = currentContext.openFileInput(Keys.DATA_FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            albumList = (AlbumList) ois.readObject();
            fis.close();
        } catch (ClassNotFoundException e) {
            System.err.println("Data file broken");
            e.printStackTrace();
            albumList = new AlbumList(intent);
        } catch (IOException e) {
            System.err.println("Cannot read data from data file");
            e.printStackTrace();
            albumList = new AlbumList(intent);
        }
    }

    public void renameAlbum(String oldName, String newName)
            throws NoSuchElementException, DuplicateElementException
    {
        if (!this.albums.containsKey(oldName)) {
            throw new NoSuchElementException("The old album does not exist.");
        }
        if (this.albums.containsKey(newName)) {
            throw new DuplicateElementException("The new album already exists.");
        }
        Album album = albums.get(oldName);
        album.setAlbumName(newName);
        albums.remove(oldName);
        albums.put(newName, album);
    }

    public void save() {
        try {
            FileOutputStream fos = currentContext.openFileOutput(Keys.DATA_FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            fos.close();
        } catch (IOException e) {
            System.err.println("Cannot save data to data file");
            e.printStackTrace();
        }
    }

    public void removeAlbum(String albumName) throws NoSuchElementException {
        if (!this.albums.containsKey(albumName)) {
            throw new NoSuchElementException("The album does not exist");
        }
        this.albums.remove(albumName);
    }

}
