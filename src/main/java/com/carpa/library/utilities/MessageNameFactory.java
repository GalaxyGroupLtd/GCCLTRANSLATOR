package com.carpa.library.utilities;

public class MessageNameFactory {
    public static String name(String name) {
        String[] hName = DataFactory.splitString(name.replaceAll("-", " ").toUpperCase(), ".");
        if (hName.length == 2) {
            return hName[0];
        }
        return hName[0];
    }

    public static String messageDate(String name) {
        if (name.length() > 12) {
            return name.substring(3, 11);
        }
        return "N/A";
    }
}
