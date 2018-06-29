package com.carpa.library.utilities;

import android.util.Log;

import com.carpa.library.entities.Messages;
import com.carpa.library.entities.facade.MessagesFacade;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;

public class DownloadUtil implements OnStartOrResumeListener, OnPauseListener, OnCancelListener, OnProgressListener, OnDownloadListener {
    private OnDownloadUtil mListener;
    private int downloadId;
    private String url;
    private String directory;
    private String fileName;

    public DownloadUtil(String url, String directory, String fileName, OnDownloadUtil mListener) {
        this.mListener = mListener;
        this.url = url;
        this.directory = directory;
        this.fileName = fileName;
    }

    public int startDownload() {
        Log.d("DOWNLOAD", "Downloading: " + url + " | " + directory + " | " + fileName);
        url = url.replaceAll(" ", "%20");
        this.downloadId = PRDownloader.download(url, directory, fileName)
                .build()
                .setOnStartOrResumeListener(this)
                .setOnPauseListener(this)
                .setOnCancelListener(this)
                .setOnProgressListener(this)
                .start(this);
        return downloadId;
    }

    @Override
    public void onStartOrResume() {
        Log.e("DOWNLOAD", " URL: " + url + " | Directory: " + directory + " | File name: " + fileName + " Download ID: " + downloadId + " | Download: Resumed");
    }

    @Override
    public void onPause() {
        Log.e("DOWNLOAD", " URL: " + url + " | Directory: " + directory + " | File name: " + fileName + " Download ID: " + downloadId + " | Download: Paused");
    }

    @Override
    public void onCancel() {
        Log.e("DOWNLOAD", " URL: " + url + " | Directory: " + directory + " | File name: " + fileName + " | Download: " + downloadId + " Cancelled");
        mListener.onDownloadCanceled(downloadId);
    }

    @Override
    public void onProgress(Progress progress) {
        Log.e("DOWNLOAD", " URL: " + url + " | Directory: " + directory + " | File name: " + fileName + " Download ID: " + downloadId + " | Progress: " + progress.currentBytes + " / " + progress.totalBytes);
        mListener.onProgress(progress);
    }

    @Override
    public void onDownloadComplete() {
        Log.e("DOWNLOAD", " URL: " + url + " | Directory: " + directory + " | File name: " + fileName + " Download ID: " + downloadId + " | Download: Completed");
        try {
            Messages messages = MessagesFacade.getFileDownloadId(downloadId + "");
            if (messages != null) {
                messages.setNew(true);
                messages.save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mListener.onDownloadSuccess(downloadId);
    }

    @Override
    public void onError(Error error) {
        Log.e("DOWNLOAD", " URL: " + url + " | Directory: " + directory + " | File name: " + fileName + " Download ID: " + downloadId + " | Server error: " + error.isServerError() + " Connection Error: " + error.isConnectionError() + " | " + error.toString());
        mListener.onDownloadFaillure(downloadId, " URL: " + url + " | Directory: " + directory + " | File name: " + fileName + " Download ID: " + downloadId + " | Server error: " + error.isServerError() + " Connection Error: " + error.isConnectionError() + " | " + error.toString());
    }

    public interface OnDownloadUtil {
        void onDownloadSuccess(int downloadId);

        void onDownloadCanceled(int downloadId);

        void onProgress(Progress progress);

        void onDownloadFaillure(int downloadId, Object cause);
    }
}
