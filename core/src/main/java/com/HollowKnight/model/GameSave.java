package com.HollowKnight.model;

public class GameSave {
    private String saveName;
    private int progress;
    private String timePlayed = "00:00";
    private String currentScene = "ROOM 1";

    private int slotIndex;

    private int health = 5;
    private int maxHealth = 5;
    private int geo = 0;
    private int currentRoom = 1;
    private float playerX = 100f;
    private float playerY = 150f;
    private float playTimeSeconds = 0f;

    public GameSave() {}

    public GameSave(String saveName, int progress, String timePlayed, String currentScene) {
        this.saveName = saveName;
        this.progress = progress;
        this.timePlayed = timePlayed;
        this.currentScene = currentScene;
    }

    public String getSaveName() { return saveName; }
    public void setSaveName(String saveName) { this.saveName = saveName; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public String getTimePlayed() { return timePlayed; }
    public void setTimePlayed(String timePlayed) { this.timePlayed = timePlayed; }
    public String getCurrentScene() { return currentScene; }
    public void setCurrentScene(String currentScene) { this.currentScene = currentScene; }

    public int getSlotIndex() { return slotIndex; }
    public void setSlotIndex(int slotIndex) { this.slotIndex = slotIndex; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }

    public int getGeo() { return geo; }
    public void setGeo(int geo) { this.geo = geo; }

    public int getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(int currentRoom) { this.currentRoom = currentRoom; }

    public float getPlayerX() { return playerX; }
    public void setPlayerX(float playerX) { this.playerX = playerX; }

    public float getPlayerY() { return playerY; }
    public void setPlayerY(float playerY) { this.playerY = playerY; }

    public float getPlayTimeSeconds() { return playTimeSeconds; }
    public void setPlayTimeSeconds(float playTimeSeconds) { this.playTimeSeconds = playTimeSeconds; }
}
