/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carpa.library.utilities;

import android.text.TextUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.System.out;

/**
 * @author Hp
 */
public class DataFactory {

    public DataFactory() {
    }

    public static final String objectToString(Object object) throws IOException {
        SimpleDateFormat sFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(sFormat);

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            String jsonData = mapper.writeValueAsString(object);
            return jsonData;
        } catch (IOException e) {
            e.printStackTrace(out);
            throw e;
        }
    }

    public static final String errorObject(Object object) {
        SimpleDateFormat sFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(sFormat);

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            String jsonData = mapper.writeValueAsString(object);
            return jsonData;
        } catch (IOException e) {
            e.printStackTrace(out);
            return e.getMessage();
        }
    }

    public static final List<Object> stringToObjectList(Class className, String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.readValue(jsonString, mapper.getTypeFactory().constructCollectionType(List.class, className));
        } catch (IOException e) {
            throw e;
        }
    }

    public static final Object stringToObject(Class className, String jsonString) throws IOException {
        SimpleDateFormat df = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(df);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            Object result = mapper.readValue(jsonString, className);
            return result;
        } catch (IOException e) {
            throw e;
        }
    }

    public static final String[] splitString(String input, String criteria) {
        String[] outPut = input.split("\\" + criteria);
        return outPut;
    }

    public static String formatDate(Date date) {
        try {
            SimpleDateFormat sFormat = new SimpleDateFormat("yyy-MM-dd");
            return sFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace(out);
            Calendar calendar = Calendar.getInstance();
            if (date != null) {
                calendar.setTime(date);
            }else{
                calendar = Calendar.getInstance();
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(calendar.get(Calendar.YEAR)).append("-").append(calendar.get(Calendar.MONTH) + 1).append("-").append(calendar.get(Calendar.DAY_OF_MONTH)).append(" ");
            stringBuilder.append(calendar.get(Calendar.HOUR_OF_DAY)).append(":").append(calendar.get(Calendar.MINUTE)).append(":").append(calendar.get(Calendar.SECOND));
            return stringBuilder.toString();
        }
    }

    public static Date formatStringDate(String date) throws Exception {
        try {
            SimpleDateFormat sFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
            return sFormat.parse(date);
        } catch (ParseException e) {
            throw new Exception(e.getMessage());
        }
    }

    public static boolean isNumeric(String str) {
        return TextUtils.isDigitsOnly(str);
    }
}
