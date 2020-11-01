package com.mauricesoa.mauricesensores.Responses;

import com.mauricesoa.mauricesensores.Responses.ClassesNestedJson.EventProdResponse;

public class RegisterEventProdResponse {

    private Boolean success;
    private String env;
    private EventProdResponse event;

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

    public EventProdResponse getEvent() {
        return event;
    }

    public void setEvent(EventProdResponse event) {
        this.event = event;
    }
}
