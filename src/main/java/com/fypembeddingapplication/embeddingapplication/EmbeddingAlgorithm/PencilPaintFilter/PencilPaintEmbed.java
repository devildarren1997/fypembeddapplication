package com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.PencilPaintFilter;

import com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.MosaicFilter.filterInput;
import com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.PencilPaintFilter.ColorDogeComposite.ColorDodgeComposite;
import com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.PencilPaintFilter.GaussianFilter.GaussianFilter;
import com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.PencilPaintFilter.GrayscaleFilter.GrayscaleFilter;
import com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.PencilPaintFilter.ImageUtils.ImageUtils;
import com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.PencilPaintFilter.InvertFilter.InvertFilter;
import com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.PencilPaintFilter.PointFilter.PointFilter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class PencilPaintEmbed {
    private BufferedImage src;
    private String imageType;
    private ArrayList<String> errorMessage =new ArrayList<>();
    private ArrayList<String > exceptionMessage =new ArrayList<>();
    public PencilPaintEmbed(String base64,String imageType) {
        this.src = converBase64ToImage(base64);
        this.imageType = imageType;
    }

    public PencilPaintEmbed() {
    }

    public ArrayList<String> getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ArrayList<String> errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ArrayList<String> getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(ArrayList<String> exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public BufferedImage getSrc() {
        return src;
    }

    public void setSrc(BufferedImage src) {
        this.src = src;
    }

    public String embedded (String info){
        BufferedImage target = null;
        this.src = ImageUtils.convertImageToARGB(src);
        //gray scale
        PointFilter grayScaleFilter = new GrayscaleFilter();
        BufferedImage grayScale = new BufferedImage(src.getWidth(),src.getHeight(),src.getType());
        grayScaleFilter.filter(src, grayScale);
        BufferedImage inverted = new BufferedImage(src.getWidth(),src.getHeight(),src.getType());
        PointFilter invertFilter = new InvertFilter();
        invertFilter.filter(grayScale,inverted);
        GaussianFilter gaussianFilter = new GaussianFilter(20);
        BufferedImage gaussianFiltered = new BufferedImage(src.getWidth(),src.getHeight(),src.getType());
        gaussianFilter.filter(inverted, gaussianFiltered);
        ColorDodgeComposite cdc = new ColorDodgeComposite(1.0f);
        CompositeContext cc = cdc.createContext(inverted.getColorModel(), grayScale.getColorModel(), null);
        Raster invertedR = gaussianFiltered.getRaster();
        Raster grayScaleR = grayScale.getRaster();
        BufferedImage composite = new BufferedImage(src.getWidth(),src.getHeight(),src.getType());
        WritableRaster colorDodgedR = composite.getRaster();
        cc.compose(invertedR, grayScaleR , colorDodgedR);
        List<Integer> binaryCode = convertByteArraysToBinary(info.getBytes());
        target = composite;
        int [] pixels=null;
        if (imageType=="JPG"||imageType=="JPEG"){
            pixels= covertToPixelsJpg(target);
        }
        else {
            pixels = convertToPixels(target);
        }
        if (pixels==null){
            errorMessage.add("Error code 103. Fail to apply Pencil Paint watermark to your image");
        }
        for (int i=0;i<pixels.length;i=i+8){

            int currentReminder;
            int numberOfBlack=0;
            if ((i+8)<pixels.length){
                for(int x=0;x<8;x++){
                    if (getLuminance(pixels[i+x])==1){
                        numberOfBlack++;
                    }
                }
                currentReminder=numberOfBlack%3;
                if (binaryCode.size()!=0){
                    int requestedRemainder = binaryCode.remove(0);
                    int[] newArray = new int[8];
                    System.arraycopy(pixels,i,newArray,0,8);
                    int [] modifiedArray=modifyArray(newArray,numberOfBlack,currentReminder,requestedRemainder);
                    System.arraycopy(modifiedArray,0,pixels,i,8);
                }
                else {
                    int[] newArray = new int[8];
                    System.arraycopy(pixels,i,newArray,0,8);
                    int [] modifiedArray=modifyArray(newArray,numberOfBlack,currentReminder,2);
                    System.arraycopy(modifiedArray,0,pixels,i,8);
                }
            }else {
                if (binaryCode.size()>0){
                    errorMessage.add("Error code 104. Your watermark information is too long");
                }
            }

        }
        return converPixelsToBase64(pixels,target.getHeight(),target.getWidth());
    }
    public int[] modifyArray(int[] array, int numberOfBlack,int currentReminder, int requestedRemainder){
        if ((currentReminder==0&&requestedRemainder==1)||(currentReminder==1&&requestedRemainder==2)||(currentReminder==2&&requestedRemainder==0&&numberOfBlack<8)){
            float maxDark=0;
            int maxIndex=0;
            for (int i =0;i<array.length;i++){
                float luminance =getLuminance(array[i]);
                if (luminance>maxDark&&luminance!=1.0f){
                    maxDark = luminance;
                    maxIndex = i;
                }
            }
//
            array[maxIndex] = increaseLuminace(array[maxIndex],imageType);
            return array;
        }
        else if ((currentReminder==0&&requestedRemainder==2&&numberOfBlack!=0)||(currentReminder==1&&requestedRemainder==0)||(currentReminder==2&requestedRemainder==1)){
            int changeIndex =-1;
            int i=0;
            while (changeIndex==-1&&i<array.length){
                if (getLuminance(array[i])==1.0f){
                    changeIndex=i;
                }
                else {
                    i++;
                }
            }
            array[changeIndex] = decreaseLuminance(array[changeIndex],imageType);
            return array;
        }
        else if (currentReminder==0&&requestedRemainder==2&&numberOfBlack==0){
            float max1 =0;
            int max1Index=0;
            float max2 =0;
            int max2Index=0;
            for (int i=0; i<array.length;i++){
                float luminance =getLuminance(array[i]);
                if (luminance>max1&&luminance!=1.0f){
                    max1 = luminance;
                    max1Index = i;
                }
            }
            for (int i=0;i<array.length;i++){
                float luminance =getLuminance(array[i]);
                if (luminance>max2&&luminance!=1.0f&&i!=max1Index){
                    max2 = luminance;
                    max2Index = i;
                }
            }
            array[max1Index] = increaseLuminace(array[max1Index],imageType);
            array[max2Index] = increaseLuminace(array[max1Index],imageType);
            return array;
        }
        else if (currentReminder==2&&requestedRemainder==0&&numberOfBlack==8){
            int changeIndex1 =-1;
            int changeIndex2 =-1;
            int i=0;
            while ((changeIndex1==-1||changeIndex2==-1)&&i<array.length){
                if (getLuminance(array[i])==1.0f&&changeIndex1==-1){
                    changeIndex1=i;
                    i++;
                }
                else if (getLuminance(array[i])==1.0f&&changeIndex1!=-1){
                    changeIndex2=i;
                }
                else {
                    i++;
                }
            }
            array[changeIndex1] = decreaseLuminance(array[changeIndex1],imageType);
            array[changeIndex2] = decreaseLuminance(array[changeIndex2],imageType);
            return array;
        }
        else return array;

    }
    public String extract(String base64){
        BufferedImage image = converBase64ToImage(base64);
        int[] pixels = convertToPixels(image);
        List<Integer> list= new ArrayList<>();
        for (int i =0;i<pixels.length;i=i+8){
            if ((i+8)<pixels.length){
                int numberOfBlack=0;
                for (int x=0;x<8;x++){
                    if (getLuminance(pixels[i+x])==1.0f){
                        numberOfBlack++;
                    }
                }
                if (numberOfBlack%3!=2){
                    list.add(numberOfBlack%3);
                }
            }
        }
        int [] array  = list.stream()
                .mapToInt(Integer::intValue)
                .toArray();
        return convertBinaryToStr(array);

    }
    public int decreaseLuminance(int color, String imageType){
        if (imageType=="JPG"||imageType=="JPEG"){
            return 254<<16|254<<8|254;
        }
        else {
            return 255<<24|254<<16|254<<8|254;
        }
    }
    public int setSeparation(int color, String imageType){
        if (imageType=="JPG"||imageType=="JPEG"){
            return 253<<16|253<<8|253;
        }
        else {
            return 255<<24|253<<16|253<<8|253;
        }
    }
    public int increaseLuminace(int color, String imageType){
        if (imageType=="JPG"||imageType=="JPEG"){
            return 255<<16|255<<8|255;
        }
        else {
            return 255<<24|255<<16|255<<8|255;
        }
    }
    public float getLuminance(int color){

        int red   = (color >>> 16) & 0xFF;
        int green = (color >>>  8) & 0xFF;
        int blue  = (color >>>  0) & 0xFF;
//        System.out.println(red);
//        System.out.println(green);
//        System.out.println(blue);
// calc luminance in range 0.0 to 1.0; using SRGB luminance constants
        float luminance = (red * 0.2126f + green * 0.7152f + blue * 0.0722f) / 255;

// choose brightness threshold as appropriate:
        return luminance;
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

    public static List<Integer> convertByteArraysToBinary(byte[] input) {
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

        File outputPencilFilter=new File("src/main/java/com/fypembeddingapplication/embeddingapplication/EmbeddingAlgorithm/input7PencilOut.png");
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
}
