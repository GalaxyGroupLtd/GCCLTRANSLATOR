package com.carpa.library.models;

import java.io.Serializable;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 12/25/2017.
 */

public class GeneralListModel implements Serializable {
    private String title;
    private String logo;
    private boolean isOverFlow;

    public GeneralListModel() {
    }

    public GeneralListModel(String logo, String title, boolean isOverFlow) {
        this.setTitle(title);
        this.setLogo(logo);
        this.setOverFlow(isOverFlow);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public boolean isOverFlow() {
        return isOverFlow;
    }

    public void setOverFlow(boolean overFlow) {
        isOverFlow = overFlow;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof GeneralListModel)) return false;

        GeneralListModel that = (GeneralListModel) object;

        if (isOverFlow() != that.isOverFlow()) return false;
        if (!getTitle().equals(that.getTitle())) return false;
        return getLogo().equals(that.getLogo());

    }

    @Override
    public int hashCode() {
        int result = getTitle().hashCode();
        result = 31 * result + getLogo().hashCode();
        result = 31 * result + (isOverFlow() ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GeneralListModel{" +
                "title='" + title + '\'' +
                ", logo='" + logo + '\'' +
                ", isOverFlow=" + isOverFlow +
                '}';
    }
}
