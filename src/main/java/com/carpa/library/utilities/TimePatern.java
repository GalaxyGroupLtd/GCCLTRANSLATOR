package com.carpa.library.utilities;

import java.util.regex.Pattern;

public class TimePatern {
    public static String TIME_PATERN_FORMAT = "[0-9]{8}";
    public static Pattern TIME_PATTERN = Pattern.compile(TIME_PATERN_FORMAT);
}
