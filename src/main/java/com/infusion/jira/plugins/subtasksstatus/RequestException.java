//package com.infusion.jira.plugins.subtasksstatus;
//
//import com.atlassian.sal.api.net.ResponseException;
//
///**
// * @author Wojciech Kaczmarek
// */
//public class RequestException extends ResponseException {
//
//    private int statusCode;
//
//    private String statusText;
//
//    private String body;
//
//    public RequestException() {
//        super();
//    }
//
//    public RequestException(int statusCode, String statusText, String body) {
//        this.statusCode = statusCode;
//        this.statusText = statusText;
//        this.body = body;
//    }
//
//    public RequestException(String message, int statusCode, String statusText, String body) {
//        super(message);
//        this.statusCode = statusCode;
//        this.statusText = statusText;
//        this.body = body;
//    }
//
//    public int getStatusCode() {
//        return statusCode;
//    }
//
//    public void setStatusCode(int statusCode) {
//        this.statusCode = statusCode;
//    }
//
//    public String getStatusText() {
//        return statusText;
//    }
//
//    public void setStatusText(String statusText) {
//        this.statusText = statusText;
//    }
//
//    public String getBody() {
//        return body;
//    }
//
//    public void setBody(String body) {
//        this.body = body;
//    }
//}
