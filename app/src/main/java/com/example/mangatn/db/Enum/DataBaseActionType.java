package com.example.mangatn.db.Enum;

public enum DataBaseActionType {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete");

    private final String name;

    DataBaseActionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
