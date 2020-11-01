package com.mauricesoa.mauricesensores;

import java.io.Serializable;
import java.sql.Timestamp;

public class EventRegistered implements Serializable {

    private String type_event;
    private String description;
    private Timestamp timestamp;

    public String getType_event() {
        return type_event;
    }

    public void setType_event(String type_event) {
        this.type_event = type_event;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
