package com.carpa.library.entities;

import com.carpa.library.utilities.MessageNameFactory;
import com.carpa.library.utilities.UtilAbstractModelORM;
import com.carpa.library.utilities.UtilModel;

public class Messages extends UtilAbstractModelORM<Messages> implements UtilModel {
    private long id;
    private String messageName;
    private String fileName;
    private String extension;
    private String path;
    private String fileSize;
    private String lastModified;
    private boolean isFavorite;
    private String downloadId;
    private boolean isNew;
    private boolean isDownload;

    public Messages() {
        super(Messages.class);
    }

    public Messages(long id, String messageName, String fileName, String extension, String path, String fileSize, String lastModified) {
        super(Messages.class);
        this.id = id;
        this.messageName = messageName;
        this.fileName = fileName;
        this.extension = extension;
        this.path = path;
        this.fileSize = fileSize;
        this.lastModified = lastModified;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    public void setId(long id) {
        this.id = id;
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
        return "Messages{" +
                "id=" + id +
                ", messageName='" + messageName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", extension='" + extension + '\'' +
                ", path='" + path + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", lastModified='" + lastModified + '\'' +
                '}';
    }

    @Override
    public String details() {
        String display = messageName + "\n" + "File type: ";
        if (extension != null) {
            display += extension.equalsIgnoreCase("mp3") ? "Audio\n" : "Book\n";
        }
        display += "Date: " + MessageNameFactory.messageDate(messageName) + "\n" + "Uploaded: " + lastModified;
        return display;
    }

    @Override
    public String display() {
        return messageName;
    }
}
