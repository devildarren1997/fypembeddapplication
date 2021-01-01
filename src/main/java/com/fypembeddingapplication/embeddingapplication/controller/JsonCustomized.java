package com.fypembeddingapplication.embeddingapplication.controller;

import java.util.HashMap;
import java.util.Map;

public class JsonCustomized <K,V> {
    private HashMap <K,V> map = new HashMap<>();

    public JsonCustomized() {
    }

    public void put(K key, V value){
        this.map.put(key,value);
    }
    public Map<K, V> returmMap(){
        return this.map;
    }
}
