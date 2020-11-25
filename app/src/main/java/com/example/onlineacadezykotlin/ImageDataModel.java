package com.example.onlineacadezykotlin;

import android.graphics.Bitmap;

public class ImageDataModel {
    private Bitmap bitmap;
    private float amountX;
    private float amountY;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public float getAmountX() {
        return amountX;
    }

    public void setAmountX(float amountX) {
        this.amountX = amountX;
    }

    public float getAmountY() {
        return amountY;
    }

    public void setAmountY(float amountY) {
        this.amountY = amountY;
    }
}
