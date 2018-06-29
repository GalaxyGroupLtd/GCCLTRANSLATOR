package com.carpa.library.utilities;

import com.carpa.library.entities.Messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CloudMessageCache {
    private static ConcurrentHashMap<String, List<Messages>> cloudCache = new ConcurrentHashMap<>();

    public static void add(String languageName, List<Messages> mMessages) {
        cloudCache.put(languageName, mMessages);
    }

    public static List<Messages> getMessages(String languageName) {
        List<Messages> mMessages = new ArrayList<>();
        if (!cloudCache.containsKey(languageName))
            return mMessages;
        return cloudCache.get(languageName);
    }

    public static List<Messages> getAll() {
        List<Messages> mMessages = new ArrayList<>();
        if (cloudCache.isEmpty())
            return mMessages;
        for (Map.Entry entry : cloudCache.entrySet()) {
            mMessages.add((Messages) entry.getValue());
        }
        return mMessages;
    }
}
