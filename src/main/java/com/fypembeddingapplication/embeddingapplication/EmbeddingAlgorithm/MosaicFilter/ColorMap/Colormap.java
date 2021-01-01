package com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.MosaicFilter.ColorMap;

public interface Colormap {
    /**
     * Convert a value in the range 0..1 to an RGB color.
     * @param v a value in the range 0..1
     * @return an RGB color
     */
    public int getColor(float v);
}

