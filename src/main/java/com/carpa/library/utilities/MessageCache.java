package com.carpa.library.utilities;

import com.carpa.library.entities.Messages;
import com.carpa.library.models.GroupingModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageCache {
    private static ConcurrentHashMap<String, List<Messages>> mCache = new ConcurrentHashMap<>();
    public static boolean isAdding = false;

    public static void addAll(List<Messages> mMessages) {
        if (!isAdding)
            isAdding = true;
        else
            return;
        for (Messages message : mMessages) {
            if (mCache.containsKey(message.getMessageName())) {
                //add to the list this new message
                List<Messages> content = new ArrayList<>(mCache.get(message.getMessageName()));
                try {
                    for (Messages mContent : content) {
                        if (!mContent.getFileName().equals(message.getFileName()))
                            content.add(message);
                    }
                    mCache.put(message.getMessageName(), content);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                //create new record
                mCache.put(message.getMessageName(), Collections.singletonList(message));
            }
        }
        isAdding = false;
    }

    public static void add(Messages message) {
        if (mCache.containsKey(message.getMessageName())) {
            //add to the list this new message
            List<Messages> content = mCache.get(message.getMessageName());
            for (Messages messages : content) {
                try {
                    if (!messages.getFileName().equals(message.getFileName()))
                        content.add(message);

                    mCache.put(message.getMessageName(), content);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            //create new record
            mCache.put(message.getMessageName(), Collections.singletonList(message));
        }
    }

    public static List<Messages> getMessageName(String messageName) {
        return mCache.get(messageName);
    }

    public static List<Messages> getGroupedMessages() {
        List<Messages> mMessages = new ArrayList<>();
        for (Map.Entry<String, List<Messages>> entry : mCache.entrySet()) {
            Messages message = entry.getValue().get(0);
            if (!mMessages.contains(message))
                mMessages.add(entry.getValue().get(0));
        }
        return mMessages;
    }

    public static List<Messages> getLanGroupedMessages(String lanCode) {
        List<Messages> mMessages = new ArrayList<>();
        for (Messages message : getGroupedMessages()) {
            if (message.getFileName().startsWith(lanCode))
                mMessages.add(message);
        }
        return mMessages;
    }

    public static HashMap<Integer, List<Messages>> getLanYearGroupedMessages(String lanCode) {
        HashMap<Integer, List<Messages>> messagePerYear = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        for (Messages message : getLanGroupedMessages(lanCode)) {
            calendar.setTime(message.getMessageDate());
            if (message.getFileName().startsWith(lanCode)) {
                try {
                    if (messagePerYear.containsKey(calendar.get(Calendar.YEAR))) {
                        List<Messages> content = new ArrayList<>(messagePerYear.get(calendar.get(Calendar.YEAR)));
                        content.add(message);
                        messagePerYear.put(calendar.get(Calendar.YEAR), content);
                    } else {
                        messagePerYear.put(calendar.get(Calendar.YEAR), Arrays.asList(message));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return messagePerYear;
    }

    public static HashMap<String, List<Messages>> getLanYearMonthGroupedMessages(String lanCode, Integer year) {
        HashMap<Integer, List<Messages>> messagePerYear = getLanYearGroupedMessages(lanCode);
        HashMap<String, List<Messages>> messagePerMonth = new HashMap<>();
        List<Messages> monthsMessage = messagePerYear.get(year);
        Calendar calendar = Calendar.getInstance();
        for (Messages message : monthsMessage) {
            calendar.setTime(message.getMessageDate());
            try {
                String dispName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
                if (messagePerMonth.containsKey(dispName)) {
                    List<Messages> content = new ArrayList<>(messagePerMonth.get(dispName));
                    content.add(message);
                    messagePerMonth.put(dispName, content);
                } else {
                    messagePerMonth.put(dispName, Collections.singletonList(message));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return messagePerMonth;
    }

    public static List<Messages> getDownloaded() {
        List<Messages> mMessages = new ArrayList<>();
        for (Map.Entry<String, List<Messages>> entry : mCache.entrySet()) {
            Messages message = entry.getValue().get(0);
            if (!mMessages.contains(message))
                mMessages.add(entry.getValue().get(0));
        }
        List<Messages> downloaded = new ArrayList<>();
        for (Messages messages : mMessages) {
            List<Messages> content = mCache.get(messages.getMessageName());
            boolean isDownloaded = false;
            Messages mMessage = null;
            for (Messages message : content) {
                if (message.isDownload()) {
                    isDownloaded = true;
                    mMessage = message;
                }
            }
            if (isDownloaded && !downloaded.contains(mMessage)) {
                downloaded.add(mMessage);
            }
        }
        return downloaded;
    }

    public static List<GroupingModel> getGroupedYearMessages() {
        List<Messages> mMessages = getGroupedMessages();
        List<GroupingModel> mGrouping = new ArrayList<>();
        HashMap<String, Integer> yearCount = new HashMap<>();
        for (Messages messages : mMessages) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(messages.getMessageDate());
            String year = String.valueOf(calendar.get(Calendar.YEAR));
            if (yearCount.containsKey(year)) {
                Integer count = yearCount.get(year) + 1;
                yearCount.put(year, count);
            } else {
                yearCount.put(year, 1);
            }
        }

        for (Map.Entry<String, Integer> entry : yearCount.entrySet()) {
            mGrouping.add(new GroupingModel(entry.getKey(), String.valueOf(entry.getValue())));
        }
        return mGrouping;
    }

    public static HashMap<String, List<Messages>> getYearGroupedMessages() {
        List<Messages> mMessages = getGroupedMessages();
        HashMap<String, List<Messages>> yearMessages = new HashMap<>();
        for (Messages messages : mMessages) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(messages.getMessageDate());
            String year = String.valueOf(calendar.get(Calendar.YEAR));
            if (yearMessages.containsKey(year)) {
                List<Messages> currentYearMessage = yearMessages.get(year);
                currentYearMessage.add(messages);
                yearMessages.put(year, currentYearMessage);
            } else {
                yearMessages.put(year, Arrays.asList(messages));
            }
        }
        return yearMessages;
    }

    public static List<GroupingModel> getGroupedMonthMessages(int year) {
        List<Messages> mMessages = getGroupedMessages();
        List<GroupingModel> mGrouping = new ArrayList<>();
        HashMap<String, Integer> monthCount = new HashMap<>();
        for (Messages messages : mMessages) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(messages.getMessageDate());
            if (calendar.get(Calendar.YEAR) == year) {
                String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
                if (monthCount.containsKey(month)) {
                    Integer count = monthCount.get(month) + 1;
                    monthCount.put(month, count);
                } else {
                    monthCount.put(month, 1);
                }
            }
        }

        for (Map.Entry<String, Integer> entry : monthCount.entrySet()) {
            mGrouping.add(new GroupingModel(entry.getKey(), String.valueOf(entry.getValue())));
        }
        return mGrouping;
    }

    public static List<Messages> getFavorites() {
        List<Messages> mMessages = new ArrayList<>();
        for (Messages message : getGroupedMessages()) {
            if (message.isFavorite()) {
                mMessages.add(message);
            }
        }
        return mMessages;
    }

    public static List<Messages> getNew() {
        List<Messages> mMessages = new ArrayList<>();
        for (Messages message : getGroupedMessages()) {
            if (message.isNew()) {
                mMessages.add(message);
            }
        }
        return mMessages;
    }
}
