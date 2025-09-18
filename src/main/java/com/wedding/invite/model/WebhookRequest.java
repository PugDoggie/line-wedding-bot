package com.wedding.invite.model;

import java.util.List;

public class WebhookRequest {
    private List<LineEvent> events;

    public List<LineEvent> getEvents() {
        return events;
    }

    public void setEvents(List<LineEvent> events) {
        this.events = events;
    }
}