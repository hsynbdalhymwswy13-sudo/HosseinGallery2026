package com.hossein.gallery.model;

import android.net.Uri;

public class PhotoItem {

    public Uri uri;
    public String name;
    public long size;
    public long dateAdded;
    public int width;
    public int height;
    public String mimeType;
    public String album;
    public boolean favorite;
    public boolean trashed;

    public PhotoItem(
            Uri uri,
            String name,
            long size,
            long dateAdded,
            int width,
            int height,
            String mimeType,
            String album
    ) {
        this.uri = uri;
        this.name = name;
        this.size = size;
        this.dateAdded = dateAdded;
        this.width = width;
        this.height = height;
        this.mimeType = mimeType;
        this.album = album;
        this.favorite = false;
        this.trashed = false;
    }
}
