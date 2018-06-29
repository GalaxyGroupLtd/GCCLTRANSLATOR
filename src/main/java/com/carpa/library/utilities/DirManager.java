package com.carpa.library.utilities;

import android.os.Environment;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DirManager {
    public static final String ROOT = "GCCL_LIBRARY";

    public static final String getRoot() {
        File mFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), ROOT);
        return mFile.getAbsolutePath();
    }

    public static void rootDir() throws Exception {
        boolean isCreated = true;
        File mFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), ROOT);
        if (!mFile.exists()) {
            //file don't exist
            isCreated = mFile.mkdir();
        }
        if (!isCreated)
            throw new Exception("GCCL library directory is missing.");
        //continue with sub directories if any
    }

    public static boolean isFileExist(String fileName) {
        File mFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), ROOT + File.separatorChar + fileName);
        return mFile.exists();
    }

    public static List<File> listFiles() throws Exception {
        File mFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), ROOT);
        if (!mFile.exists())
            throw new Exception("GCCL library directory is missing.");
        return Arrays.asList(mFile.listFiles());
    }

    public static File filePath(String fileName) {
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath(), ROOT + File.separatorChar + fileName);
    }

    public static String getProgressDisplayLine(long currentBytes, long totalBytes) {
        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes);
    }

    private static String getBytesToMBString(long bytes) {
        return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00));
    }
}
