package com.fypembeddingapplication.embeddingapplication.controller;
import com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.MosaicFilter.MosicEmbed;
import com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.PencilPaintFilter.PencilPaintEmbed;
import com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.PixelExtension.PixelExtensionEmbed;
import com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.CollagesEffect.CollagesEffect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.fypembeddingapplication.embeddingapplication.database.EmbeddedDetailsRepository;
import com.fypembeddingapplication.embeddingapplication.database.EmbeddedImageRepository;
import com.fypembeddingapplication.embeddingapplication.database.OriginalImageRepository;
import com.fypembeddingapplication.embeddingapplication.database.UserRepository;
import com.fypembeddingapplication.embeddingapplication.database.EncryptionDetailsRepository;
import com.fypembeddingapplication.embeddingapplication.database.TempRepository;
import com.fypembeddingapplication.embeddingapplication.model.embeddedDetails;
import com.fypembeddingapplication.embeddingapplication.model.embeddedImage;
import com.fypembeddingapplication.embeddingapplication.model.User;
import com.fypembeddingapplication.embeddingapplication.model.originalImage;
import com.fypembeddingapplication.embeddingapplication.model.encryptionDetail;
import com.fypembeddingapplication.embeddingapplication.model.tempTable;
import com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.MosaicFilter.ImageCompress;
import com.fypembeddingapplication.embeddingapplication.Encryption.ASEEncryption;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fypembeddingapplication.embeddingapplication.responseModel.requestForEmbeddedImageID;
import com.fypembeddingapplication.embeddingapplication.responseModel.requestForExtraction;

@RestController
public class uploadImageForEmbedded {
    @Autowired
    EmbeddedDetailsRepository embeddedDetailsRepository;
    @Autowired
    EmbeddedImageRepository embeddedImageRepository;
    @Autowired
    OriginalImageRepository originalImageRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EncryptionDetailsRepository encryptionDetailsRepository;
    @Autowired
    TempRepository tempRepository;


