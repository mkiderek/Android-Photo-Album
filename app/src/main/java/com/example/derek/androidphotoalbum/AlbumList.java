package com.example.derek.androidphotoalbum;

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

    private HashMap<String, Album> albums;

    private AlbumList() {
        albums = new HashMap<>();
    }

    public static AlbumList getInstance() {
        if (albumList == null) {
            albumList = new AlbumList();
        }
        return albumList;
    }

    public void addAlbum(String albumName) throws DuplicateElementException {
        if (this.albums.containsKey(albumName)) {
            throw new DuplicateElementException("The album already exists.");
        }
        this.albums.put(albumName, new Album(albumName));
    }

    public void changeAlbumName(String oldName, String newName)
            throws NoSuchElementException, DuplicateElementException
    {
        if (!this.albums.containsKey(oldName)) {
            throw new NoSuchElementException("The old album does not exist.");
        }
        if (this.albums.containsKey(newName)) {
            throw new DuplicateElementException("The new album already exists.");
        }
        Album album = albums.get(oldName);
        album.albumName = newName;
        albums.remove(oldName);
        albums.put(newName, album);
    }

    public boolean containsAlbum(String albumName) {
        return albums.containsKey(albumName);
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

    public void removeAlbum(String albumName) throws NoSuchElementException {
        if (!this.albums.containsKey(albumName)) {
            throw new NoSuchElementException("The album does not exist");
        }
        this.albums.remove(albumName);
    }

}
