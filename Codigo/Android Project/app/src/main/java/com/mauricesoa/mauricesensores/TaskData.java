package com.mauricesoa.mauricesensores;

import java.util.ArrayList;

public class TaskData {

    private ArrayList<EventRegistered> list;
    private String type_event;

    public ArrayList<EventRegistered> getList() {
        return list;
    }

    public void setList(ArrayList<EventRegistered> list) {
        this.list = list;
    }

    public String getType_event() {
        return type_event;
    }

    public void setType_event(String type_event) {
        this.type_event = type_event;
    }
}