    @GetMapping ("/test/{userId}")
    public JsonOutput test(@PathVariable("userId") Long id)throws Exception{
        HashMap<String,String>body =new HashMap<>();
        body.put("TestItem","ABC");
        JsonOutput.getJson().setCode("200");
        JsonOutput.getJson().setMessage("Ok");
        JsonOutput.getJson().setBody(body);
        return JsonOutput.getJson();
    }
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/getTempEmbeddedImage")
    @ResponseBody
    public Map<String, Object> getTempEmbeddedImage(@RequestBody String allParams){
        ObjectMapper mapper = new ObjectMapper();
        String jsonString=allParams;
        ArrayList<String> errorMessage = new ArrayList<>();
        ArrayList<String> exceptionMessage = new ArrayList<>();
        JsonCustomized<String,Object> jsonOutPut =new JsonCustomized<>();
        try{
            requestForEmbeddedImageID request = mapper.readValue(jsonString, requestForEmbeddedImageID.class);
            Long userId = request.getUserId();
            String imageBase64 =request.getImageBase64();
            String filter = request.getFilter();
            String secondaryPassword= request.getSecondaryPassword();
            String embedText = request.getEmbedText();
            Timestamp timestamp=new Timestamp(System.currentTimeMillis());
            String imageName= request.getName() +"_" +timestamp.toString();
            ImageCompress compress = new ImageCompress();
            String imageCompressBase64 = compress.compress(imageBase64);
            if (tempRepository.findByUserId(userId).isPresent()){
                try {
                    tempRepository.deleteAllByUserId(userId);
                }catch (Exception e){
                    e.printStackTrace();
                    exceptionMessage.add(e.getMessage());
                }
            }
            String embeddedInformation=embedText;
            ASEEncryption encryption = new ASEEncryption();
            String encryptKey;
            String encryptedInformation;
            if (secondaryPassword!=null){
                encryptKey=null; //if got secondary password, encryptkey is secondary password
                encryptedInformation = encryption.encrypt(embeddedInformation,secondaryPassword);//user secondaryPassword for encryption
            }else {
                encryptKey=encryption.getRandomEncryptKey();
                encryptedInformation = encryption.encrypt(embeddedInformation,encryptKey);
            }
            if(encryption.getErrorMessage().size()>0){
                jsonOutPut.put("status","f");
                errorMessage.addAll(encryption.getErrorMessage());
            }
            if (encryption.getExceptionMessage().size()>0){
                exceptionMessage.addAll(encryption.getExceptionMessage());
            }
            if (encryptedInformation==null){
                jsonOutPut.put("status","f");
                errorMessage.add("Error Code 302. Fail to encrypt your information. You may consider to change your watermark info");
            }
            String imageCompressedOut=null;
            String imageOutPut=null;

            if (filter.equalsIgnoreCase("fragment")){
                MosicEmbed mosicEmbed = new MosicEmbed(encryptedInformation,imageBase64);
                imageOutPut = mosicEmbed.embedding();
                imageCompressedOut=compress.compress(imageOutPut);
                if (imageCompressedOut==null&&imageOutPut!=null){
                    jsonOutPut.put("status","f");
                    errorMessage.add("Error Code 305. Fail to generator a review image");
                }
                if(mosicEmbed.getExceptionMessage().size()>0){
                    jsonOutPut.put("status","f");
                    exceptionMessage.addAll(mosicEmbed.getExceptionMessage());
                }
                if (mosicEmbed.getErrorMessage().size()>0){
                    jsonOutPut.put("status","f");
                    errorMessage.addAll(mosicEmbed.getErrorMessage());
                }
                String embeddedImageName = imageName + "_" + filter+ "_embedded";
                try {
                    tempTable tempTable = new tempTable(userId,imageName,imageBase64,imageCompressBase64,embeddedImageName,filter,imageOutPut,imageCompressedOut,encryptKey,encryptedInformation);
                    tempRepository.save(tempTable);
                    jsonOutPut.put("status","s");
                    jsonOutPut.put("embeddedImage",imageCompressedOut);
                }catch (Exception e){
                    e.printStackTrace();
                    exceptionMessage.add(e.getMessage());
                }
            }
            else if (filter.equalsIgnoreCase("pencil")){
                PencilPaintEmbed pencilPaintEmbed = new PencilPaintEmbed(imageBase64,"PNG");
                imageOutPut = pencilPaintEmbed.embedded(encryptedInformation);
                imageCompressedOut = compress.compress(imageOutPut);
                if (imageCompressedOut==null&&imageOutPut!=null){
                    jsonOutPut.put("status","f");
                    errorMessage.add("Error Code 305. Fail to generator a review image");
                }
                if(pencilPaintEmbed.getExceptionMessage().size()>0){
                    exceptionMessage.addAll(pencilPaintEmbed.getExceptionMessage());
                }
                if (pencilPaintEmbed.getErrorMessage().size()>0){
                    jsonOutPut.put("status","f");
                    errorMessage.addAll(pencilPaintEmbed.getErrorMessage());
                }
                String embeddedImageName = imageName + "_" + filter+ "_embedded";
                try {
                    tempTable tempTable = new tempTable(userId,imageName,imageBase64,imageCompressBase64,embeddedImageName,filter,imageOutPut,imageCompressedOut,encryptKey,encryptedInformation);
                    tempRepository.save(tempTable);
                    jsonOutPut.put("status","s");
                    jsonOutPut.put("embeddedImage",imageCompressedOut);
                }catch (Exception e){
                    e.printStackTrace();
                    exceptionMessage.add(e.getMessage());
                }
            }
            else if (filter.equalsIgnoreCase("pixelextension")) {
            	PixelExtensionEmbed pixelExtensionEmbed = new PixelExtensionEmbed(encryptedInformation, imageBase64);
            	imageOutPut = pixelExtensionEmbed.PixelExtension();
            	imageCompressedOut = compress.compress(imageOutPut);
            	if(imageCompressedOut==null && imageOutPut!=null) {
            		jsonOutPut.put("status", "f");
            		errorMessage.add("Error Code 305. Fail to generate a review image");
            	}
            	if(pixelExtensionEmbed.getExceptionMessage().size()>0) {
            		exceptionMessage.addAll(pixelExtensionEmbed.getExceptionMessage());
            	}
            	if (pixelExtensionEmbed.getErrorMessage().size()>0){
                    jsonOutPut.put("status","f");
                    errorMessage.addAll(pixelExtensionEmbed.getErrorMessage());
                }
            	String embeddedImageName = imageName + "_" + filter+ "_embedded";
            	try {
                    tempTable tempTable = new tempTable(userId,imageName,imageBase64,imageCompressBase64,embeddedImageName,filter,imageOutPut,imageCompressedOut,encryptKey,encryptedInformation);
                    tempRepository.save(tempTable);
                    jsonOutPut.put("status","s");
                    jsonOutPut.put("embeddedImage",imageCompressedOut);
                }catch (Exception e){
                    e.printStackTrace();
                    exceptionMessage.add(e.getMessage());
                }
            }
            else if(filter.equalsIgnoreCase("collageseffect")) {
            	CollagesEffect collagesEffect = new CollagesEffect(encryptedInformation, imageBase64);
            	imageOutPut = collagesEffect.Collages();
            	imageCompressedOut = compress.compress(imageOutPut);
            	if(imageCompressedOut==null && imageOutPut!=null) {
            		jsonOutPut.put("status", "f");
            		errorMessage.add("Error Code 305. Fail to generate a review image");
            	}
            	if(collagesEffect.getExceptionMessage().size()>0) {
            		exceptionMessage.addAll(collagesEffect.getExceptionMessage());
            	}
            	if (collagesEffect.getErrorMessage().size()>0){
                    jsonOutPut.put("status","f");
                    errorMessage.addAll(collagesEffect.getErrorMessage());
                }
            	
            	String embeddedImageName = imageName + "_" + filter+ "_embedded";
            	try {
                    tempTable tempTable = new tempTable(userId,imageName,imageBase64,imageCompressBase64,embeddedImageName,filter,imageOutPut,imageCompressedOut,encryptKey,encryptedInformation);
                    tempRepository.save(tempTable);
                    jsonOutPut.put("status","s");
                    jsonOutPut.put("embeddedImage",imageCompressedOut);
                }catch (Exception e){
                    e.printStackTrace();
                    exceptionMessage.add(e.getMessage());
                }
            }
        }
        catch (JsonParseException e) { e.printStackTrace();exceptionMessage.add(e.getMessage());}
        catch (JsonMappingException e) { e.printStackTrace(); exceptionMessage.add(e.getMessage());}
        catch (IOException e) { e.printStackTrace(); exceptionMessage.add(e.getMessage());}
        if(errorMessage.size()!=0||exceptionMessage.size()!=0){
            jsonOutPut.put("status","f");
        }
        jsonOutPut.put("error",errorMessage);
        jsonOutPut.put("exception",exceptionMessage);
        return jsonOutPut.returmMap();
    }
    @GetMapping ("/get_review_embedded_image/{userId}")
    public Map<String, Object> getReviewEmbeddedImage(@PathVariable("userId") Long id){
        ArrayList<String> errorMessage = new ArrayList<>();
        JsonCustomized<String,Object> jsonOutPut =new JsonCustomized<>();
        ArrayList<String> exceptionMessage = new ArrayList<>();
        try {
            final Optional<tempTable> retrieveTempData = tempRepository.findByUserId(id);
            if(retrieveTempData.isPresent()){
                jsonOutPut.put("status","f");
                errorMessage.add("Error Code 306. Fail to get embedded image");
            }else {
                tempTable tempTable = retrieveTempData.get();
                jsonOutPut.put("embeddedImage",tempTable.getEmbeddedImageCompressedBase64());
                jsonOutPut.put("status","s");
            }
        }catch (Exception e){
            exceptionMessage.add(e.getMessage());
        }
        if(errorMessage.size()!=0||exceptionMessage.size()!=0){
            jsonOutPut.put("status","f");
        }
        jsonOutPut.put("error",errorMessage);
        jsonOutPut.put("exception",exceptionMessage);
        return jsonOutPut.returmMap();
    }
    @Transactional(rollbackFor = Exception.class)
    @GetMapping ("/confirmEmbeddedImage/{userId}")
    public Map<String, Object> confirmImageEmbedded(@PathVariable("userId") Long id)throws Exception{
        ArrayList<String> errorMessage = new ArrayList<>();
        JsonCustomized<String,Object> jsonOutPut =new JsonCustomized<>();
        ArrayList<String> exceptionMessage = new ArrayList<>();
        final Optional<tempTable> retrieveTempData = tempRepository.findByUserId(id);

        if(!retrieveTempData.isPresent()){
            jsonOutPut.put("status","f");
            errorMessage.add("Error Code 101.Error occur in database.");
        }
        else {
            tempTable tempTable =retrieveTempData.get();
            try {
                originalImage originalImage =new originalImage(id,tempTable.getOriginalImageName(),tempTable.getOriginalImageBase64(),tempTable.getOriginalImageCompressedBase64());
                encryptionDetail encryptionDetail =new encryptionDetail(id,tempTable.getEncryptionKey(),tempTable.getEncryptedString());
                embeddedImage embeddedImage =new embeddedImage(id,tempTable.getEmbeddedImageName(),tempTable.getFilter(),tempTable.getEmbeddedImage1Base64(),tempTable.getEmbeddedImageCompressedBase64());
                originalImageRepository.save(originalImage);
                encryptionDetailsRepository.save(encryptionDetail);
                embeddedImageRepository.save(embeddedImage);
            }catch (Exception e){
                exceptionMessage.add(e.getMessage());
            }
            final Optional<embeddedImage> retrievedEmbeddedImageByName = embeddedImageRepository.findByName(tempTable.getEmbeddedImageName());
            Long embeddedImageId=null;
            if (!retrievedEmbeddedImageByName.isPresent()){
                jsonOutPut.put("status","f");
                errorMessage.add("Error Code 101.Error occur in database.");
            }else {
                embeddedImageId = retrievedEmbeddedImageByName.get().getEmbeddedImageId();
            }
            final Optional<originalImage> retrievedOriginalImageByName = originalImageRepository.findByName(tempTable.getOriginalImageName());
            Long originalImageId=null;
            if (!retrievedOriginalImageByName.isPresent()){
                jsonOutPut.put("status","f");
                errorMessage.add("Error Code 101.Error occur in database.");
            }
            else{
                originalImageId = retrievedOriginalImageByName.get().getOriginalImageId();
            }
            try {
                embeddedDetails embeddedDetails = new embeddedDetails(id,originalImageId,embeddedImageId,tempTable.getFilter());
                embeddedDetailsRepository.save(embeddedDetails);
                jsonOutPut.put("embeddedImage",tempTable.getEmbeddedImage1Base64());
                tempRepository.deleteAllByUserId(id);
                jsonOutPut.put("status","s");
            }catch (Exception e){
                exceptionMessage.add(e.getMessage());
            }
        }
        if(errorMessage.size()!=0||exceptionMessage.size()!=0){
            jsonOutPut.put("status","f");
        }
        jsonOutPut.put("error",errorMessage);
        jsonOutPut.put("exception",exceptionMessage);
        return jsonOutPut.returmMap();
    }
    @Transactional(rollbackFor = Exception.class)
    @DeleteMapping("/cancelTempEmbedding/{userId}")
    public Map<String,Object> cancelEmbedding (@PathVariable("userId") Long id)throws Exception  {
        ArrayList<String> errorMessage = new ArrayList<>();
        ArrayList<String> exceptionMessage = new ArrayList<>();
        JsonCustomized<String,Object> jsonOutPut =new JsonCustomized<>();
        final Optional<tempTable> retrieveTempData = tempRepository.findByUserId(id);
        if(!retrieveTempData.isPresent()){
            jsonOutPut.put("status","f");
            errorMessage.add("Error Code 101.Error occur in database.");
        }
        else {
            try {
                tempRepository.deleteAllByUserId(id);

            }catch (Exception e){
                exceptionMessage.add(e.getMessage());
            }
            if (!tempRepository.findByUserId(id).isPresent()){
                jsonOutPut.put("status","f");
                errorMessage.add("Error Code 307.Fail to cancel the embedding process.");
            }
            else {
                jsonOutPut.put("status","s");
            }
        }
        if(errorMessage.size()!=0||exceptionMessage.size()!=0){
            jsonOutPut.put("status","f");
        }
        jsonOutPut.put("error",errorMessage);
        jsonOutPut.put("exception",exceptionMessage);
        return jsonOutPut.returmMap();
    }
    @Transactional(rollbackFor =  Exception.class)
    @PutMapping("/refresh/{userId}")
    public Map<String,Object> refreshEmbedding (@PathVariable("userId") Long id)throws Exception{
        JsonCustomized<String,Object> jsonOutPut =new JsonCustomized<>();
        final Optional<tempTable> retrieveTempDetail =tempRepository.findByUserId(id);
        tempTable newTempTable =retrieveTempDetail.get();
        String originalImageBase64 = retrieveTempDetail.get().getOriginalImageBase64();
        String encrytedInformation = retrieveTempDetail.get().getEncryptedString();
        MosicEmbed mosicEmbed = new MosicEmbed(encrytedInformation,originalImageBase64);
        String embeddedImageBase64 = mosicEmbed.embedding();
        ImageCompress compress =new ImageCompress();
        String embeddedImageCompressBase64 =  compress.compress(embeddedImageBase64);
        newTempTable.setEmbeddedImage1Base64(embeddedImageBase64);
        newTempTable.setEmbeddedImageCompressedBase64(embeddedImageCompressBase64);
        tempRepository.save(newTempTable);
        jsonOutPut.put("embeddedImage",embeddedImageCompressBase64);
        jsonOutPut.put("status","s");
        return jsonOutPut.returmMap();
    }
    @GetMapping(path = { "/get/{userId}" })
    public Map<String, String> getUserForTest (@PathVariable("userId") Long id) throws IOException {
        final Optional<User> retrievedUserID = userRepository.findById(id);
        JsonCustomized<String,String> map =new JsonCustomized<String, String>();
        map.put("default_digital_watermark", retrievedUserID.get().getDefaultDigitalWatermark() );
        return map.returmMap();
    }
    @GetMapping(path = { "/getEmbeddedImageFromDatabase/{embeddedImageId}" })
    public Map<String, Object> getEmbeddedImageFromDatabase (@PathVariable("embeddedImageId") String id) throws IOException{
        Long embeddedImageId = Long.parseLong(id);
        JsonCustomized<String,Object> jsonOutPut =new JsonCustomized<>();
        final Optional<embeddedImage> retrievedEmbeddedImage = embeddedImageRepository.findByEmbeddedImageId(embeddedImageId);
        String embeddedImageCompressedBase64 = retrievedEmbeddedImage.get().imageCompressedBase64();
        if(embeddedImageCompressedBase64==null){
            String errorMessage = "Cannot find the embeddedImage";
            jsonOutPut.put("error",errorMessage);
            jsonOutPut.put("status","f");
        }else {
            jsonOutPut.put("embeddedImageCompressedBase64",embeddedImageCompressedBase64);
            jsonOutPut.put("status","s");
        }

        return jsonOutPut.returmMap();
    }
    @PostMapping (path = {"/extractFromImage"})
    @ResponseBody
    public Map<String,Object> getHiddenInformation (@RequestBody String allParams){
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = allParams;
        ArrayList<String> errorMessage = new ArrayList<>();
        JsonCustomized<String,Object> jsonOutPut =new JsonCustomized<>();
        try{
            requestForExtraction request = mapper.readValue(jsonString, requestForExtraction.class);
            String embeddedImage = request.getEmbeddedImage();
            Long userId = request.getUserId();
            String filter = request.getFilter();
            String secondaryPassword=request.getSecondaryPassword();
            if (filter.equalsIgnoreCase("pencil")){
                    PencilPaintEmbed pencilPaintEmbed = new PencilPaintEmbed();
                    String extractedString = pencilPaintEmbed.extract(embeddedImage);
                    if (secondaryPassword==null){
                        Optional<List<encryptionDetail>> retrieveEmbeddedDetails =encryptionDetailsRepository.findByUserIdAndEncryptedString(userId,extractedString);
                        if (!retrieveEmbeddedDetails.isPresent()){
                            jsonOutPut.put("status","f");
                            errorMessage.add("Error code 401. Fail to extract information");
                        }
                        else {
                            ASEEncryption aseEncryption = new ASEEncryption();
                            String encryptionKey =retrieveEmbeddedDetails.get().get(0).getEncryptionKey();
                            if (encryptionKey==null){
                                //Only the embedding without secondary password will have a null value encryptionKey
                                jsonOutPut.put("status","f");
                                errorMessage.add("Error code 402. Enter your secondary password");
                            }else {
                                String hiddenInformation = aseEncryption.decrypt(extractedString,encryptionKey);
                                jsonOutPut.put("status","s");
                                jsonOutPut.put("hiddenInformation",hiddenInformation);
                            }

                        }
                    }else{
                        ASEEncryption aseEncryption = new ASEEncryption();
                        String hiddenInformation = aseEncryption.decrypt(extractedString,secondaryPassword);
                        if (hiddenInformation==null){
                            jsonOutPut.put("status","f");
                            errorMessage.add("Error code 403. You may enter a wrong secondary password");
                        }else {
                            jsonOutPut.put("status","s");
                            jsonOutPut.put("hiddenInformation",hiddenInformation);
                        }

                    }

            }
            else if (filter.equalsIgnoreCase("fragment") ){
                MosicEmbed mosicEmbed = new MosicEmbed(embeddedImage);
                String extractedString = mosicEmbed.extraction();
                if (secondaryPassword==null){
                    Optional<List<encryptionDetail>> retrieveEmbeddedDetails =encryptionDetailsRepository.findByUserIdAndEncryptedString(userId,extractedString);
                    if (!retrieveEmbeddedDetails.isPresent()){
                        jsonOutPut.put("status","f");
                        errorMessage.add("Error code 401. Fail to extract information");
                    }
                    else {
                        ASEEncryption aseEncryption = new ASEEncryption();
                        String encryptionKey =retrieveEmbeddedDetails.get().get(0).getEncryptionKey();
                        if (encryptionKey==null){
                            //Only the embedding without secondary password will have a null value encryptionKey
                            jsonOutPut.put("status","f");
                            errorMessage.add("Error code 402. Enter your secondary password");
                        }else {
                            String hiddenInformation = aseEncryption.decrypt(extractedString,encryptionKey);
                            jsonOutPut.put("status","s");
                            jsonOutPut.put("hiddenInformation",hiddenInformation);
                        }
                    }
                }else {
                    ASEEncryption aseEncryption = new ASEEncryption();
                    String hiddenInformation = aseEncryption.decrypt(extractedString,secondaryPassword);
                    if (hiddenInformation==null){
                        jsonOutPut.put("status","f");
                        errorMessage.add("Error code 403. You may enter a wrong secondary password");
                    }else {
                        jsonOutPut.put("status","s");
                        jsonOutPut.put("hiddenInformation",hiddenInformation);
                    }
                }
            }
            else if(filter.equalsIgnoreCase("pixelextension")) {
            	PixelExtensionEmbed pixelExtensionEmbed = new PixelExtensionEmbed(embeddedImage);
            	String extractedString = pixelExtensionEmbed.PixelExtensionExtraction();
            	
            	if (secondaryPassword==null){
                    final Optional<List<encryptionDetail>> retrieveEmbeddedDetails =encryptionDetailsRepository.findByUserIdAndEncryptedString(userId,extractedString);
                    if (!retrieveEmbeddedDetails.isPresent()){
                        jsonOutPut.put("status","f");
                        errorMessage.add("Error code 401. Fail to extract information");
                    }
                    else {
                    	if(extractedString.equalsIgnoreCase("error102")) {
                    		jsonOutPut.put("status","f");
                            errorMessage.add("Error code 404. Fail to get complete binaryString embedded into the image.");
                    	}
                    	else{
                    		ASEEncryption aseEncryption = new ASEEncryption();
                            String encryptionKey =retrieveEmbeddedDetails.get().get(0).getEncryptionKey();
                            if (encryptionKey==null){
                                //Only the embedding without secondary password will have a null value encryptionKey
                                jsonOutPut.put("status","f");
                                errorMessage.add("Error code 402. Enter your secondary password");
                            }else {
                                String hiddenInformation = aseEncryption.decrypt(extractedString,encryptionKey);
                                jsonOutPut.put("status","s");
                                jsonOutPut.put("hiddenInformation",hiddenInformation);
                            }
                    	} 
                    }
                }
            	else {
            		if(extractedString.equalsIgnoreCase("error102")){
            			jsonOutPut.put("status","f");
                        errorMessage.add("Error code 404. Fail to get complete binaryString embedded into the image.");
            		}
            		else {
            			 ASEEncryption aseEncryption = new ASEEncryption();
                         String hiddenInformation = aseEncryption.decrypt(extractedString,secondaryPassword);
                        if (hiddenInformation==null){
                            jsonOutPut.put("status","f");
                            errorMessage.add("Error code 403. You may enter a wrong secondary password");
                        }else {
                            jsonOutPut.put("status","s");
                            jsonOutPut.put("hiddenInformation",hiddenInformation);
                        }
            		}
                }
            }
            else if(filter.equalsIgnoreCase("collageseffect")) {
            	CollagesEffect collagesEffect = new CollagesEffect(embeddedImage);
            	String extractedString = collagesEffect.CollageExtraction();
            	
            	if (secondaryPassword==null){
                    final Optional<List<encryptionDetail>> retrieveEmbeddedDetails =encryptionDetailsRepository.findByUserIdAndEncryptedString(userId,extractedString);
                    if (!retrieveEmbeddedDetails.isPresent()){
                        jsonOutPut.put("status","f");
                        errorMessage.add("Error code 401. Fail to extract information");
                    }
                    else {
                    	if(extractedString.equalsIgnoreCase("error102")){
                			jsonOutPut.put("status","f");
                            errorMessage.add("Error code 404. Fail to get complete binaryString embedded into the image.");
                		}
                		else {
                			 ASEEncryption aseEncryption = new ASEEncryption();
                            String encryptionKey =retrieveEmbeddedDetails.get().get(0).getEncryptionKey();
                            if (encryptionKey==null){
                                //Only the embedding without secondary password will have a null value encryptionKey
                                jsonOutPut.put("status","f");
                                errorMessage.add("Error code 402. Enter your secondary password");
                            }else {
                                String hiddenInformation = aseEncryption.decrypt(extractedString,encryptionKey);
                                jsonOutPut.put("status","s");
                                jsonOutPut.put("hiddenInformation",hiddenInformation);
                            }
                		}
                    }
                }
            	else {
            		if(extractedString.equalsIgnoreCase("error102")){
            			jsonOutPut.put("status","f");
                        errorMessage.add("Error code 404. Fail to get complete binaryString embedded into the image.");
            		}
            		else {
            			 ASEEncryption aseEncryption = new ASEEncryption();
                         String hiddenInformation = aseEncryption.decrypt(extractedString,secondaryPassword);
                        if (hiddenInformation==null){
                            jsonOutPut.put("status","f");
                            errorMessage.add("Error code 403. You may enter a wrong secondary password");
                        }else {
                            jsonOutPut.put("status","s");
                            jsonOutPut.put("hiddenInformation",hiddenInformation);
                        }
            		}
                }
            }

        }

        catch (IOException e) { e.printStackTrace(); errorMessage.add(e.getMessage());}
        if(errorMessage.size()!=0){
            jsonOutPut.put("status","f");
        }
        jsonOutPut.put("error",errorMessage);
        return jsonOutPut.returmMap();
    }
    @GetMapping(path = { "/getOriginalImageList/{userId}" })
    public Map<String, Object> getOriginalImageList (@PathVariable("userId") String id) throws IOException{
        Long userId = Long .parseLong(id);
        ArrayList<String> errorMessage = new ArrayList<>();
        List <imageDetail> list =new ArrayList<>();

        JsonCustomized<String,Object> jsonOutPut =new JsonCustomized<>();
        final Optional <List<originalImage>> retrieveOriginalImageList = originalImageRepository.findByUserId(userId);
        if(retrieveOriginalImageList.get().size()!=0){
            for (int i =0 ;i<retrieveOriginalImageList.get().size();i++){
                list.add(new imageDetail(retrieveOriginalImageList.get().get(i).getOriginalImageId(),retrieveOriginalImageList.get().get(i).getImageCompressedBase64(),retrieveOriginalImageList.get().get(i).getName()));
            }
            jsonOutPut.put("originalImageList" , list);
            jsonOutPut.put("status","s");
        }
        else {
            jsonOutPut.put("status","f");
            errorMessage.add("Error Code 101.Error occur in database.");
        }
        jsonOutPut.put("error",errorMessage);
        return jsonOutPut.returmMap();
    }
    class imageDetail{
        String image;
        String imageName;
        Long id;
        public imageDetail(Long id,String image, String imageName) {
            this.image = image;
            this.imageName = imageName;
            this.id = id;
        }

        public imageDetail() {
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getImageName() {
            return imageName;
        }

        public void setImageName(String imageName) {
            this.imageName = imageName;
        }
    }
}
