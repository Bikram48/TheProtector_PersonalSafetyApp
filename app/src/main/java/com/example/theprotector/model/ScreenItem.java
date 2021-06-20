package com.example.theprotector.model;

public class ScreenItem {
    String title,Description;
    int ScreenImg;
    int floatingImg;

    public ScreenItem(String title, String description, int screenImg, int floatingImg) {
        this.title = title;
        Description = description;
        ScreenImg = screenImg;
        this.floatingImg = floatingImg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getScreenImg() {
        return ScreenImg;
    }

    public void setScreenImg(int screenImg) {
        ScreenImg = screenImg;
    }

    public int getFloatingImg() {
        return floatingImg;
    }

    public void setFloatingImg(int floatingImg) {
        this.floatingImg = floatingImg;
    }
}
