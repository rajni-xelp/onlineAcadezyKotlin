package com.example.onlineacadezykotlin;

import java.util.List;

public class ModelCanvas {
    private PaintView paintView;
    private List<ImageDataModel> imageDataModelList;

    public PaintView getPaintView() {
        return paintView;
    }

    public void setPaintView(PaintView paintView) {
        this.paintView = paintView;
    }

    public List<ImageDataModel> getImageDataModelList() {
        return imageDataModelList;
    }

    public void setImageDataModelList(List<ImageDataModel> imageDataModelList) {
        this.imageDataModelList = imageDataModelList;
    }
}
