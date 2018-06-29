package com.carpa.library.utilities;

public class DownloadTaskListener {
    private static boolean IS_SCHEDULED;

    public static void setSchedule(boolean isScheduled) {
        IS_SCHEDULED = isScheduled;
    }

    public static boolean isScheduled() {
        return IS_SCHEDULED;
    }
}
