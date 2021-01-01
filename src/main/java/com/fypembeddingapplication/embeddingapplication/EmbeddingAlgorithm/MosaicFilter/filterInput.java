package com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.MosaicFilter;

public class filterInput {
    private int[] inPixles;
    private int height;
    private int width;

    public filterInput(int[] inPixles, int height, int width) {
        this.inPixles = inPixles;
        this.height = height;
        this.width = width;
    }

    public int[] getInPixles() {
        return inPixles;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
