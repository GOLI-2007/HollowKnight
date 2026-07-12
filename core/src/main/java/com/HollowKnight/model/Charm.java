package com.HollowKnight.model;

public class Charm {

    private String id;
    private String name;
    private String description;
    private int notchCost;
    private boolean equipped;

    public Charm(String id,
                 String name,
                 String description,
                 int notchCost) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.notchCost = notchCost;
        this.equipped = false;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getNotchCost() {
        return notchCost;
    }

    public boolean isEquipped() {
        return equipped;
    }

    public void setEquipped(boolean equipped) {
        this.equipped = equipped;
    }
}
