package com.carpa.library.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

public class MessageNameFactory {
    public static String name(String name) {
        String[] hName = DataFactory.splitString(name.replaceAll("-", " ").toUpperCase(), ".");
        String messageName = hName[0];
        String[] formatedName = DataFactory.splitString(messageName, " ");
        if(formatedName.length <= 0){
            return messageName;
        }
        String fName = "";
        for(int i = 1; i <= formatedName.length - 1; i++){
            fName += formatedName[i]+ " ";
        }
        return fName.trim();
    }

    public static String messageDate(String name) {
        String messageDate = findTime(name);
        return messageDate == null ? "N/A" : messageDate;
    }

    public static final String findTime(String name) {
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyMMdd");

        List<String> allMatches = new ArrayList<>();
        Matcher matcher;
            matcher = TimePatern.TIME_PATTERN.matcher(name);

        while (matcher.find()) {
            allMatches.add(matcher.group());
        }
        if (allMatches.isEmpty())
            return null;
        else{
            try {
                String found = allMatches.get(0);
                String year = found.substring(0,4);
                String month = found.substring(4,6);
                String day = found.substring(6, found.length());
                String date = year+"-"+month+"-"+day+" 00:00:00";
                return date;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
