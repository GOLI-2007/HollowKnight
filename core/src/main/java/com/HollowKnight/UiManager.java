package com.HollowKnight;

import com.HollowKnight.screen.AbstractScreen;
import com.badlogic.gdx.Screen;

public class UiManager {
    private static Main main;

    public static void init(Main main) {
        UiManager.main = main;
    }

    public static void setScreen(Screen screen) {
        main.setScreen(screen);
    }

    public static AbstractScreen getScreen() {
        if (main.getScreen() instanceof AbstractScreen) {
            return (AbstractScreen) main.getScreen();
        }
        return null;
    }

    public static void reloadCurrentScreen() {
        Screen screen = main.getScreen();
        if (screen instanceof AbstractScreen) {
            ((AbstractScreen) screen).show();
        }
    }
}
