package com.Obrabotka.IT.models;

public enum Stage {

    OPEN ("Open"),
    ASSIGNED ("Assigned"),
    WORKING ("Working"),
    HOLD ("On hold"),
    WAITING_INPUT ("Pending input"),
    CLOSED ("Closed");

    private final String name;

    Stage(String name) {this.name = name;}

    public String getName() {return name;}

}
