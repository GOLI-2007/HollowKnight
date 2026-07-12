package com.HollowKnight.model;

import java.util.ArrayList;
import java.util.List;

public class AchievementManager {
    private static AchievementManager instance;
    private List<Achievement> achievements;
    private List<AchievementListener> listeners;

    private boolean killedMosquito = false;
    private boolean killedCrawlid = false;
    private boolean killedMosscreep = false;
    private boolean killedWingedSentry = false;
    private boolean killedCrystallized = false;
    private boolean killedHuskHornhead = false;
    private float playTimeSeconds = 0;

    private int totalKills = 0;
    private int deathCount = 0;

    private AchievementManager() {
        achievements = new ArrayList<>();
        listeners = new ArrayList<>();

        achievements.add(new Achievement("completion", "Completion", "Finish the game", false, "achievement__0007_stag_quest_complete.png"));
        achievements.add(new Achievement("speedrun", "Speedrun", "Finish in under 1 hour", false, "achievement_fast_finish.png"));
        achievements.add(new Achievement("true_hunter", "True Hunter", "Kill all enemy types", false, "achievement_Hunter_Journal.png"));
        achievements.add(new Achievement("false_knight", "False Knight Slayer", "Defeat the False Knight", false, "achievement_false_knight.png"));
        achievements.add(new Achievement("explorer", "Explorer", "Discover a secret room", false, "achievement_lord_shade.png"));
    }

    public  static AchievementManager getInstance() {
        if (instance == null) {
            instance = new AchievementManager();
        }
        return instance;
    }

    public void addListener(AchievementListener listener) {
        listeners.add(listener);
    }

    public void removeListener(AchievementListener listener) {
        listeners.remove(listener);
    }

    public List<Achievement> getAchievements() { return achievements; }

    public void updatePlayTime(float delta) {
        playTimeSeconds += delta;
    }

    public void registerEnemyKill(String enemyType) {
        totalKills++;

        if (enemyType.equals("Mosquito")) killedMosquito = true;
        if (enemyType.equals("Crawlid")) killedCrawlid = true;
        if (enemyType.equals("Mosscreep")) killedMosscreep = true;
        if (enemyType.equals("Winged Sentry")) killedWingedSentry = true;
        if (enemyType.equals("Crystallized")) killedCrystallized = true;
        if (enemyType.equals("Husk Hornhead")) killedHuskHornhead = true;

        if (killedMosquito && killedCrawlid && killedMosscreep &&
            killedWingedSentry && killedCrystallized && killedHuskHornhead) {
            unlock("true_hunter");
        }
    }


    public void registerBossDefeated() {
        unlock("false_knight");
        unlock("completion");
        if (playTimeSeconds <= 3600) {
            unlock("speedrun");
        }
    }

    public void unlock(String id) {
        for (Achievement a : achievements) {
            if (a.getId().equals(id) && !a.isUnlocked()) {
                a.setUnlocked(true);
                for (AchievementListener listener : listeners) {
                    listener.onAchievementUnlocked(a);
                }
                break;
            }
        }
    }
    public void setPlayTime(float time) {
        this.playTimeSeconds = time;
    }
    public float getPlayTime() {
        return playTimeSeconds;
    }
    public int getTotalKills() {return totalKills;}
    public int getDeathCount() {return deathCount;}
    public void incrementDeathCount() {this.deathCount++;}
}
