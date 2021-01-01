package com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.MosaicFilter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

public class ImageCompress {
    private ArrayList<String> errorMessage =new ArrayList<>();
    private ArrayList<String > exceptionMessage =new ArrayList<>();

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

    public ImageCompress() {
    }
    public String compress(String originalBase64){
        int tThumbWidth = 0;
        int tThumbHeight =0;
        byte[] decodedBytes= Base64.getDecoder().decode(originalBase64);
        Image image=null;
        try{
            ByteArrayInputStream inputStream= new ByteArrayInputStream(decodedBytes);
            image = ImageIO.read(inputStream);
            inputStream.close();
        }
        catch (IOException e){
            exceptionMessage.add(e.getMessage());
        }
        tThumbHeight =((BufferedImage) image).getHeight();
        tThumbWidth= ((BufferedImage) image).getWidth();
        while (tThumbHeight>1000||tThumbWidth>1500){
            tThumbHeight = tThumbHeight/2;
            tThumbWidth = tThumbWidth/2;
        }
        BufferedImage tThumbImage = new BufferedImage( tThumbWidth, tThumbHeight, BufferedImage.TYPE_INT_RGB );
        Graphics2D tGraphics2D = tThumbImage.createGraphics(); //create a graphics object to paint to
        tGraphics2D.setBackground( Color.WHITE );
        tGraphics2D.setPaint( Color.WHITE );
        tGraphics2D.fillRect( 0, 0, tThumbWidth, tThumbHeight );
        tGraphics2D.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
        tGraphics2D.drawImage( image, 0, 0, tThumbWidth, tThumbHeight, null ); //draw the image scaled
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String imageString = null;
        try {
            ImageIO.write(tThumbImage, "JPG", bos);
            byte[] imageBytes = bos.toByteArray();
            imageString = new String (Base64.getEncoder().encode(imageBytes) ,"UTF-8") ;
            bos.close();
        } catch (IOException e) {
            exceptionMessage.add(e.getMessage());
        }
        return imageString;
    }
}
