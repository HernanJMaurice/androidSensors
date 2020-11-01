package com.mauricesoa.mauricesensores.Responses;

import com.mauricesoa.mauricesensores.Responses.ClassesNestedJson.EventTestResponse;

public class RegisterEventTestResponse {
    private Boolean success;
    private String env;
    private EventTestResponse event;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public EventTestResponse getEvent() {
        return event;
    }

    public void setEvent(EventTestResponse event) {
        this.event = event;
    }
}

