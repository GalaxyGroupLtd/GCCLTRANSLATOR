package com.carpa.library.entities;

import com.carpa.library.utilities.DataFactory;
import com.carpa.library.utilities.MessageNameFactory;
import com.carpa.library.utilities.UtilAbstractModelORM;
import com.carpa.library.utilities.UtilModel;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Messages extends UtilAbstractModelORM<Messages> implements UtilModel, Serializable {
    private String messageName;
    private String fileName;
    private Date messageDate;
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

    public Messages(String messageName, String fileName, Date messageDate, String extension, String path, String fileSize, String lastModified, boolean isFavorite, String downloadId, boolean isNew, boolean isDownload) {
        super(Messages.class);
        this.messageName = messageName;
        this.fileName = fileName;
        this.messageDate = messageDate;
        this.extension = extension;
        this.path = path;
        this.fileSize = fileSize;
        this.lastModified = lastModified;
        this.isFavorite = isFavorite;
        this.downloadId = downloadId;
        this.isNew = isNew;
        this.isDownload = isDownload;
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
        return "Messages{" +
                "id=" + getId() +
                ", messageName='" + messageName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", messageDate=" + DataFactory.formatDate(messageDate) +
                ", extension='" + extension + '\'' +
                ", path='" + path + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", lastModified='" + lastModified + '\'' +
                ", isFavorite=" + isFavorite +
                ", downloadId='" + downloadId + '\'' +
                ", isNew=" + isNew +
                ", isDownload=" + isDownload +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Messages messages = (Messages) object;

        if (getId() != messages.getId()) return false;
        if (isFavorite() != messages.isFavorite()) return false;
        if (isNew() != messages.isNew()) return false;
        if (isDownload() != messages.isDownload()) return false;
        if (getMessageName() != null ? !getMessageName().equals(messages.getMessageName()) : messages.getMessageName() != null)
            return false;
        if (getFileName() != null ? !getFileName().equals(messages.getFileName()) : messages.getFileName() != null)
            return false;
        if (getMessageDate() != null ? !getMessageDate().equals(messages.getMessageDate()) : messages.getMessageDate() != null)
            return false;
        if (getExtension() != null ? !getExtension().equals(messages.getExtension()) : messages.getExtension() != null)
            return false;
        if (getPath() != null ? !getPath().equals(messages.getPath()) : messages.getPath() != null)
            return false;
        if (getFileSize() != null ? !getFileSize().equals(messages.getFileSize()) : messages.getFileSize() != null)
            return false;
        if (getLastModified() != null ? !getLastModified().equals(messages.getLastModified()) : messages.getLastModified() != null)
            return false;
        return getDownloadId() != null ? getDownloadId().equals(messages.getDownloadId()) : messages.getDownloadId() == null;
    }

    @Override
    public int hashCode() {
        if(getId() == null)
            setId((long) 0);
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (getMessageName() != null ? getMessageName().hashCode() : 0);
        result = 31 * result + (getFileName() != null ? getFileName().hashCode() : 0);
        result = 31 * result + (getMessageDate() != null ? getMessageDate().hashCode() : 0);
        result = 31 * result + (getExtension() != null ? getExtension().hashCode() : 0);
        result = 31 * result + (getPath() != null ? getPath().hashCode() : 0);
        result = 31 * result + (getFileSize() != null ? getFileSize().hashCode() : 0);
        result = 31 * result + (getLastModified() != null ? getLastModified().hashCode() : 0);
        result = 31 * result + (isFavorite() ? 1 : 0);
        result = 31 * result + (getDownloadId() != null ? getDownloadId().hashCode() : 0);
        result = 31 * result + (isNew() ? 1 : 0);
        result = 31 * result + (isDownload() ? 1 : 0);
        return result;
    }

    @Override
    public String details() {
        String display = messageName + "\n";
        try {
            SimpleDateFormat sFormat = new SimpleDateFormat("yyy-MM-dd", Locale.getDefault());
            Date date = sFormat.parse(MessageNameFactory.messageDate(fileName));
            String fDate = sFormat.format(date);

            display += "Date: " + fDate + "\n";
        } catch (ParseException e) {
            e.printStackTrace();
            display += "Date: " + messageName + "\n";
        }
        if (extension != null) {
            display += "File type: ";
            display += (extension.equals(".mp3") ||
                    extension.equals(".aac") ||
                    extension.equals(".aac+") ||
                    extension.equals(".avi") ||
                    extension.equals(".flac") ||
                    extension.equals(".mp2") ||
                    extension.equals(".mp4") ||
                    extension.equals(".ogg") ||
                    extension.equals(".3gp"))  ? "Audio\n" : "Book\n";
        }
        display += "Uploaded: " + lastModified;
        return display;
    }

    @Override
    public String display() {
        return messageName;
    }
}
