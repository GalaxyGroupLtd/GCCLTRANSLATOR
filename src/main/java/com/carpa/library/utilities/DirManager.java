package com.carpa.library.utilities;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DirManager {
    public static final String ROOT = "GCCL_LIBRARY";
    public static final String STORAGE_ENVIRONMENT = SDPath();

    public static String getRoot() {
        File mFile = new File(STORAGE_ENVIRONMENT, ROOT);
        return mFile.getAbsolutePath();
    }

    public static void rootDir() throws Exception {
        boolean isCreated = true;
        File mFile = new File(STORAGE_ENVIRONMENT, ROOT);
        try {
            if (!mFile.exists()) {
                //file don't exist
                isCreated = mFile.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isCreated)
            throw new Exception("GCCL library directory couldn't be created or it is missing.");
        //continue with sub directories if any
    }

    public static boolean isFileExist(String fileName) {
        return new File(STORAGE_ENVIRONMENT, ROOT + File.separatorChar + fileName).exists();
    }

    public static List<File> listFiles() throws Exception {
        File mFile = new File(STORAGE_ENVIRONMENT, ROOT);
        if (!mFile.exists())
            throw new Exception("GCCL library directory is missing.");
        return Arrays.asList(mFile.listFiles());
    }

    public static File filePath(String fileName) {
        return new File(STORAGE_ENVIRONMENT, ROOT + File.separatorChar + fileName);
    }

    public static String getProgressDisplayLine(long currentBytes, long totalBytes) {
        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes);
    }

    private static String getBytesToMBString(long bytes) {
        return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00));
    }

    public static List<File> listFiles(boolean isFromRoot, boolean isOnlyFiles, String directoryName) {
        File directory;
        List<File> resultList = new ArrayList<>();
        if (isFromRoot) {
            directory = new File(getRoot());
        } else {
            if (directoryName != null)
                directory = new File(directoryName);
            else
                return resultList;
        }
        // get all the files from a directory
        File[] fList = directory.listFiles();
        resultList.addAll(Arrays.asList(fList));
        for (File file : fList) {
            if (file.isFile()) {
                if (isOnlyFiles)
                    resultList.add(file);
                System.out.println(file.getAbsolutePath());
            } else if (file.isDirectory()) {
                if(!isOnlyFiles)
                    resultList.add(file);
                resultList.addAll(listFiles(false, isOnlyFiles, file.getAbsolutePath()));
            }
        }
        return resultList;
    }

    public static String SDPath() {
        String sdcardpath = "";

        //Datas
        if (new File("/data/sdext4/").exists() && new File("/data/sdext4/").canRead()) {
            sdcardpath = "/data/sdext4/";
        }
        if (new File("/data/sdext3/").exists() && new File("/data/sdext3/").canRead()) {
            sdcardpath = "/data/sdext3/";
        }
        if (new File("/data/sdext2/").exists() && new File("/data/sdext2/").canRead()) {
            sdcardpath = "/data/sdext2/";
        }
        if (new File("/data/sdext1/").exists() && new File("/data/sdext1/").canRead()) {
            sdcardpath = "/data/sdext1/";
        }
        if (new File("/data/sdext/").exists() && new File("/data/sdext/").canRead()) {
            sdcardpath = "/data/sdext/";
        }

        //MNTS

        if (new File("mnt/sdcard/external_sd/").exists() && new File("mnt/sdcard/external_sd/").canRead()) {
            sdcardpath = "mnt/sdcard/external_sd/";
        }
        if (new File("mnt/extsdcard/").exists() && new File("mnt/extsdcard/").canRead()) {
            sdcardpath = "mnt/extsdcard/";
        }
        if (new File("mnt/external_sd/").exists() && new File("mnt/external_sd/").canRead()) {
            sdcardpath = "mnt/external_sd/";
        }
        if (new File("mnt/emmc/").exists() && new File("mnt/emmc/").canRead()) {
            sdcardpath = "mnt/emmc/";
        }
        if (new File("mnt/sdcard0/").exists() && new File("mnt/sdcard0/").canRead()) {
            sdcardpath = "mnt/sdcard0/";
        }
        if (new File("mnt/sdcard1/").exists() && new File("mnt/sdcard1/").canRead()) {
            sdcardpath = "mnt/sdcard1/";
        }
        if (new File("mnt/sdcard/").exists() && new File("mnt/sdcard/").canRead()) {
            sdcardpath = "mnt/sdcard/";
        }

        //Storages
        if (new File("/storage/removable/sdcard1/").exists() && new File("/storage/removable/sdcard1/").canRead()) {
            sdcardpath = "/storage/removable/sdcard1/";
        }
        if (new File("/storage/external_SD/").exists() && new File("/storage/external_SD/").canRead()) {
            sdcardpath = "/storage/external_SD/";
        }
        if (new File("/storage/ext_sd/").exists() && new File("/storage/ext_sd/").canRead()) {
            sdcardpath = "/storage/ext_sd/";
        }
        if (new File("/storage/sdcard1/").exists() && new File("/storage/sdcard1/").canRead()) {
            sdcardpath = "/storage/sdcard1/";
        }
        if (new File("/storage/sdcard0/").exists() && new File("/storage/sdcard0/").canRead()) {
            sdcardpath = "/storage/sdcard0/";
        }
        if (new File("/storage/sdcard/").exists() && new File("/storage/sdcard/").canRead()) {
            sdcardpath = "/storage/sdcard/";
        }
        if (sdcardpath.contentEquals("")) {
            sdcardpath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return sdcardpath;
    }
}
