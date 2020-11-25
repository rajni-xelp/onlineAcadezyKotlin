package com.example.onlineacadezykotlin;

import android.graphics.Bitmap;

public class UploadedImageDetails {
    public Bitmap bitmap;
    public int heightFromTop;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getHeightFromTop() {
        return heightFromTop;
    }

    public void setHeightFromTop(int heightFromTop) {
        this.heightFromTop = heightFromTop;
    }
}
