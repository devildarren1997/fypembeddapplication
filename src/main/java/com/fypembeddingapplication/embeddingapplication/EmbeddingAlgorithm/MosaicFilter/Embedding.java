package com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.MosaicFilter;
import java.awt.image.*;
import java.io.ByteArrayInputStream;
import com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.MosaicFilter.filterInput;

import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Random;
import com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.MosaicFilter.MosaicFilter;
public class Embedding {
    private String embeddedInformation;
    private String URLImageBase64;
    private String embeddedKey;
    private String OutEmbeddedImageBase64;
    private String OutNonEmbeddedImageBase64;
    private ArrayList<String> errorMessage =new ArrayList<>();
    private ArrayList<String > exceptionMessage =new ArrayList<>();

    public void setErrorMessage(ArrayList<String> errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ArrayList<String> getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(ArrayList<String> exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public ArrayList<String> getErrorMessage() {
        return errorMessage;
    }

    public Embedding(String embeddedInformation, String URLImageBase64) {
        this.embeddedInformation = embeddedInformation;
        this.URLImageBase64 =URLImageBase64;
    }
    public String getEmbededImage(){
        StringBuilder stringBuilder =new StringBuilder();
        ArrayList<Integer> embeddedKey=generateEmbeddedKey(embeddedInformation);
        if (embeddedKey.isEmpty()){
            errorMessage.add("Error code 103. Fail to apply Fragment Filter watermark to your image");
        }
        if (embeddedKey.size()>128){
            errorMessage.add("Error code 104. Your watermark information is too long");
        }
        for(int i =0; i<embeddedKey.size();i++){
            if(i<embeddedKey.size()-1){
                stringBuilder.append(embeddedKey.get(i));
                stringBuilder.append(",");
            }
            else {
                stringBuilder.append(embeddedKey.get(i));
            }
        }
        this.setEmbeddedKey(stringBuilder.toString());
        String URLImageBase64 =getURLImageBase64();
        filterInput input = converBase64ToPixels(URLImageBase64);
        MosaicFilter mosaicFilter=new MosaicFilter(getEmbeddedList(embeddedInformation,embeddedKey));
        int pixelsMosaicFilter[]= mosaicFilter.filter(input.getInPixles(),input.getWidth(),input.getHeight());
        String outEmbeddedImageBase64=null;
        if(pixelsMosaicFilter.length==0){
            errorMessage.add("Error code 103. Fail to apply Fragment Filter watermark to your image");
        }
        else {
            outEmbeddedImageBase64=converPixelsToBase64(pixelsMosaicFilter,input.getHeight(),input.getWidth());
            this.setOutEmbeddedImageBase64(outEmbeddedImageBase64);
            if(outEmbeddedImageBase64==null){
                errorMessage.add("Error code 103. Fail to apply Fragment Filter watermark to your image");
            }
        }
        System.out.println("done");
        return outEmbeddedImageBase64;
    }

    public String getURLImageBase64() {
        return URLImageBase64;
    }

    public String getEmbeddedInformation() {
        return embeddedInformation;
    }

    public void setEmbeddedInformation(String embeddedInformation) {
        this.embeddedInformation = embeddedInformation;
    }

    public void setEmbeddedKey(String embeddedKey) {
        this.embeddedKey = embeddedKey;
    }
    public String getEmbeddedKey(){
        return this.embeddedKey;
    }

    public String getOutEmbeddedImageBase64() {
        return OutEmbeddedImageBase64;
    }

    public void setOutEmbeddedImageBase64(String outEmbeddedImageBase64) {
        this.OutEmbeddedImageBase64 = outEmbeddedImageBase64;
    }

    public void setURLImageBase64(String URLImageBase64) {
        this.URLImageBase64 = URLImageBase64;
    }

    public filterInput converBase64ToPixels (String URLImageBase64){
//        String imageBase64 =URLImageBase64.substring(URLImageBase64.lastIndexOf(",")+1);

        byte[] decodedBytes= Base64.getDecoder().decode(URLImageBase64);
        Image image=null;
        try{
            ByteArrayInputStream inputStream= new ByteArrayInputStream(decodedBytes);
            image = ImageIO.read(inputStream);
            inputStream.close();
        }
        catch (IOException e){
//            throw new IllegalStateException(e.getMessage());
            exceptionMessage.add(e.getMessage());
        }
        filterInput filterInput =null;
        if(image!=null){
            BufferedImage ARGBimage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
            Graphics2D bGr = ARGBimage.createGraphics();
            bGr.drawImage(image, 0, 0, null);
            bGr.dispose();
            filterInput=new filterInput(convertToPixels(ARGBimage),ARGBimage.getHeight(),ARGBimage.getWidth());
        }
       return filterInput;

    }
    public String converPixelsToBase64(int[] outputPixles, int height, int width){
        MemoryImageSource ims = new MemoryImageSource(width,height, outputPixles, 0, width);
        Image image = Toolkit.getDefaultToolkit().createImage(ims);
        BufferedImage bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(image, 0, 0, null);
        bGr.dispose();

        String imageString = null;

        File outputMosaicFilter=new File("src/main/java/com/fypembeddingapplication/embeddingapplication/EmbeddingAlgorithm/input7MosicOut.png");
        try{
            ImageIO.write(bimage,"png",outputMosaicFilter);
        }
        catch (IOException e){exceptionMessage.add(e.getMessage());}
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bimage, "png", bos);
            byte[] imageBytes = bos.toByteArray();
            imageString = new String (Base64.getEncoder().encode(imageBytes) ,"UTF-8") ;
            bos.close();
        } catch (IOException e) {
            exceptionMessage.add(e.getMessage());
        }
        return imageString;
    }

    public int[] convertToPixels(Image img) {
        int width = img.getWidth(null);
        int height = img.getHeight(null);
        int[] pixels = new int[width * height];
        PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, pixels, 0, width);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            exceptionMessage.add(e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
        if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
            throw new IllegalStateException("Error: Interrupted Waiting for Pixels");
        }
        return pixels;
    }
    public static ArrayList <Integer> generateEmbeddedKey(String embededInformation){
        Random random=new Random();
        char [] stringArray=embededInformation.toCharArray();

        ArrayList <Integer> embeddedKey=new ArrayList<>();

        while (embeddedKey.size()<stringArray.length){
            int key=random.nextInt(stringArray.length)+1;
            if(!embeddedKey.contains(255-key)){
                embeddedKey.add(255-key);
            }
        }

        return embeddedKey;
    }
    public static ArrayList<Integer> getEmbeddedList(String embeddedInformation, ArrayList <Integer> embeddedKey){
        ArrayList <Integer>  embeddedList=new ArrayList<>();
        char [] stringArray=embeddedInformation.toCharArray();

        for(int i=0; i<stringArray.length;i++){
            for(int x=1;x<=(int)stringArray[i];x++){
                embeddedList.add((embeddedKey.get(i)));
            }
        }
        Collections.shuffle(embeddedList);
        return embeddedList;
    }

}
