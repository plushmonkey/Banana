package com.plushnode.banana.models;

public enum Action {
    BAN("Ban"),
    MUTE("Mute"),
    KICK("Kick"),
    WARN("Warn");

    private String display;

    Action(String display) {
        this.display = display;
    }

    public String toString() {
        return display;
    }
}
