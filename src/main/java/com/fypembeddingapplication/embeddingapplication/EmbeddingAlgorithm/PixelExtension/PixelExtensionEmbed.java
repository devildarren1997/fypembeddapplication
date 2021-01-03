package com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.PixelExtension;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

public class PixelExtensionEmbed {
	
	private static int width = 0;
    private static int height = 0;
    private static int[][] newValues = null;
    private static int minBright = 100;
    private static int length = 4;
    private static String binaryToBeEmbed;
    private String encryptedInformation;
    private String ImageInputBase64;
    private String embeddedImageBase64;
    private ArrayList<String> errorMessage =new ArrayList<>();
    private ArrayList<String> exceptionMessage =new ArrayList<>();
    
    
    public PixelExtensionEmbed(String encryptedInformation, String ImageInputBase64){
    	this.encryptedInformation = encryptedInformation;
    	this.ImageInputBase64 = ImageInputBase64;
    }
    
    public PixelExtensionEmbed(String embeddedImageBase64){
    	this.embeddedImageBase64 = embeddedImageBase64;
    }
    
    public void setErrorMessage(ArrayList<String> errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setExceptionMessage(ArrayList<String> exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
    
    public ArrayList<String> getExceptionMessage() {
        return exceptionMessage;
    }

    public ArrayList<String> getErrorMessage() {
        return errorMessage;
    }
    
    public String PixelExtension() {
    	BufferedImage loadImage = null;
    	loadImage  = convertBase64ToImage(ImageInputBase64);
    	width = loadImage.getWidth();
        height = loadImage.getHeight();
        newValues = new int[width][height];
        binaryToBeEmbed = convertStringToBinary(encryptedInformation);
        
//       try 
//       {
//    	   ImageIO.write(loadImage, "jpg", new File("C:/Users/Darren/OneDrive/Pictures/Screenshots/original.jpg"));
//       }catch(IOException e) {
//    	   exceptionMessage.add(e.getMessage());
//       }
        
        if(loadImage.equals(null)) {
        	errorMessage.add("Error 100. Fail to get Image input in PixelExtension");
        }
        
        distort(loadImage, binaryToBeEmbed);
        
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                loadImage.setRGB(i, j, newValues[i][j]);
            }
        }
        
        String imageString = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try {
        	ImageIO.write(loadImage, "png", new File("src/main/java/com/fypembeddingapplication/embeddingapplication/EmbeddingAlgorithm/ImageSampleGlitch.png"));
            ImageIO.write(loadImage, "png", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            imageString = new String(Base64.getEncoder().encode(imageBytes),"UTF-8");
            outputStream.close();
        }
        catch(IOException e){
        	exceptionMessage.add(e.getMessage());
        }
        return imageString;
    }
    
    public String PixelExtensionExtraction() {
    	BufferedImage loadImage = null;
    	loadImage = convertBase64ToImage(embeddedImageBase64);
    	try {
    		ImageIO.write(loadImage, "png", new File("src/main/java/com/fypembeddingapplication/embeddingapplication/EmbeddingAlgorithm/ImageSampleGlitchExtract.png"));
    	}
    	catch(IOException e){
    		System.out.println(e);
    	}
    	
    	width = loadImage.getWidth();
        height = loadImage.getHeight();
        String binaryString = "";
        
        for (int i = 0; i < width; i++) {

            int counter = 0;
            int redCompare = 0, greenCompare = 0, blueCompare = 0;

            for (int j = 0; j < height; j++) {

                Color colour = new Color(loadImage.getRGB(i, j));
                int red = colour.getRed();
                int green = colour.getGreen();
                int blue = colour.getBlue();

                if (j != 0) {

                    if (counter == 0) {
                        redCompare = red;
                        greenCompare = green;
                        blueCompare = blue;
                        counter++;
                    } else {
                        int preAV = Math.round(((redCompare + greenCompare + blueCompare) / 3));
//                      System.out.println(red + " " + redCompare + " " + green + " " + greenCompare + " " + blue + " " + blueCompare + " " + preAV);
                      boolean check = redCompare == red && greenCompare == green && blueCompare == blue;
//                      System.out.println(i + " " + j + " " + check + " " + counter + " " + (preAV >= minBright && check));
                      if (preAV >= minBright && check) {
//                      	System.out.println("We found a bit in: i " +i + " and j " + j);
                          counter++;
                      } else {
                          if (counter == 101) {
                              binaryString = binaryString + '0';
                              break;
                          } else if (counter == 130) {
                              binaryString = binaryString + '1';
                              break;
                          }
                          redCompare = red;
                          greenCompare = green;
                          blueCompare = blue;
                          counter = 1;
                      }
                  }
                }// j != 0
            }// j loop
        }// i loop
        System.out.println("This is the binaryString: " + binaryString);
        if(binaryString.length() < 10 || binaryString == null) {
        	errorMessage.add("Error 101. Fail to get the binaryString embedded into the image.");
        	System.out.println("we return null");
        	return "0";
        }
        else if(binaryString.length() == 192 || binaryString.length() == 352) {
        	String convertedBinary = convertBinaryToString(binaryString);
            return convertedBinary;
        }
        else {
        	errorMessage.add("Error 102. Fail to get complete binaryString embedded into the image.");
        	System.out.println("we return error");
        	return "Error102";
        }
        
    }
    
    
    public static void distort(BufferedImage image, String binaryToEmbed) {
//        int count = 0;
//        int max = 10;
        int counti = 0;
        String binaryNeedsToEmbed = binaryToEmbed;

        for (int i = 0; i < width; i++) {

            int maxV = 0;
            Boolean embedded = false;
            int redEmbed = 0, greenEmbed = 0, blueEmbed = 0;
            int previousRedEmbed = -1, previousGreenEmbed = -1, previousBlueEmbed = -1;
            boolean justEmbed = false;

            for (int j = 0; j < height; j++) {
                Color colour = new Color(image.getRGB(i, j));
                int red = colour.getRed();
                int green = colour.getGreen();
                int blue = colour.getBlue();
                if(previousRedEmbed == redEmbed && 
                            previousGreenEmbed == greenEmbed &&
                            previousBlueEmbed == blueEmbed){
                    red -= 3;
                    green -= 3;
                    blue -= 3;
                    previousRedEmbed = -1;
                    previousGreenEmbed = -1;
                    previousBlueEmbed = -1;
                }

                if (j != 0) {
                    int preAV = Math.round(((red + green + blue) / 3));

                    if (preAV >= minBright && embedded == false && binaryNeedsToEmbed.length() > 0) {

//                        System.out.println("We fullfill the length and firstextension true at the i : " + i + "and j " + j);

                        if (j < (height * 0.9)) {
                            counti++;
//                            System.out.println("We embed a binary");
                            String binaryEmbed = binaryNeedsToEmbed;
                            char binarybit = binaryEmbed.charAt(0);

                            if (binarybit == '0') {
                                maxV = 101;
//                                System.out.println("The length embed is " + maxV);
                            } else if (binarybit == '1') {
                                maxV = 130;
//                                System.out.println("The length embed is " + maxV);
                            }
                            redEmbed = red;
                            blueEmbed = blue;
                            greenEmbed = green;
                            embedded = true;
                            justEmbed = true;
                        }// if j fulfill height condition
                    }
                    else if(preAV >= minBright && maxV == 0){

                        if (j < (height * 0.9) && !justEmbed) {
                            Random rand = new Random();
                            do{
                                maxV = rand.nextInt(50) + 1;
                            }while(maxV >= 90 && maxV <= 110 || maxV >= 120 && maxV <= 140);
//                            System.out.println("maxV: " + maxV);
                            redEmbed = red;
                            blueEmbed = blue;
                            greenEmbed = green;
                        }// if j fulfill height condition
                        else{
                            justEmbed = false;
                        }
                    }

                    if (maxV > 0) {

                        int col = (redEmbed << 16 | greenEmbed << 8 | blueEmbed);
                        newValues[i][j] = col;
//                        System.out.println(i + " " + j + " " + redEmbed + " " + greenEmbed + " " + blueEmbed);
                        maxV--;
                        if(maxV == 0){
                            previousRedEmbed = redEmbed;
                            previousGreenEmbed = greenEmbed;
                            previousBlueEmbed = blueEmbed;
                            justEmbed = true;
                        }
                    }
                    else{
                        int col = (red << 16 | green << 8 | blue);
                        newValues[i][j] = col;
                    }
                }// if j != 0

            } // j loop
            if (binaryNeedsToEmbed.length() != 0) {
                binaryNeedsToEmbed = binaryNeedsToEmbed.substring(1);
//                System.out.println("The binary left" + binaryNeedsToEmbed.length());
            }
        } // i loop
//        System.out.println("count i: " + counti);
    }// distortImage
    
//    public static void distort(BufferedImage image, String binaryToEmbed) {
//
//        int max = 10;
//
//        Random random = new Random();
//        String binaryNeedsToEmbed = binaryToEmbed;
//
//        for (int i = 0; i < width; i++) {
//
//
//            int maxV = 0;
//            Boolean embedded = false;
//            int redEmbed = 0, greenEmbed = 0, blueEmbed = 0;
//
//            for (int j = 0; j < height; j++) {
//                Color colour = new Color(image.getRGB(i, j));
//                int red = colour.getRed();
//                int green = colour.getGreen();
//                int blue = colour.getBlue();
//
//                if (j != 0) {
//                    int preAV = Math.round(((red + green + blue) / 3));
//
//                    if (preAV >= minBright && embedded == false && binaryNeedsToEmbed.length() > 0) {
//
//                        System.out.println("We fullfill the length and firstextension true at the i : " + i + "and j " + j);
//
//                        if (j < (height * 0.8)) {
//                            System.out.println("We embed a binary");
//                            String binaryEmbed = binaryNeedsToEmbed;
//                            char binarybit = binaryEmbed.charAt(0);
//
//                            if (binarybit == '0') {
//                                maxV = 11;
//                                System.out.println("The length embed is " + maxV);
//                            } else if (binarybit == '1') {
//                                maxV = 30;
//                                System.out.println("The length embed is " + maxV);
//                            }
//                            redEmbed = red;
//                            blueEmbed = blue;
//                            greenEmbed = green;
//                            embedded = true;
//                        }// if j fulfill height condition
//                    }
////                    else if(preAV >= minBright && maxV == 0){
////
////                        if (j < (height * 0.8)) {
////                        	max = preAV - minBright;
////                        	maxV = (int)Math.round(max*length);
////                            
////                        	if(maxV >= 70 &&  maxV <= 130) {
////                        		do{
////                                    maxV = random.nextInt(50) + 1;
////                                }while(maxV >= 80 && maxV <= 130);
////                        	}
////                            
////                            redEmbed = red;
////                            blueEmbed = blue;
////                            greenEmbed = green;
////                        }// if j fulfill height condition
////                    }
//                    
//
//                    if (maxV > 0) {
//
//                        int col = (redEmbed << 16 | greenEmbed << 8 | blueEmbed);
//                        newValues[i][j] = col;
////                        System.out.println(i + " " + j + " " + redEmbed + " " + greenEmbed + " " + blueEmbed);
//                        maxV--;
//                    }
//                    else{
//                        int col = (red << 16 | green << 8 | blue);
//                        newValues[i][j] = col;
//                    }
//                }// if j != 0
//
//            } // j loop
//            if (binaryNeedsToEmbed.length() != 0 && embedded == true) {
//                binaryNeedsToEmbed = binaryNeedsToEmbed.substring(1);
//                System.out.println("The binary left" + binaryNeedsToEmbed.length());
//            }
//        } // i loop
//    }
    
    private BufferedImage convertBase64ToImage(String inputImageBase64) {
    	byte[] decodedBytes = Base64.getDecoder().decode(inputImageBase64);
    	Image image = null;
    	try {
    		ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedBytes);
    		image = ImageIO.read(inputStream);
    		inputStream.close();
    	}catch(IOException e) {
    		exceptionMessage.add(e.getMessage());
    	}
    	if(image != null) {
    		BufferedImage ARGBimage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
    		Graphics2D bGr = ARGBimage.createGraphics();
            bGr.drawImage(image, 0, 0, null);
            bGr.dispose();
            return ARGBimage;
    	}
    	return null;
    }
	
	
	private String convertStringToBinary(String encodedBase64) {
		StringBuilder binaryBase64 = new StringBuilder();
		char[] chars = encodedBase64.toCharArray();
        for(char aChar : chars){
            binaryBase64.append(
                    String.format("%8s", Integer.toBinaryString(aChar)).replaceAll(" ", "0")
            ); 
        }
        String binary = binaryBase64.toString(); //01101110011011000100010101101010011110010011100000101011
        
        return binary;
	}
	
	private String convertBinaryToString(String stringToDecrypt) {
		List<String> binaryBase64co = new ArrayList<>();
        int index = 0;
        while(index < stringToDecrypt.length()){
            binaryBase64co.add(stringToDecrypt.substring(index, Math.min(index+8, stringToDecrypt.length())));
            index += 8;
        }
        String correctBinary = binaryBase64co.stream().collect(Collectors.joining(" ")); 
		String stringBase64 = Arrays.stream(correctBinary.split(" "))
                .map(binary -> Integer.parseInt(binary, 2)).map(Character::toString)
                .collect(Collectors.joining());
		return stringBase64;
	}
	
	
}
