package com.carpa.library.models;

public class ApiResponse {
    private int status;
    private Object received;

    public ApiResponse() {
    }

    public ApiResponse(int status, Object received) {
        this.setStatus(status);
        this.setReceived(received);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getReceived() {
        return received;
    }

    public void setReceived(Object received) {
        this.received = received;
    }
}
