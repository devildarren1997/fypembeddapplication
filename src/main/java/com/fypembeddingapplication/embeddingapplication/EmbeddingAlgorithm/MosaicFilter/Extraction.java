package com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.MosaicFilter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
public class Extraction {
    private List<List<Integer>>  embeddedKey =new ArrayList<>();
    private String embeddedImageBase64;

    public Extraction(List<List<Integer>> embeddedKey, String embeddedImageBase64) {
        this.embeddedKey = embeddedKey;
        this.embeddedImageBase64 = embeddedImageBase64;
    }

    public List<List<Integer>> getEmbeddedKey() {
        return embeddedKey;
    }

    public void setEmbeddedKey(List<List<Integer>> embeddedKey) {
        this.embeddedKey = embeddedKey;
    }

    public String getEmbeddedImageBase64() {
        return embeddedImageBase64;
    }

    public void setEmbeddedImageBase64(String embeddedImageBase64) {
        this.embeddedImageBase64 = embeddedImageBase64;
    }

    public ArrayList <String> extract () {
        byte[] decodedBytes= Base64.getDecoder().decode(embeddedImageBase64);
        List<List<Integer>>  embeddedKeyList =embeddedKey;
        ArrayList <String> errorMessage =new ArrayList<>();
        Image image=null;
        try{
            ByteArrayInputStream inputStream= new ByteArrayInputStream(decodedBytes);
            image = ImageIO.read(inputStream);
            inputStream.close();
        }
        catch (IOException e){
            throw new IllegalStateException(e.getMessage());
        }
        int [] inPixels = null;
        if(image!=null){
            BufferedImage ARGBimage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D bGr = ARGBimage.createGraphics();
            bGr.drawImage(image, 0, 0, null);
            bGr.dispose();
            inPixels = convertToPixels(ARGBimage);
            HashMap<Integer,Integer> counter = new HashMap<>();
            for (int i = 0; i<inPixels.length; i ++){
                if(checkEmbeddedKey(inPixels[i])){
                    int temp =embeddedKeyList.size();
                    for (int j =0 ; j<temp;j++){
                        if (embeddedKeyList.get(j).contains(getEmbeddedKey(inPixels[i]))==false){
                            embeddedKeyList.remove(j);
                            temp = temp -1;
                            j--;
                        }
                    }
                    if(counter.containsKey(getEmbeddedKey(inPixels[i]))){
                        counter.put(getEmbeddedKey(inPixels[i]) ,counter.get(getEmbeddedKey(inPixels[i]))+1);
                    }
                    else {
                        counter.put(getEmbeddedKey(inPixels[i]) ,1);
                    }
                }
            }
            if (embeddedKeyList.size()>0){
                ArrayList <String> possibleOutPut =new ArrayList<>();
                for(int x = 0; x< embeddedKeyList.size();x++){
                    StringBuilder embeddedData =new StringBuilder();
                    for(int i=0;i<embeddedKeyList.get(x).size();i++){
                        if(counter.get(embeddedKeyList.get(x).get(i))!=null){
                            embeddedData.append((char)(int)counter.get(embeddedKeyList.get(0).get(i)));
                        }
                        else {
                            errorMessage.add("Error code 201");
                            return errorMessage;
                        }
                    }

                    possibleOutPut.add(embeddedData.toString());
                }
                return possibleOutPut;
            }else {
                errorMessage.add("Error code 202");
                return errorMessage;
            }
        }
        else {
            errorMessage.add("Error code 203");
            return errorMessage;
        }
    }
    public static int[] convertToPixels(Image img) {
        int width = img.getWidth(null);
        int height = img.getHeight(null);
        int[] pixels = new int[width * height];
        PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, pixels, 0, width);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e.getMessage());
        }
        if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
            throw new IllegalStateException("Error: Interrupted Waiting for Pixels");
        }
        return pixels;
    }

    public static boolean checkEmbeddedKey (int pixel){

        if((pixel>>24&0xff)==255){
            return false;
        }
        else {
            return true;
        }
    }
    public static int getEmbeddedKey (int pixel){
        return pixel>>24&0xff;
    }
}
