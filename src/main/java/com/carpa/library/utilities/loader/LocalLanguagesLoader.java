package com.carpa.library.utilities.loader;

import android.os.AsyncTask;

import com.carpa.library.entities.Languages;
import com.carpa.library.entities.facade.LanguageFacade;

import java.util.ArrayList;
import java.util.List;

public class LocalLanguagesLoader {
    private OnLocalLanguagesLoader mListener;

    public LocalLanguagesLoader(OnLocalLanguagesLoader mListener) {
        this.mListener = mListener;
    }

    public void load() {
        new Loader().execute("");
    }

    public interface OnLocalLanguagesLoader {
        void onLocalLanguages(boolean isLoaded, String message, List<Languages> languages);
    }

    class Loader extends AsyncTask<String, Integer, List<Languages>> {
        String errorMessage = null;
        List<Languages> mLanguages = new ArrayList<>();

        @Override
        protected List<Languages> doInBackground(String... param) {
            try {
                return LanguageFacade.getListOfLanguages();
            } catch (Exception e) {
                errorMessage = e.getMessage();
                return mLanguages;
            }
        }

        @Override
        protected void onPostExecute(List<Languages> mMessages) {
            if (errorMessage != null) {
                mListener.onLocalLanguages(false, errorMessage, mMessages);
            } else {
                mListener.onLocalLanguages(true, null, mMessages);
            }
        }
    }
}
