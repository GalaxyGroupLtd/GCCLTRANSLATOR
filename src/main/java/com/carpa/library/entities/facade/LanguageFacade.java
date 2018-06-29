package com.carpa.library.entities.facade;

import com.carpa.library.entities.Languages;
import com.orm.util.NamingHelper;

import java.util.List;

public class LanguageFacade {
    public static List<Languages> getListOfLanguages() {
        try {
            return Languages.listAll(Languages.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isLanguageSet() throws Exception {
        try {
            List<Languages> mMain = Languages.findWithQuery(Languages.class, "SELECT * FROM " + NamingHelper.toSQLName(Languages.class) + " WHERE " +
                            NamingHelper.toSQLNameDefault("isMain") + " = ?  ORDER BY " + NamingHelper.toSQLNameDefault("id") + " DESC LIMIT 1",
                    "1");
            return !mMain.isEmpty() && mMain.get(0) != null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("We couldn't find default configurations.");
        }
    }

    public static Languages getDefaultLanguage() throws Exception {
        try {
            List<Languages> mMain = Languages.findWithQuery(Languages.class, "SELECT * FROM " + NamingHelper.toSQLName(Languages.class) + " WHERE " +
                            NamingHelper.toSQLNameDefault("isMain") + " = ?  ORDER BY " + NamingHelper.toSQLNameDefault("id") + " DESC LIMIT 1",
                    "1");
            if (!mMain.isEmpty())
                return mMain.get(0);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("We couldn't find default configurations.");
        }
    }

    public static Languages findLanguage(String languageName) throws Exception {
        try {
            List<Languages> mMain = Languages.findWithQuery(Languages.class, "SELECT * FROM " + NamingHelper.toSQLName(Languages.class) + " WHERE " +
                            NamingHelper.toSQLNameDefault("languageName") + " = ?  ORDER BY " + NamingHelper.toSQLNameDefault("id") + " DESC LIMIT 1",
                    languageName);
            if (!mMain.isEmpty())
                return mMain.get(0);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("We couldn't find language.");
        }
    }
}
