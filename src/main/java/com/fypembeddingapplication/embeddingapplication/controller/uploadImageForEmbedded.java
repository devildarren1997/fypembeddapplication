package com.fypembeddingapplication.embeddingapplication.controller;
import com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.MosaicFilter.MosicEmbed;
import com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.PencilPaintFilter.PencilPaintEmbed;
import com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.PixelExtension.PixelExtensionEmbed;
import com.fypembeddingapplication.embeddingapplication.EmbeddingAlgorithm.CollagesEffect.CollagesEffect;
import com.fypembeddingapplication.embeddingapplication.database.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.fypembeddingapplication.embeddingapplication.model.encryptionDetail;
import com.fypembeddingapplication.embeddingapplication.Encryption.ASEEncryption;
import com.fypembeddingapplication.embeddingapplication.model.Buffer;
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
    UserRepository userRepository;
    @Autowired
    EncryptionDetailsRepository encryptionDetailsRepository;
    @Autowired
    BufferRepository bufferRepository;


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
            if (bufferRepository.findByUserId(userId).isPresent()){
                try {
                    bufferRepository.deleteAllByUserId(userId);
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
            String imageOutPut=null;

            if (filter.equalsIgnoreCase("fragment")){
                MosicEmbed mosicEmbed = new MosicEmbed(encryptedInformation,imageBase64);
                imageOutPut = mosicEmbed.embedding();
                if (imageOutPut==null){
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
                try {
                    Buffer buffer= new Buffer(userId,filter,encryptKey,encryptedInformation);
                    bufferRepository.save(buffer);
                    jsonOutPut.put("status","s");
                    jsonOutPut.put("embeddedImage",imageOutPut);
                }catch (Exception e){
                    e.printStackTrace();
                    exceptionMessage.add(e.getMessage());
                }
            }
            else if (filter.equalsIgnoreCase("pencil")){
                PencilPaintEmbed pencilPaintEmbed = new PencilPaintEmbed(imageBase64,"PNG");
                imageOutPut = pencilPaintEmbed.embedded(encryptedInformation);
                if (imageOutPut==null){
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
                try {
                    Buffer buffer = new Buffer(userId,filter,encryptKey,encryptedInformation);
                    bufferRepository.save(buffer);
                    jsonOutPut.put("status","s");
                    jsonOutPut.put("embeddedImage",imageOutPut);
                }catch (Exception e){
                    e.printStackTrace();
                    exceptionMessage.add(e.getMessage());
                }
            }
            else if (filter.equalsIgnoreCase("pixelextension")) {
            	PixelExtensionEmbed pixelExtensionEmbed = new PixelExtensionEmbed(encryptedInformation, imageBase64);
            	imageOutPut = pixelExtensionEmbed.PixelExtension();
            	if(imageOutPut==null) {
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
            	try {
                    Buffer buffer= new Buffer(userId,filter,encryptKey,encryptedInformation);
                    bufferRepository.save(buffer);
                    jsonOutPut.put("status","s");
                    jsonOutPut.put("embeddedImage",imageOutPut);
                }catch (Exception e){
                    e.printStackTrace();
                    exceptionMessage.add(e.getMessage());
                }
            }
            else if(filter.equalsIgnoreCase("collageseffect")) {
            	CollagesEffect collagesEffect = new CollagesEffect(encryptedInformation, imageBase64);
            	imageOutPut = collagesEffect.Collages();
            	if(imageOutPut==null) {
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

            	try {
                    Buffer buffer= new Buffer(userId,filter,encryptKey,encryptedInformation);
                    bufferRepository.save(buffer);
                    jsonOutPut.put("status","s");
                    jsonOutPut.put("embeddedImage",imageOutPut);
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

    @Transactional(rollbackFor = Exception.class)
    @GetMapping ("/confirmEmbeddedImage/{userId}")
    public Map<String, Object> confirmImageEmbedded(@PathVariable("userId") Long id)throws Exception{
        ArrayList<String> errorMessage = new ArrayList<>();
        JsonCustomized<String,Object> jsonOutPut =new JsonCustomized<>();
        ArrayList<String> exceptionMessage = new ArrayList<>();
        final Optional<Buffer> retrieveBufferData = bufferRepository.findByUserId(id);

        if(!retrieveBufferData.isPresent()){
            jsonOutPut.put("status","f");
            errorMessage.add("Error Code 101.Error occur in database.");
        }
        else {
            Buffer buffer =retrieveBufferData.get();
            try {
                encryptionDetail encryptionDetail =new encryptionDetail(id,buffer.getEncryptionKey(),buffer.getEncryptedString());
                encryptionDetailsRepository.save(encryptionDetail);
            }catch (Exception e){
                exceptionMessage.add(e.getMessage());
            }

            try {
                bufferRepository.deleteAllByUserId(id);
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
        final Optional<Buffer> retrieveBufferData = bufferRepository.findByUserId(id);
        if(!retrieveBufferData.isPresent()){
            jsonOutPut.put("status","f");
            errorMessage.add("Error Code 101.Error occur in database.");
        }
        else {
            try {
                bufferRepository.deleteAllByUserId(id);

            }catch (Exception e){
                exceptionMessage.add(e.getMessage());
            }
            if (bufferRepository.findByUserId(id).isPresent()){
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
                            errorMessage.add("Error code 403. You may enter a wrong secondary password or select wrong image effect for extraction");
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
                        errorMessage.add("Error code 403. You may enter a wrong secondary password or select wrong image effect for extraction");
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
                    	if(extractedString.equalsIgnoreCase("error404")) {
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
                            errorMessage.add("Error code 403. You may enter a wrong secondary password or select wrong image effect for extraction");
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
                    	if(extractedString.equalsIgnoreCase("error404")){
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
            		if(extractedString.equalsIgnoreCase("error404")){
            			jsonOutPut.put("status","f");
                        errorMessage.add("Error code 404. Fail to get complete binaryString embedded into the image.");
            		}
            		else {
            			 ASEEncryption aseEncryption = new ASEEncryption();
                         String hiddenInformation = aseEncryption.decrypt(extractedString,secondaryPassword);
                        if (hiddenInformation==null){
                            jsonOutPut.put("status","f");
                            errorMessage.add("Error code 403. You may enter a wrong secondary password or select wrong image effect for extraction");
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

}
