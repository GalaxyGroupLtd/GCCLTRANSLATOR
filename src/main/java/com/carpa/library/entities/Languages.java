package com.carpa.library.entities;

import android.annotation.SuppressLint;

import com.carpa.library.utilities.DataFactory;
import com.carpa.library.utilities.UtilAbstractModelORM;
import com.carpa.library.utilities.UtilModel;

import java.io.Serializable;
import java.util.Objects;

public class Languages extends UtilAbstractModelORM<Languages> implements UtilModel, Serializable {
    boolean isMain;
    private String languageName;
    private String languageUrl;
    private String languageCode;

    public Languages() {
        super(Languages.class);
    }

    public Languages(String languageName, String languageUrl, boolean isMain, String languageCode) {
        super(Languages.class);
        this.languageName = languageName;
        this.languageUrl = languageUrl;
        this.isMain = isMain;
        this.languageCode = languageCode;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getLanguageUrl() {
        return languageUrl;
    }

    public void setLanguageUrl(String languageUrl) {
        this.languageUrl = languageUrl;
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    @SuppressLint("NewApi")
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.languageName);
        hash = 29 * hash + Objects.hashCode(this.languageUrl);
        return hash;
    }

    @SuppressLint("NewApi")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Languages other = (Languages) obj;
        if (!Objects.equals(this.languageName, other.languageName)) {
            return false;
        }
        if (!Objects.equals(this.languageUrl, other.languageUrl)) {
            return false;
        }
        return true;
    }
    @Override
    public String toString() {
        return "Languages{" +
                "languageName='" + languageName + '\'' +
                ", languageUrl='" + languageUrl + '\'' +
                ", isMain=" + isMain +
                '}';
    }

    @Override
    public String details() {
        String[] dispName = DataFactory.splitString(languageName, ".");
        String lan = "";
        if (dispName.length >= 3) {
            lan = dispName[1];
        } else {
            lan = languageName;
        }
        return "Language: " + display() + "\n" + "URL: " + languageUrl + "\n";
    }

    @Override
    public String display() {
        String[] dispName = DataFactory.splitString(languageName, ".");
        if (dispName.length >= 3) {
            return dispName[0];
        }
        return languageName;
    }
}
