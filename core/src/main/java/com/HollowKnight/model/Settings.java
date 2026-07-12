package com.HollowKnight.model;

import com.HollowKnight.Localization;
import com.HollowKnight.UiManager;
import com.badlogic.gdx.Input;

public class Settings {
    private static Settings instance;

    private float musicVolume = 0.3f;
    private float soundVolume = 1.0f;
    private boolean musicOn = true;
    private boolean sfxOn = true;
    private float brightness = 1.0f;
    private String language = "English";

    private int keyMoveUp = Input.Keys.UP;
    private int keyMoveDown = Input.Keys.DOWN;
    private int keyMoveLeft = Input.Keys.LEFT;
    private int keyMoveRight = Input.Keys.RIGHT;

    private int keyJump = Input.Keys.Z;
    private int keyAttack = Input.Keys.X;
    private int keyDash = Input.Keys.C;
    private int keyFocus = Input.Keys.A;
    private int keyInventory = Input.Keys.I;

    private String[] backgrounds = {
        "ui/bg_menu.png",
        "ui/bg_menu1.png",
        "ui/bg_menu2.png",
        "ui/bg_menu3.png",
        "ui/bg_menu4.png"
    };
    private int currentBgIndex = 0;

    private Settings() {}

    public static Settings getInstance(){
        if (instance == null) instance = new Settings();
        return instance;
    }


    public float getBrightness() { return brightness; }
    public void setBrightness(float brightness) { this.brightness = brightness; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public boolean isMusicOn() { return musicOn; }
    public void setMusicOn(boolean musicOn) { this.musicOn = musicOn; }

    public float getMusicVolume() { return musicVolume; }
    public void setMusicVolume(float musicVolume) { this.musicVolume = musicVolume; }

    public float getSoundVolume() {
        return soundVolume;
    }

    public void setSoundVolume(float soundVolume) {
        this.soundVolume = soundVolume;
    }

    public boolean isSfxOn() { return sfxOn; }
    public void setSfxOn(boolean sfxOn) { this.sfxOn = sfxOn; }

    public void cycleLanguage() {
        if ("English".equals(language)) {
            language = "French";
        } else {
            language = "English";
        }
        Localization.getInstance().setLanguage(language);
        UiManager.reloadCurrentScreen();
    }

    public int getKeyMoveUp()     { return keyMoveUp; }
    public int getKeyMoveDown()   { return keyMoveDown; }
    public int getKeyMoveLeft()   { return keyMoveLeft; }
    public int getKeyMoveRight()  { return keyMoveRight; }
    public int getKeyJump()       { return keyJump; }
    public int getKeyDash()       { return keyDash; }
    public int getKeyAttack()     { return keyAttack; }
    public int getKeyInventory()  { return keyInventory; }
    public int getKeyFocus()      { return keyFocus; }

    public void setKeyMoveLeft(int keyMoveLeft) { this.keyMoveLeft = keyMoveLeft; }
    public void setKeyMoveRight(int keyMoveRight) { this.keyMoveRight = keyMoveRight; }
    public void setKeyJump(int keyJump) { this.keyJump = keyJump; }
    public void setKeyDash(int keyDash) { this.keyDash = keyDash; }
    public void setKeyAttack(int keyAttack) { this.keyAttack = keyAttack; }
    public void setKeyFocus(int keyFocus) { this.keyFocus = keyFocus; }
    public void setKeyInventory(int keyInventory) { this.keyInventory = keyInventory; }

    public void setKeyMoveDown(int keyMoveDown) {this.keyMoveDown = keyMoveDown;}
    public static void setInstance(Settings instance) {Settings.instance = instance;}
    public void setKeyMoveUp(int keyMoveUp) {this.keyMoveUp = keyMoveUp;}

    public void resetControls() {
        keyMoveUp = Input.Keys.UP;
        keyMoveDown = Input.Keys.DOWN;
        keyMoveLeft = Input.Keys.LEFT;
        keyMoveRight = Input.Keys.RIGHT;
        keyJump = Input.Keys.Z;
        keyAttack = Input.Keys.X;
        keyDash = Input.Keys.C;
        keyFocus = Input.Keys.A;
    }

    public void resetAll() {
        musicVolume = 0.3f;
        soundVolume = 1.0f;
        musicOn = true;
        sfxOn = true;
        brightness = 1.0f;
        language = "English";
        resetControls();
    }

    public String getCurrentBackgroundPath() {
        return backgrounds[currentBgIndex];
    }

    public int getCurrentBgIndex() {
        return currentBgIndex;
    }

    public void cycleBackground() {
        currentBgIndex++;
        if (currentBgIndex >= backgrounds.length) {
            currentBgIndex = 0;
        }
    }
}
