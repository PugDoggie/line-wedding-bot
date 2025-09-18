package com.wedding.invite.model;

public class LineEvent {
    private String replyToken;

    public String getReplyToken() {
        return replyToken;
    }

    public void setReplyToken(String replyToken) {
        this.replyToken = replyToken;
    }

    @Override
    public String toString() {
        return "LineEvent{replyToken='" + replyToken + "'}";
    }
}