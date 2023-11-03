package com.nvt.iot.model;


public class Greeting {
    private String content;
    private String senderName;

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Greeting() {
    }

    public Greeting(String content, String senderName) {
        this.content = content;
        this.senderName = senderName;
    }

    public Greeting(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
