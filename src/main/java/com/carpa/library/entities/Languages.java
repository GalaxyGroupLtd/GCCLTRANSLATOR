package com.carpa.library.entities;

import com.carpa.library.utilities.DataFactory;
import com.carpa.library.utilities.UtilAbstractModelORM;
import com.carpa.library.utilities.UtilModel;

public class Languages extends UtilAbstractModelORM<Languages> implements UtilModel {
    boolean isMain;
    private String languageName;
    private String languageUrl;

    public Languages() {
        super(Languages.class);
    }

    public Languages(String languageName, String languageUrl, boolean isMain) {
        super(Languages.class);
        this.languageName = languageName;
        this.languageUrl = languageUrl;
        this.isMain = isMain;
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
