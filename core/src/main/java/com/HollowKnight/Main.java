package com.HollowKnight;

import com.HollowKnight.audio.AudioManager;
import com.HollowKnight.screen.MainMenuScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends Game {
    @Override
    public void create() {
        GameAssetManager.init();
        UiManager.init(this);
        Localization.getInstance().setLanguage("English");
        AudioManager.getInstance();
        setScreen(new MainMenuScreen());
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        GameAssetManager.dispose();
        AudioManager.getInstance().dispose();
    }
}
