package com.EL.DL.NN.CNN;

public class Point {
    int red;
    int green;
    int blue;

    public Point() {
    }

    public Point(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getRed() {
        return red;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getGreen() {
        return green;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public int getBlue() {
        return blue;
    }

    public String getPoint() {
        return "(" + red + "," + green + "," + blue + ")";
    }
}
