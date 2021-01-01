package com.fypembeddingapplication.embeddingapplication.controller;

import java.util.HashMap;

public class JsonOutput  <K,V>{
    private static JsonOutput jsonOutput;
    private String code;
    private String message;
    private HashMap<K,V> body = new HashMap<>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HashMap<K,V> getBody() {
        return body;
    }

    public void setBody(HashMap<K,V> body) {
        this.body = body;
    }

    private JsonOutput(){

    }
    public static JsonOutput getJson(){
        if (jsonOutput == null){
            jsonOutput = new JsonOutput();
        }
        return jsonOutput;
    }

}
