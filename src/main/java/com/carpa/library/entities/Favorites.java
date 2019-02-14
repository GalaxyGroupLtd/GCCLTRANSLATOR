package com.carpa.library.entities;

import com.carpa.library.utilities.DataFactory;
import com.carpa.library.utilities.MessageNameFactory;
import com.carpa.library.utilities.UtilModel;
import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.Date;

public class Favorites extends SugarRecord implements UtilModel, Serializable {
    private String messageName;
    private String fileName;
    private Date messageDate;
    private String extension;
    private String path;
    private String fileSize;
    private String lastModified;

    public Favorites() {
    }

    public Favorites(String messageName, String fileName, Date messageDate, String extension, String path, String fileSize, String lastModified) {
        this.messageName = messageName;
        this.fileName = fileName;
        this.messageDate = messageDate;
        this.extension = extension;
        this.path = path;
        this.fileSize = fileSize;
        this.lastModified = lastModified;
    }

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public Date getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }

    @Override
    public String toString() {
        return "Favorites{" +
                "messageName='" + messageName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", messageDate=" + DataFactory.formatDate(messageDate) +
                ", extension='" + extension + '\'' +
                ", path='" + path + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", lastModified='" + lastModified + '\'' +
                '}';
    }

    @Override
    public String details() {
        return messageName + "\n" + "Date: " + MessageNameFactory.messageDate(messageName) + "\n" + "Uploaded: " + lastModified;
    }

    @Override
    public String display() {
        return messageName;
    }
}
