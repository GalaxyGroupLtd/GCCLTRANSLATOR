package com.carpa.library.models;

import com.carpa.library.utilities.DataFactory;
import com.carpa.library.utilities.UtilModel;

import java.io.Serializable;

public class MyLanguageModel implements Serializable, UtilModel {
    private String name;
    private String lanCode;
    private String messages;

    public MyLanguageModel() {
    }

    public MyLanguageModel(String name, String lanCode, String messages) {
        this.name = name;
        this.lanCode = lanCode;
        this.messages = messages;
    }

    @Override
    public String details() {
        return getName();
    }

    @Override
    public String display() {
        return getName();
    }
    public String lanName(){
        String[] disp = DataFactory.splitString(getName(), ".");
        if(disp.length == 1)
            return getName();
        else
            return disp[0];
    }

    @Override
    public String toString() {
        return "MyLanguageModel{" +
                "name='" + name + '\'' +
                ", lanCode='" + lanCode + '\'' +
                ", messages='" + messages + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanCode() {
        return lanCode;
    }

    public void setLanCode(String lanCode) {
        this.lanCode = lanCode;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }
}
