package com.fypembeddingapplication.embeddingapplication.Encryption;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class ASEEncryption {
    private SecretKeySpec secretKey;
    private byte[] encryptKeyByte;
    private ArrayList<String> errorMessage = new ArrayList<>();
    private ArrayList<String> exceptionMessage = new ArrayList<>();
    public ASEEncryption() {
    }

    public void setKey(String encryptKey) {
        MessageDigest messageDigest = null;
        try {
            encryptKeyByte = encryptKey.getBytes("UTF-8");
            messageDigest = MessageDigest.getInstance("SHA-1");
            encryptKeyByte = messageDigest.digest(encryptKeyByte);
            encryptKeyByte = Arrays.copyOf(encryptKeyByte, 16);
            secretKey = new SecretKeySpec(encryptKeyByte, "AES");
        }
        catch (NoSuchAlgorithmException e) {
            exceptionMessage.add(e.getMessage());
        }
        catch (UnsupportedEncodingException e) {
            exceptionMessage.add(e.getMessage());
        }
    }
    public String encrypt(String strToEncrypt, String encryptKey)
    {
        try
        {
            setKey(encryptKey);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        }
        catch (Exception e)
        {
            exceptionMessage.add(e.getMessage());
        }
        return null;
    }
    public  String decrypt(String strToDecrypt, String encryptKey)
    {
        try
        {
            setKey(encryptKey);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        }
        catch (Exception e)
        {
            exceptionMessage.add(e.getMessage());
        }
        return null;
    }
    public  ArrayList<String> getErrorMessage() {
        return errorMessage;
    }

    public  void setErrorMessage(ArrayList<String> errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ArrayList<String> getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(ArrayList<String> exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getRandomEncryptKey (){
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
        int n = 8;
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }
}
