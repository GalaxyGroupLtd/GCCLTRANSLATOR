package com.carpa.library.utilities;

import android.content.Context;
import android.os.AsyncTask;

import com.carpa.library.entities.Messages;
import com.carpa.library.entities.facade.MessagesFacade;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ApplicationInitiator {
    private Context context;
    private OnAppInitiated mListener;
    public static boolean INITIATING = false;

    public ApplicationInitiator(Context context, OnAppInitiated mListener) {
        this.context = context;
        this.mListener = mListener;
    }

    public void start() {
        INITIATING = true;
        new Loader().execute();
    }

    public interface OnAppInitiated {
        void onAppInitiated(boolean isInitiated, String message);

        void onInitiationProgress(int progress, Object extra);
    }

    class Loader extends AsyncTask<String, Integer, Boolean> {
        String errorMessage = "";
        List<File> mFiles = new ArrayList<>();
        int totalTask, totalErrors;

        @Override
        protected Boolean doInBackground(String... param) {
            try {
                mFiles = DirManager.listFiles(true, true, DirManager.getRootSD());
                totalTask = mFiles.size();
                int tTask = totalTask;
                onProgressUpdate(totalTask);
                for (File file : mFiles) {
                    onProgressUpdate(tTask);
                    try {
                        Messages mMessage = MessagesFacade.getFileName(file.getName());
                        if (mMessage == null) {
                            double bytes = file.length();
                            double kilobytes = (bytes / 1024);
                            double megabytes = (kilobytes / 1024);
                            String ext = "";
                            if (file.getName().lastIndexOf(".") != -1 && file.getName().lastIndexOf(".") != 0)
                                ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                            mMessage = new Messages();
                            mMessage.setMessageName(MessageNameFactory.name(file.getName()));
                            mMessage.setMessageDate(DataFactory.formatStringDate(MessageNameFactory.messageDate(file.getName())));
                            mMessage.setFileName(file.getName());
                            mMessage.setExtension(ext);
                            mMessage.setPath(file.getAbsolutePath());
                            mMessage.setFileSize(megabytes + "Mb");
                            mMessage.setLastModified(DataFactory.formatDate(new Date(file.lastModified())));
                            mMessage.setFavorite(false);
                            mMessage.setDownload(false);
                            mMessage.setNew(true);
                            mMessage.setDownloadId(IdGen.NUMBER() + "");
                            mMessage.save();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        totalErrors++;
                        errorMessage += file.getName() + " Failed.\n";
                    }
                    tTask--;
                }
                if (totalTask > totalErrors)
                    return true;
                return false;
            } catch (Exception e) {
                errorMessage = e.getMessage();
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            int tTask = progress[0];
            mListener.onInitiationProgress(totalTask, "Reading " + tTask + " Message file out of " + totalTask + "...");
        }

        @Override
        protected void onPostExecute(Boolean isInitiated) {
            INITIATING = false;
            if (!isInitiated) {
                if (!errorMessage.isEmpty()) {
                    mListener.onAppInitiated(false, errorMessage);
                } else {
                    mListener.onAppInitiated(false, "Initiating the application content encountered difficulties.");
                }
            } else {
                if (!errorMessage.isEmpty()) {
                    mListener.onAppInitiated(true, "Initiating the application content completed successfully with some errors. The following file has abnormalities\n " + errorMessage);
                } else {
                    mListener.onAppInitiated(true, "Initiating the application content completed successfully.");
                }
            }
        }
    }
}
