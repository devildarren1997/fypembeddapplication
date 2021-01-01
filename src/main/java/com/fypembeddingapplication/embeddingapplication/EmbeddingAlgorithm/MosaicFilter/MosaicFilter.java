package com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.MosaicFilter;


import com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.MosaicFilter.ColorMap.Colormap;
import com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.MosaicFilter.Gradient.Gradient;
import com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.MosaicFilter.ImageMath.ImageMath;
import com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.MosaicFilter.Noise.Noise;
import com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.MosaicFilter.Point;
import java.util.ArrayList;
import java.util.Random;



public class MosaicFilter {

    protected float scale = 50;
    protected float stretch = 1.0f;
    protected float angle = 0.0f;
    public float amount = 1.0f;
    public float turbulence = 1.0f;
    public float distancePower = 2;
    protected Colormap colormap = new Gradient();
    protected float[] coefficients = { 1, 0, 0, 0 };
    protected float angleCoefficient;
    protected Random random = new Random();
    protected float m00 = 1.0f;
    protected float m01 = 0.0f;
    protected float m10 = 0.0f;
    protected float m11 = 1.0f;
    protected Point[] results;
    protected float randomness = 0.5f;
    protected int gridType = RANDOM;
    private static byte[] probabilities;
    private float gradientCoefficient;

    public final static int RANDOM = 0;
    public final static int SQUARE = 1;
    public final static int HEXAGONAL = 2;
    public final static int OCTAGONAL = 3;
    public final static int TRIANGULAR = 4;
    private float edgeThickness = 0.4f;
    private ArrayList<Integer> embeddedList;
    public MosaicFilter(ArrayList<Integer> embeddedList) {
        results = new Point[3];
        for (int j = 0; j < results.length; j++)
            results[j] = new Point();
        if (probabilities == null) {
            probabilities = new byte[8192];
            float factorial = 1;
            float total = 0;
            float mean = 2.5f;
            for (int i = 0; i < 10; i++) {
                if (i > 1)
                    factorial *= i;
                float probability = (float)Math.pow(mean, i) * (float)Math.exp(-mean) / factorial;
                int start = (int)(total * 8192);
                total += probability;
                int end = (int)(total * 8192);
                for (int j = start; j < end; j++)
                    probabilities[j] = (byte)i;
            }
        }
        this.embeddedList = embeddedList;
    }

    private float checkCube(float x, float y, int cubeX, int cubeY, Point[] results) {
        int numPoints;
        random.setSeed(571*cubeX + 23*cubeY);
        switch (gridType) {
            case RANDOM:
            default:
                numPoints = probabilities[random.nextInt() & 0x1fff];
                break;
            case SQUARE:
                numPoints = 1;
                break;
            case HEXAGONAL:
                numPoints = 1;
                break;
            case OCTAGONAL:
                numPoints = 2;
                break;
            case TRIANGULAR:
                numPoints = 2;
                break;
        }
        for (int i = 0; i < numPoints; i++) {
            float px = 0, py = 0;
            float weight = 1.0f;
            switch (gridType) {
                case RANDOM:
                    px = random.nextFloat();
                    py = random.nextFloat();
                    break;
                case SQUARE:
                    px = py = 0.5f;
                    if (randomness != 0) {
                        px += randomness * (random.nextFloat()-0.5);
                        py += randomness * (random.nextFloat()-0.5);
                    }
                    break;
                case HEXAGONAL:
                    if ((cubeX & 1) == 0) {
                        px = 0.75f; py = 0;
                    } else {
                        px = 0.75f; py = 0.5f;
                    }
                    if (randomness != 0) {
                        px += randomness * Noise.noise2(271*(cubeX+px), 271*(cubeY+py));
                        py += randomness * Noise.noise2(271*(cubeX+px)+89, 271*(cubeY+py)+137);
                    }
                    break;
                case OCTAGONAL:
                    switch (i) {
                        case 0: px = 0.207f; py = 0.207f; break;
                        case 1: px = 0.707f; py = 0.707f; weight = 1.6f; break;
                    }
                    if (randomness != 0) {
                        px += randomness * Noise.noise2(271*(cubeX+px), 271*(cubeY+py));
                        py += randomness * Noise.noise2(271*(cubeX+px)+89, 271*(cubeY+py)+137);
                    }
                    break;
                case TRIANGULAR:
                    if ((cubeY & 1) == 0) {
                        if (i == 0) {
                            px = 0.25f; py = 0.35f;
                        } else {
                            px = 0.75f; py = 0.65f;
                        }
                    } else {
                        if (i == 0) {
                            px = 0.75f; py = 0.35f;
                        } else {
                            px = 0.25f; py = 0.65f;
                        }
                    }
                    if (randomness != 0) {
                        px += randomness * Noise.noise2(271*(cubeX+px), 271*(cubeY+py));
                        py += randomness * Noise.noise2(271*(cubeX+px)+89, 271*(cubeY+py)+137);
                    }
                    break;
            }
            float dx = (float)Math.abs(x-px);
            float dy = (float)Math.abs(y-py);
            float d;
            dx *= weight;
            dy *= weight;
            if (distancePower == 1.0f)
                d = dx + dy;
            else if (distancePower == 2.0f)
                d = (float)Math.sqrt(dx*dx + dy*dy);
            else
                d = (float)Math.pow((float)Math.pow(dx, distancePower) + (float)Math.pow(dy, distancePower), 1/distancePower);

            // Insertion sort the long way round to speed it up a bit
            if (d < results[0].distance) {
                Point p = results[2];
                results[2] = results[1];
                results[1] = results[0];
                results[0] = p;
                p.distance = d;
                p.dx = dx;
                p.dy = dy;
                p.x = cubeX+px;
                p.y = cubeY+py;
            } else if (d < results[1].distance) {
                Point p = results[2];
                results[2] = results[1];
                results[1] = p;
                p.distance = d;
                p.dx = dx;
                p.dy = dy;
                p.x = cubeX+px;
                p.y = cubeY+py;
            } else if (d < results[2].distance) {
                Point p = results[2];
                p.distance = d;
                p.dx = dx;
                p.dy = dy;
                p.x = cubeX+px;
                p.y = cubeY+py;
            }
        }
        return results[2].distance;
    }

