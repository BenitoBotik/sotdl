package com.example.example_project.ui.model;

import java.io.Serializable;

public class Icon implements Serializable {
    private String image;
    private float x;
    private float y;

    // keep this constructor empty for Firebase
    public Icon() {
    }

    public Icon(String image, float x, float y) {
        this.image = image;
        this.x = x;
        this.y = y;
    }

    public String getImage() {
        return image;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void SetX(float x) {
        this.x = x;
    }

    public void SetY(float y) {
        this.y = y;
    }
}
