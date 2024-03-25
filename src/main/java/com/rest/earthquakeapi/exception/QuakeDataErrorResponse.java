package com.rest.earthquakeapi.exception;
public class QuakeDataErrorResponse extends RuntimeException{
    private int status;
    private String message;

    private String timeStamp;

    public QuakeDataErrorResponse(){

    }
    public QuakeDataErrorResponse(int status, String message, String timeStamp) {
        this.status = status;
        this.message = message;
        this.timeStamp = timeStamp;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    @Override
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getTimeStamp() {
        return timeStamp;
    }
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
