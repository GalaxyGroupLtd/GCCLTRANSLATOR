package com.carpa.library.utilities.loader;

import android.os.AsyncTask;

import com.carpa.library.entities.Messages;
import com.carpa.library.entities.facade.MessagesFacade;
import com.carpa.library.utilities.DirManager;

import java.util.ArrayList;
import java.util.List;

public class LocalMessageLoader {
    private OnLocalMessagesLoader mListener;

    public LocalMessageLoader(OnLocalMessagesLoader mListener) {
        this.mListener = mListener;
    }

    public void load() {
        new Loader().execute("");
    }

    public void loadAll() {
        new Loader().execute("*");
    }

    public void loadUnavailable() {
        new Loader().execute("un");
    }

    public void loadFavorites() {
        new Loader().execute("fav");
    }

    public void loadNew() {
        new Loader().execute("new");
    }

    public void loadDownloads() {
        new Loader().execute("download");
    }

    public interface OnLocalMessagesLoader {
        void onLocalMessages(boolean isLoaded, String message, List<Messages> messages);
    }

    class Loader extends AsyncTask<String, Integer, List<Messages>> {
        String errorMessage = null;
        List<Messages> mMessages = new ArrayList<>();

        @Override
        protected List<Messages> doInBackground(String... param) {
            try {
                if (param[0].equals("*")) {
                    for (Messages message : MessagesFacade.getMessages()) {
                        if (DirManager.isFileExist(message.getFileName()))
                            mMessages.add(message);
                    }
                    return mMessages;
                } else if (param[0].equals("fav")) {
                    for (Messages message : MessagesFacade.getFavorites()) {
                        List<Messages> availableMessage = MessagesFacade.getMessagePerName(message.getMessageName());
                        if (!availableMessage.isEmpty()) {
                            for (Messages avMessages : availableMessage) {
                                if (DirManager.isFileExist(avMessages.getFileName())) {
                                    mMessages.add(message);
                                    break;
                                }
                            }
                        }
                    }
                    return mMessages;
                } else if (param[0].equals("new")) {
                    for (Messages message : MessagesFacade.getNewMessages()) {
                        List<Messages> availableMessage = MessagesFacade.getMessagePerName(message.getMessageName());
                        if (!availableMessage.isEmpty()) {
                            for (Messages avMessages : availableMessage) {
                                if (DirManager.isFileExist(avMessages.getFileName())) {
                                    mMessages.add(message);
                                    break;
                                }
                            }
                        }
                    }
                    return mMessages;
                } else if (param[0].equals("un")) {
                    for (Messages message : MessagesFacade.getMessages()) {
                        if (!DirManager.isFileExist(message.getFileName())) {
                            mMessages.add(message);
                            break;
                        }
                    }
                    return mMessages;
                } else if (param[0].equals("download")) {
                    for (Messages message : MessagesFacade.getGroupDownloadMessages()) {
                        List<Messages> availableMessage = MessagesFacade.getMessagePerName(message.getMessageName());
                        if (!availableMessage.isEmpty()) {
                            for (Messages avMessages : availableMessage) {
                                if (DirManager.isFileExist(avMessages.getFileName())) {
                                    mMessages.add(message);
                                    break;
                                }
                            }
                        }
                    }
                    return mMessages;
                } else {
                    for (Messages message : MessagesFacade.getGroupMessages()) {
                        List<Messages> availableMessage = MessagesFacade.getMessagePerName(message.getMessageName());
                        if (!availableMessage.isEmpty()) {
                            for (Messages avMessages : availableMessage) {
                                if (DirManager.isFileExist(avMessages.getFileName())) {
                                    mMessages.add(message);
                                    break;
                                }
                            }
                        }
                    }
                    return mMessages;
                }
            } catch (Exception e) {
                errorMessage = e.getMessage();
                return mMessages;
            }
        }

        @Override
        protected void onPostExecute(List<Messages> mMessages) {
            if (errorMessage != null) {
                mListener.onLocalMessages(false, errorMessage, mMessages);
            } else {
                mListener.onLocalMessages(true, null, mMessages);
            }
        }
    }
}
