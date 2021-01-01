package com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.MosaicFilter;

public class Point {
    public int index;
    public float x, y;
    public float dx, dy;
    public float cubeX, cubeY;
    public float distance;
    public Point() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getDx() {
        return dx;
    }

    public void setDx(float dx) {
        this.dx = dx;
    }

    public float getDy() {
        return dy;
    }

    public void setDy(float dy) {
        this.dy = dy;
    }

    public float getCubeX() {
        return cubeX;
    }

    public void setCubeX(float cubeX) {
        this.cubeX = cubeX;
    }

    public float getCubeY() {
        return cubeY;
    }

    public void setCubeY(float cubeY) {
        this.cubeY = cubeY;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
