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

    public static final String streamToString(InputStream inputStream) throws Exception {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String read;
            while ((read = br.readLine()) != null) {
                sb.append(read);
            }
            br.close();
            String result = sb.toString();
            return result;
        } catch (IOException e) {
            e.printStackTrace(out);
            throw new Exception(e.getMessage());
        }
    }

    public static final String[] splitString(String input, String criteria) {
        String[] outPut = input.split("\\" + criteria);
        return outPut;
    }

    public static final String phoneFormat(String input) throws Exception {
        try {
            input = input.trim().replace(" ", "").replace("+", "");
            if (input.length() < 10) {
                return "Tel ntago yemewe, invalid tel, tel refuser.";
            }
            String firstPart = input.substring(0, 1);
            if (firstPart.equalsIgnoreCase("07"))
                input = "25" + input;
            return input;
        } catch (Exception e) {
            e.printStackTrace(out);
            throw new Exception(e.getMessage());
        }
    }

    public static final long printDifference(Date startDate, Date endDate) {
        long different = endDate.getTime() - startDate.getTime();
        long elapsedTime = elapsed(startDate, endDate);
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;
        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;
        long elapsedSeconds = different / secondsInMilli;
        return elapsedTime;
    }

    private static long elapsed(Date startDate, Date endDate) {
        long diffMills = endDate.getTime() - startDate.getTime();
        return (diffMills / 1000) / 60;
    }

    public static long diffMinutes(Date startDate, Date endDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
            startDate = format.parse(format.format(startDate));
            endDate = format.parse(format.format(endDate));
            long result = (endDate.getTime() - startDate.getTime()) / (60 * 1000);
            return result;
        } catch (ParseException e) {
            e.printStackTrace(out);
            return 1;
        }
    }

    public static String formatDate(Date date) {
        try {
            SimpleDateFormat sFormat = new SimpleDateFormat("dd-MM-yyy");
            return sFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace(out);
            Calendar calendar = Calendar.getInstance();
            if (date != null) {
                calendar.setTime(date);
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
