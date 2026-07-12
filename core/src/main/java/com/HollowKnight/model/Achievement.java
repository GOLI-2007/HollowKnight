package com.HollowKnight.model;

public class Achievement {
    private String id;
    private String name;
    private String description;
    private boolean unlocked;
    private String iconFileName;

    public Achievement(String id, String name, String description, boolean unlocked, String iconFileName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.unlocked = unlocked;
        this.iconFileName = iconFileName;
    }
    public String getIconFileName() {return iconFileName;}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }
}
