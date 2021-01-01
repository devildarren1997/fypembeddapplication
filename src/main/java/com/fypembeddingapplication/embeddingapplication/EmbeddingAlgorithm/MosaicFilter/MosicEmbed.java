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
import java.util.List;
import java.util.Random;
import com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.MosaicFilter.MosaicFilter;

public class MosicEmbed {
    private String embeddedInformation;
    private String URLImageBase64;
    private String embeddedImageBase64;
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

    public MosicEmbed(String embeddedInformation, String URLImageBase64) {
        this.embeddedInformation = embeddedInformation;
        this.URLImageBase64 =URLImageBase64;
    }
    public MosicEmbed(String embeddedImageBase64) {
        this.embeddedImageBase64 = embeddedImageBase64;
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

    public String getOutEmbeddedImageBase64() {
        return OutEmbeddedImageBase64;
    }

    public void setOutEmbeddedImageBase64(String outEmbeddedImageBase64) {
        this.OutEmbeddedImageBase64 = outEmbeddedImageBase64;
    }
    public void setURLImageBase64(String URLImageBase64) {
        this.URLImageBase64 = URLImageBase64;
    }



    public static int[] covertToPixelsJpg(BufferedImage image){
        int index=0;
        int w = image.getWidth();
        int h = image.getHeight();
        int[] Pixels = new int[w * h];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int pixel = image.getRGB(j, i);
                int r=pixel>>16&0xff;
                int g=pixel>>8&0xff;
                int b=pixel&0xff;
                Pixels[index]= r<<16|g<<8|b;
                index++;
            }
        }
        return Pixels;
    }
    public static int[] convertToPixels(Image img) {
        int width = img.getWidth(null);
        int height = img.getHeight(null);
        int[] pixel = new int[width * height];

        PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, pixel, 0, width);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            throw new IllegalStateException("Error: Interrupted Waiting for Pixels");
        }
        if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
            throw new IllegalStateException("Error: Image Fetch Aborted");
        }
        return pixel;
    }

    public static java.util.List<Integer> convertByteArraysToBinary(byte[] input) {
        List<Integer> list= new ArrayList();
        for (byte b : input) {
            int val = b;
            for (int i = 0; i < 8; i++) {
                list.add((val & 128) == 0 ? 0 : 1);      // 128 = 1000 0000
                val <<= 1;
            }
        }
        return list;
    }

    public static String convertBinaryToStr(int[] array){
        StringBuilder result= new StringBuilder();
        for (int i =0 ; i<array.length;i=i+8){
            StringBuilder binaryChar = new StringBuilder();

            for (int x=0;x<8;x++){
                binaryChar.append(array[i+x]);
            }
            int ascii=Integer.parseInt(binaryChar.toString(),2);
            result.append((char)ascii);


        }
        return result.toString();
    }
    public String converPixelsToBase64(int[] outputPixles, int height, int width){
        MemoryImageSource ims = new MemoryImageSource(width,height, outputPixles, 0, width);
        Image image = Toolkit.getDefaultToolkit().createImage(ims);
        BufferedImage bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(image, 0, 0, null);
        bGr.dispose();

        File outputPencilFilter=new File("src/main/java/com/fypembeddingapplication/embeddingapplication/EmbeddingAlgorithm/input7MosicOut.png");
        try{
            ImageIO.write(bimage,"png",outputPencilFilter);
        }
        catch (IOException e){exceptionMessage.add(e.getMessage());}

        String imageString = null;
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
    public BufferedImage converBase64ToImage (String URLImageBase64){

        byte[] decodedBytes= Base64.getDecoder().decode(URLImageBase64);
        Image image=null;
        try{
            ByteArrayInputStream inputStream= new ByteArrayInputStream(decodedBytes);
            image = ImageIO.read(inputStream);
            inputStream.close();
        }
        catch (IOException e){
            exceptionMessage.add(e.getMessage());
        }
        if(image!=null){
            BufferedImage ARGBimage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D bGr = ARGBimage.createGraphics();
            bGr.drawImage(image, 0, 0, null);
            bGr.dispose();
            return ARGBimage;
        }
        return null;

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
    public String embedding (){
        String URLImageBase64 =getURLImageBase64();
        filterInput input = converBase64ToPixels(URLImageBase64);
        MosaicFilter mosaicFilter=new MosaicFilter(getEmbeddedList(embeddedInformation));
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
    public String extraction (){
        BufferedImage image = converBase64ToImage(embeddedImageBase64);
        int[] pixels = convertToPixels(image);
        List<Integer> list= new ArrayList<>();
        String result = null;
        for (int item :pixels){
            if (checkEmbeddedKey(item)){
                list.add(item>>24&0xff);
            }
        }
        List<Integer> list1= new ArrayList<>();
        List<Integer> list2= new ArrayList<>();
        List<Integer> list3= new ArrayList<>();
        int zero = 0;
        int one =0;
        int separation = 0;
        boolean done = false;
        for(int i =0;i<list.size();i++){
            if (list.get(i)>zero&!done){
                one =zero;
                zero =list.get(i);
            }
            if (list.get(i)!=zero&&!done){
                one=list.get(i);
            }
            if (zero>one &&zero!=0&&one!=0){
                done=true;
            }
            if (list.get(i)==254){
                if (separation==0){
                    list1.addAll(list.subList(separation,i));
                }
                else if (i==list.size()-1){
                    list3.addAll(list.subList(separation,i));
                }else {
                    list2.addAll(list.subList(separation,i));
                }
                separation = i+1;
            }
        }

        if (list1.size()!=list2.size()&&list1.size()!=list3.size()){
            errorMessage.add("Error code 301. Fail to extract information");
        }
        else {
            int[] binaryResult = new int[list1.size()];
            for (int i = 0;i<list1.size();i++){
                if (recover(list1.get(i),list2.get(i),list3.get(i))==zero){
                    binaryResult[i]=0;
                }else {
                    binaryResult[i]=1;
                }
            }
            result =convertBinaryToStr(binaryResult);
        }
        return result;
    }
    public static int recover (int number1,int number2,int number3){
        if (number1==number2){
            return number1;
        }else {
            return number3;
        }
    }
    public static boolean checkEmbeddedKey (int pixel){

        if((pixel>>24&0xff)==255){
            return false;
        }
        else {
            return true;
        }
    }
    public static ArrayList<Integer> getEmbeddedList(String embeddedInformation){
        List<Integer> binaryCode = convertByteArraysToBinary(embeddedInformation.getBytes());
        ArrayList<Integer> embeddedList = new ArrayList<>();
        Random random = new Random();
        int zero = 0; //higher alpha value is zero
        int one =0; // lower alpha value is one

        while (zero==0&&one==0){
            int random1=random.nextInt(25)+2;
            int random2= random.nextInt(25)+2;
            if (random1>random2){
                zero =255-random2;
                one = 255-random1;
            }
            else if (random2>random1){
                zero = 255-random1;
                one = 255-random2;
            }
        }

        for (int i =0;i<binaryCode.size();i++){
            if (binaryCode.get(i)==0){
                binaryCode.set(i,zero);
            }else {
                binaryCode.set(i,one);
            }
        }
        embeddedList.addAll(binaryCode);
        embeddedList.add(254); // fix alpha value 254 as the separation
        embeddedList.addAll(binaryCode);
        embeddedList.add(254);
        embeddedList.addAll(binaryCode);
        embeddedList.add(254);
        return embeddedList;
    }
}
