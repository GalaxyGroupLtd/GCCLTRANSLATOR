package com.carpa.library.models;

import java.util.Objects;

public class GroupingModel {
    private String title;
    private String count;

    public GroupingModel() {
    }

    public GroupingModel(String title, String count) {
        this.title = title;
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        GroupingModel that = (GroupingModel) object;
        return getTitle().equals(that.getTitle());
    }

    @Override
    public int hashCode() {
        return getTitle().hashCode();
    }
}
