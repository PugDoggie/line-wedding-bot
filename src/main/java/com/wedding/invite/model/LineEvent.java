package com.wedding.invite.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LineEvent {
    private String replyToken;
    private Message message;
    private Source source;
    private String type;

    public String getReplyToken() { return replyToken; }
    public void setReplyToken(String replyToken) { this.replyToken = replyToken; }

    public Message getMessage() { return message; }
    public void setMessage(Message message) { this.message = message; }

    public Source getSource() { return source; }
    public void setSource(Source source) { this.source = source; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}