    public void evaluate(float x, float y) {
        for (int j = 0; j < results.length; j++)
            results[j].distance = Float.POSITIVE_INFINITY;

        int ix = (int)x;
        int iy = (int)y;
        float fx = x-ix;
        float fy = y-iy;

        float d = checkCube(fx, fy, ix, iy, results);
        if (d > fy)
            d = checkCube(fx, fy+1, ix, iy-1, results);
        if (d > 1-fy)
            d = checkCube(fx, fy-1, ix, iy+1, results);
        if (d > fx) {
            checkCube(fx+1, fy, ix-1, iy, results);
            if (d > fy)
                d = checkCube(fx+1, fy+1, ix-1, iy-1, results);
            if (d > 1-fy)
                d = checkCube(fx+1, fy-1, ix-1, iy+1, results);
        }
        if (d > 1-fx) {
            d = checkCube(fx-1, fy, ix+1, iy, results);
            if (d > fy)
                d = checkCube(fx-1, fy+1, ix+1, iy-1, results);
            if (d > 1-fy)
                d = checkCube(fx-1, fy-1, ix+1, iy+1, results);
        }


    }


    public int getPixel(int x, int y, int[] inPixels, int width, int height,int index,boolean check) {
        float nx = m00*x + m01*y;
        float ny = m10*x + m11*y;
        nx /= scale;
        ny /= scale * stretch;
        nx += 1000;
        ny += 1000;	// Reduce artifacts around 0,0
        float f;
        evaluate(nx, ny);
        float f1 = results[0].distance;
        float f2 = results[1].distance;
        int v= inPixels[index];
        f = (f2 - f1) / edgeThickness;
        f = ImageMath.smoothStep(0, edgeThickness, f);
//        if(f<edgeThickness){
//            if( check&&f < edgeThickness/2){
//                v= (255<<24)|(0 << 16) | (0 << 8) | embeddedList.remove(0)&0xff;
//
//            }
//            else {
//                v= (255<<24)|(0 << 16) | (0 << 8) | 0;
//            }
//        }

        if(check&&f<edgeThickness) {
            v = ImageMath.mixColors(f, 0xff000000, v, embeddedList.remove(0));
        }
        else if(f<edgeThickness) {
            v = ImageMath.mixColors(f, 0xff000000, v);
        }


        return v;
    }
    public int[] filter( int[] src, int w, int h ) {
        int width = w;
        int height =h;

        int[] inPixels = src;
        inPixels = filterPixels( width, height, inPixels );

        return inPixels;
    }
    public String toString() {
        return "Pixellate/Mosaic...";
    }

    protected int[] filterPixels(int width, int height, int[] inPixels) {
        int index = 0;
        int[] outPixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if(x > (width/4) && x < (width/4*3) && y>(height/4) && y< (height*3/4) &&embeddedList.size()>0){
                    outPixels[index] = getPixel(x, y, inPixels, width, height,index,true);
                }
                else {
                    outPixels[index] = getPixel(x, y, inPixels, width, height,index,false);
                }
                index++;
            }
        }
        if(embeddedList.size()==0){
            System.out.println("All data has been embedded");
        }
        return outPixels;
    }
}
