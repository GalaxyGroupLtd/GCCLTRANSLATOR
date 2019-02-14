package com.carpa.library.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LanguageCache {
    public static String SET_LANGUAGE = "SET_LANGUAGE";
    private static ConcurrentHashMap<String, List<Object>> cache = new ConcurrentHashMap<>();
    public static void ADD(String key, List<Object> value){
        remove(key);
        cache.put(key, value);
    }
    public static List<Object> find(String key){
        return cache.get(key);
    }
    public static void remove(String key){
        cache.remove(key);
    }
    public static boolean isContained(String key){
        return cache.containsKey(key);
    }
    public static void clearCache(){
        cache.clear();
    }
    public static List<Object> listAll(){
        List<Object> objects = new ArrayList<>();
        for(Map.Entry<String, List<Object>> entry : cache.entrySet()){
            objects.addAll(entry.getValue());
        }
        return objects;
    }
}
