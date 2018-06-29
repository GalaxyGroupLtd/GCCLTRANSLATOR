/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carpa.library.models;

/**
 * @author ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr /
 * aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662
 */
public class ErrorModel {
    private String code;
    private String title;
    private String description;
    private String userDescr;

    public ErrorModel() {
    }

    public ErrorModel(String code, String title, String description) {
        this.code = code;
        this.title = title;
        this.description = description;
    }

    public ErrorModel(String code, String title, String description, String userDescr) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.userDescr = userDescr;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the userDescr
     */
    public String getUserDescr() {
        return userDescr;
    }

    /**
     * @param userDescr the userDescr to set
     */
    public void setUserDescr(String userDescr) {
        this.userDescr = userDescr;
    }

}
