package com.carpa.library.entities.facade;

import com.carpa.library.entities.Messages;
import com.carpa.library.models.GroupingModel;
import com.orm.util.NamingHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MessagesFacade {
    public static List<Messages> getMessages() {
        List<Messages> messages = new ArrayList<>();
        try {
            return Messages.listAll(Messages.class);
        } catch (Exception e) {
            e.printStackTrace();
            return messages;
        }
    }

    public static List<Messages> getGroupMessages() throws Exception {
        try {
            return Messages.findWithQuery(Messages.class, "SELECT * FROM " + NamingHelper.toSQLName(Messages.class) + " WHERE " +
                            NamingHelper.toSQLNameDefault("isDownload") + " = ? GROUP BY " + NamingHelper.toSQLNameDefault("messageName") + " ORDER BY " + NamingHelper.toSQLNameDefault("id") + " DESC",
                    "0");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("We couldn't find messages for the moment.");
        }
    }

    public static List<Messages> getGroupDownloadMessages() throws Exception {
        try {
            return Messages.findWithQuery(Messages.class, "SELECT * FROM " + NamingHelper.toSQLName(Messages.class) + " WHERE " +
                            NamingHelper.toSQLNameDefault("isDownload") + " = ? GROUP BY " + NamingHelper.toSQLNameDefault("messageName") + " ORDER BY " + NamingHelper.toSQLNameDefault("id") + " DESC",
                    "1");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("We couldn't find downloaded messages for the moment.");
        }
    }

    public static List<Messages> getFavorites() throws Exception {
        try {
            return Messages.findWithQuery(Messages.class, "SELECT * FROM " + NamingHelper.toSQLName(Messages.class) + " WHERE " +
                            NamingHelper.toSQLNameDefault("isFavorite") + " = ? GROUP BY " + NamingHelper.toSQLNameDefault("messageName") + " ORDER BY " + NamingHelper.toSQLNameDefault("id") + " DESC",
                    "1");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("We couldn't find favorites messages for the moment.");
        }
    }

    public static List<Messages> getNewMessages() throws Exception {
        try {
            return Messages.findWithQuery(Messages.class, "SELECT * FROM " + NamingHelper.toSQLName(Messages.class) + " WHERE " +
                            NamingHelper.toSQLNameDefault("isNew") + " = ? GROUP BY " + NamingHelper.toSQLNameDefault("messageName") + " ORDER BY " + NamingHelper.toSQLNameDefault("id") + " DESC",
                    "1");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("We couldn't find favorites messages for the moment.");
        }
    }

    public static List<Messages> getMessagePerName(String name) throws Exception {
        try {
            return Messages.findWithQuery(Messages.class, "SELECT * FROM " + NamingHelper.toSQLName(Messages.class) + " WHERE " +
                            NamingHelper.toSQLNameDefault("messageName") + " = ?  ORDER BY " + NamingHelper.toSQLNameDefault("id") + " DESC",
                    name);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("We couldn't find messages for the moment.");
        }
    }

    public static Messages getFileName(String fileName) throws Exception {
        try {
            List<Messages> mMessages = Messages.findWithQuery(Messages.class, "SELECT * FROM " + NamingHelper.toSQLName(Messages.class) + " WHERE " +
                            NamingHelper.toSQLNameDefault("fileName") + " = ?  ORDER BY " + NamingHelper.toSQLNameDefault("id") + " DESC LIMIT 1",
                    fileName);
            return mMessages.isEmpty() ? null : mMessages.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("We couldn't find messages for the moment.");
        }
    }

    public static Messages getFileDownloadId(String downloadId) throws Exception {
        try {
            List<Messages> mMessages = Messages.findWithQuery(Messages.class, "SELECT * FROM " + NamingHelper.toSQLName(Messages.class) + " WHERE " +
                            NamingHelper.toSQLNameDefault("downloadId") + " = ?  ORDER BY " + NamingHelper.toSQLNameDefault("id") + " DESC LIMIT 1",
                    downloadId);
            return mMessages.isEmpty() ? null : mMessages.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("We couldn't find messages for the moment.");
        }
    }
}
