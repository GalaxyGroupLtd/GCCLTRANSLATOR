package com.carpa.library.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.carpa.library.config.CmdConfig;
import com.carpa.library.entities.Languages;
import com.carpa.library.entities.Messages;
import com.carpa.library.entities.facade.LanguageFacade;
import com.carpa.library.entities.facade.MessagesFacade;
import com.carpa.library.utilities.ApplicationInitiator;
import com.carpa.library.utilities.DataFactory;
import com.carpa.library.utilities.DeviceIdentity;
import com.carpa.library.utilities.DirManager;
import com.carpa.library.utilities.DownloadUtil;
import com.carpa.library.utilities.MessageCache;
import com.carpa.library.utilities.MessageNameFactory;
import com.carpa.library.utilities.loader.FilterLoader;
import com.carpa.library.utilities.loader.LocalMessageLoader;
import com.downloader.Progress;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class CloudService extends IntentService implements LocalMessageLoader.OnLocalMessagesLoader, FilterLoader.OnFilterLoader {
    public static final String ACTION_SYNC = "com.carpa.library.services.action.SYNC";
    public static final String EXTRA_SYNCYPARAM = "com.carpa.library.services.extra.SYNC_PARAM";
    public static final long PERIOD = 1000 * 60 * 1; //1, 15 MIN

    private List<Languages> languagesList;
    private List<Messages> messages;
    private List<Messages> messagesToBeDownloaded = new ArrayList<>();
    private LocalMessageLoader messageLoader;
    private FilterLoader filterLoader;

    public CloudService() {
        super("CloudService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionSync(Context context, String syncParam) {
        Intent intent = new Intent(context, CloudService.class);
        intent.setAction(ACTION_SYNC);
        intent.putExtra(EXTRA_SYNCYPARAM, syncParam);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SYNC.equals(action)) {
                final String sync = intent.getStringExtra(EXTRA_SYNCYPARAM);
                handleActionSync(sync);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSync(String syncParam) {
        //Get default language and Get all local files
        try {
            languagesList = LanguageFacade.getSetLanguage();
        } catch (Exception e) {
            Log.d("DEFAULT_LAN", "Failed to get default languages");
            e.printStackTrace();
        }
        if (languagesList == null)
            return;

        //Request language content from web core
        loadMessages();
    }

    @Override
    public void onLocalMessages(boolean isLoaded, String error, List<Messages> messages) {
        if (isLoaded && !messages.isEmpty()) {
            for (Messages message : messages) {
                if (!DirManager.isFileExist(message.getFileName())) {
                    messagesToBeDownloaded.add(message);
                    //(Un)Comment for multiple file download or single file download
                    //break;
                }
            }
            //Start download
            startDownload();
        }
    }

    private void loadMessages() {
        if(ApplicationInitiator.INITIATING)
            return;
        for (Languages languages : languagesList) {
            filterLoader = new FilterLoader(CloudService.this, CloudService.this, CmdConfig.GET_LANGUAGE_CONTENT.toString(), DeviceIdentity.getCountryCode(CloudService.this), languages.getLanguageName());
            filterLoader.start();
        }
    }

    private void startDownload() {
        for (Messages message : messagesToBeDownloaded) {
            new RequestDownload().execute(message);
        }

    }

    @Override
    public void onFilterLoader(boolean isLoaded, Object response) {
        if (!isLoaded) {
            Log.e("RESP", response.toString());
            return;
        }
        try {
            if(response == null || response.toString() == null){
                Log.d("RESP", "There is no cloud message found for this language");
                return;
            }
            List<Messages> mMessages = new Messages().serializeList(response.toString());
            for (Messages message : mMessages) {
                message.setMessageName(MessageNameFactory.name(message.getFileName()));
                message.setMessageDate(DataFactory.formatStringDate(MessageNameFactory.messageDate(message.getFileName())));
                Messages localMessage = MessagesFacade.getFileName(message.getFileName());
                if (localMessage == null) {
                    message.save();
                }
            }
            if (!MessageCache.isAdding)
                MessageCache.addAll(mMessages);
            //continue with getting all the files in the DB
            messageLoader = new LocalMessageLoader(CloudService.this);
            messageLoader.loadUnavailable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class RequestDownload extends AsyncTask<Messages, Integer, Integer> {
        String errorMessage = null;

        @Override
        protected Integer doInBackground(Messages... param) {
            Messages message = param[0];
            int downloadId = 0;
            try {
                if (DirManager.isFileExist(message.getFileName())) {
                    Messages checkMessage = MessagesFacade.getFileName(message.getFileName());
                    if (checkMessage == null) {
                        message.setMessageName(MessageNameFactory.name(message.getFileName()));
                        message.save();
                    }
                    return 0;
                }
                Log.d("DOWNLOAD", "Downloading: " + message.getPath() + " | " + DirManager.getRoot() + " | " + message.getFileName());
                //String url, String directory, String fileName, OnDownloadUtil mListener
                downloadId = new DownloadUtil(message.getPath(), DirManager.getRoot(), message.getFileName(), new DownloadUtil.OnDownloadUtil() {
                    @Override
                    public void onDownloadSuccess(int downloadId) {
                        Log.d("DOWNLOAD", "#" + downloadId + " Completed");
                    }

                    @Override
                    public void onDownloadCanceled(int downloadId) {
                        Log.d("DOWNLOAD", "#" + downloadId + " Cause: Cancelled");
                    }

                    @Override
                    public void onProgress(Progress progress) {
                    }

                    @Override
                    public void onDownloadFaillure(int downloadId, Object cause) {
                        Log.d("DOWNLOAD", "#" + downloadId + " Cause: " + cause.toString());
                    }
                }).startDownload();
                message.setDownloadId(downloadId + "");
                Log.d("DOWNLOAD", "Download initiated " + message.details());
                long id = message.save();
                if (id < 0) {
                    Log.d("DOWNLOAD_SAVE", "Failed to update local message " + message.details());
                } else {
                    Log.d("DOWNLOAD_SAVE", "Succeeded to update local message " + message.details());
                }
                Thread.sleep(1000 * 14);
            } catch (Exception e) {
                errorMessage = e.getMessage();
                e.printStackTrace();
            }
            return downloadId;
        }

        @Override
        protected void onPostExecute(Integer downloadId) {
            Log.d("DOWNLOAD_POST_EXECUTE", " Download #" + downloadId);
        }
    }

}
