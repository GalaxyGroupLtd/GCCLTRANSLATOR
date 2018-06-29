package com.carpa.library.entities;

import com.carpa.library.utilities.MessageNameFactory;
import com.carpa.library.utilities.UtilModel;
import com.orm.SugarRecord;

public class Favorites extends SugarRecord implements UtilModel {
    private String messageName;
    private String fileName;
    private String extension;
    private String path;
    private String fileSize;
    private String lastModified;

    public Favorites() {
    }

    public Favorites(String messageName, String fileName, String extension, String path, String fileSize, String lastModified) {
        this.messageName = messageName;
        this.fileName = fileName;
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

    @Override
    public String toString() {
        return "FavoritesFrag{" +
                "messageName='" + messageName + '\'' +
                ", fileName='" + fileName + '\'' +
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
