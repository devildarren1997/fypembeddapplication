package com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.CollagesEffect;

import java.awt.Color;
import java.awt.Graphics;
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
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

public class CollagesEffect {

	private String binaryToBeEmbed;
	private String encryptedInformation;
    private String ImageInputBase64;
    private String embeddedImageBase64;
    private ArrayList<String> errorMessage =new ArrayList<>();
    private ArrayList<String> exceptionMessage =new ArrayList<>();
	
	public CollagesEffect(String encryptedInformation, String ImageInputBase64) {
    	this.encryptedInformation = encryptedInformation;
    	this.ImageInputBase64 = ImageInputBase64;
	}
	
	public CollagesEffect(String embeddedImageBase64) {
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
	
	
	public String Collages() {
		
		BufferedImage loadImage  = convertBase64ToImage(ImageInputBase64);
	    binaryToBeEmbed = convertStringToBinary(encryptedInformation);
	    int rcount = 3;
	    int ccount = 5;
	   
	    
	    if(loadImage.equals(null)) {
        	errorMessage.add("Error 100. Fail to get Image input in PixelExtension" );
        }
	    
	    Image img = loadImage.getScaledInstance(loadImage.getWidth(), loadImage.getHeight(), BufferedImage.TYPE_INT_RGB);
	    BufferedImage[] imgs = getImages(img, rcount, ccount);
	    
	    for(int i=0; i < imgs.length; i++){
        	BufferedImage subOutImage = imgs[i];
        	
        	
        		for(int yPixel = 0; yPixel < subOutImage.getHeight();yPixel++) {
        			 Color col = new Color(255, 255, 0);
        				int lastXPixel = subOutImage.getWidth() - 1;
        				
        				subOutImage.setRGB(0, yPixel, col.getRGB());
        				subOutImage.setRGB(1, yPixel, col.getRGB());
        				subOutImage.setRGB(lastXPixel, yPixel, col.getRGB());
        				subOutImage.setRGB(lastXPixel-1, yPixel, col.getRGB());
        			
        		}
        		
        		for(int xPixel = 0; xPixel < subOutImage.getWidth(); xPixel++) {
        			Color col2 = new Color(255, 150, 50);
        			int lastYPixel = subOutImage.getHeight() - 1;
        			
    				subOutImage.setRGB(xPixel, 0, col2.getRGB());
    				subOutImage.setRGB(xPixel, 1, col2.getRGB());
    				subOutImage.setRGB(xPixel, lastYPixel, col2.getRGB());
    				subOutImage.setRGB(xPixel, lastYPixel-1, col2.getRGB());
        		}
        		
        		imgs[i] = subOutImage;
//            ImageIO.write(imgs[i], "png", new File("C:/Users/Darren/OneDrive/Pictures/Screenshots/img"+i+".png"));
        }
	    
	    BufferedImage finalImg = new BufferedImage(loadImage.getWidth(),loadImage.getHeight(), BufferedImage.TYPE_INT_RGB);
	    
	    int chunkWidth = imgs[0].getWidth();
        int chunkHeight = imgs[0].getHeight();
        int num = 0;
		
        for (int i = 0; i < rcount; i++) {  
            for (int j = 0; j < ccount; j++) {  
                finalImg.createGraphics().drawImage(imgs[num], chunkWidth * j, chunkHeight * i, null);  
                num++;  
            }  
        }
        
        String binaryString = binaryToBeEmbed;
    	for(int xPixel = 0; xPixel < finalImg.getWidth(); xPixel++) {
    		
    		if(binaryString.length() > 0) {
    		char binarybit = binaryString.charAt(0);
//			System.out.println(binarybit);
			
			if(binarybit == '0') {
				Color changeColor = new Color(255, 150, 100);
				finalImg.setRGB(xPixel, 0, changeColor.getRGB());
//				System.out.println("0 is embedded");
			}
			
			else if(binarybit == '1') {
				Color col2 = new Color(255, 150, 70);
				finalImg.setRGB(xPixel, 0, col2.getRGB());
			}
    		}
			if (binaryString.length() != 0) {
                binaryString = binaryString.substring(1);
//                System.out.println("The binary left" + binaryString.length());
            }
    	}
    	
//    	for(int i = 0; i < finalImg.getWidth(); i++) {
//        	Color checking = new Color(finalImg.getRGB(i, 0));
//	        int blue = checking.getBlue();
//	        System.out.println("This is the blue RGB: " + blue + " when i = " + i);
//        }
    	
    	String imageString = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    	
        try {
        	ImageIO.write(finalImg, "png", new File("C:/Users/Darren/OneDrive/Pictures/Screenshots/finalImg.png"));
        	ImageIO.write(finalImg, "png", outputStream);
        	byte[] imageBytes = outputStream.toByteArray();
        	imageString = new String(Base64.getEncoder().encode(imageBytes),"UTF-8");
        	outputStream.close();
        }
        catch(IOException e) {
        	exceptionMessage.add(e.getMessage());
        }
        
        return imageString;
	}
	
	
	
	public String CollageExtraction() {
		BufferedImage loadImage = null;
    	loadImage = convertBase64ToImage(embeddedImageBase64);
    	
    	int width = loadImage.getWidth();
    	String binaryString = "";
    	
    	for(int i = 0; i < width; i++) {
        	Color colorCheck = new Color(loadImage.getRGB(i, 0));
        	int blue = colorCheck.getBlue();
        	
        	if(blue == 100) {
        		binaryString = binaryString + '0';
        	}else if(blue == 70) {
        		binaryString = binaryString + '1';
        	}
        }
    	
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
	
	
	private BufferedImage[] getImages(Image img, int rows, int column) {
        BufferedImage[] splittedImages = new BufferedImage[rows * column];
        BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.createGraphics();
        g.drawImage(img, 0, 0, null);
        int width = bi.getWidth();
        int height = bi.getHeight();
        int pos = 0;
        int swidth = width / column;
        int sheight = height / rows;
 
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < column; j++) {
                BufferedImage bimg = bi.getSubimage(j * swidth, i * sheight, swidth, sheight);
                splittedImages[pos] = bimg;
                pos++;
            }
        }
 
        return splittedImages;
    }
	
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
