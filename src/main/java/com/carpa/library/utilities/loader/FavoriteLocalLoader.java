package com.carpa.library.utilities.loader;

import android.os.AsyncTask;

import com.carpa.library.entities.Messages;
import com.carpa.library.entities.facade.MessagesFacade;
import com.carpa.library.utilities.MessageCache;

import java.util.ArrayList;
import java.util.List;

public class FavoriteLocalLoader {
    private OnFavoriteLocalLoader mListener;
    private Messages mFavorite;

    public FavoriteLocalLoader(Messages mFavorite, OnFavoriteLocalLoader mListener) {
        this.mFavorite = mFavorite;
        this.mListener = mListener;
    }

    public void addFavorite() {
        new Loader().execute("add_favorite");
    }

    public void removeFavorite() {
        new Loader().execute("remove_favorite");
    }

    public interface OnFavoriteLocalLoader {
        void onFavoriteMessages(boolean isLoaded, String message, List<Messages> messages);
    }

    class Loader extends AsyncTask<String, Integer, List<Messages>> {
        String errorMessage = null;
        List<Messages> mMessages = new ArrayList<>();

        @Override
        protected List<Messages> doInBackground(String... param) {
            try {
                String action = param[0];
                List<Messages> mFavorites = MessagesFacade.getMessagePerName(mFavorite.getMessageName());
                boolean isErrorProduced = false;
                for (Messages message : mFavorites) {
                    message.setFavorite(action.equals("add_favorite"));
                    long id = message.save();
                    if (id <= 0) {
                        isErrorProduced = true;
                    } else {
                        mMessages.add(message);
                    }
                }
                if (!MessageCache.isAdding)
                    MessageCache.addAll(mMessages);
                if (isErrorProduced) {
                    errorMessage = "Sorry, we couldn't " + (action.equals("add_favorite") ? "add" : "remove") + " favorite";
                    return mMessages;
                }
                return mMessages;
            } catch (Exception e) {
                errorMessage = e.getMessage();
                return mMessages;
            }
        }

        @Override
        protected void onPostExecute(List<Messages> mMessages) {
            if (errorMessage != null) {
                mListener.onFavoriteMessages(false, errorMessage, mMessages);
            } else {
                mListener.onFavoriteMessages(true, null, mMessages);
            }
        }
    }
}
