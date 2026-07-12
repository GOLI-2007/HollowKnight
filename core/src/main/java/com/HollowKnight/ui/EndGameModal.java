package com.HollowKnight.ui;

import com.HollowKnight.GameAssetManager;
import com.HollowKnight.UiManager;
import com.HollowKnight.audio.AudioManager;
import com.HollowKnight.model.AchievementManager;
import com.HollowKnight.screen.GameScreen;
import com.HollowKnight.screen.MainMenuScreen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class EndGameModal extends Table {
    private Texture bgOverlayTexture;

    public EndGameModal(boolean isWin, final GameScreen currentScreen) {
        super();
        setFillParent(true);
        setTouchable(Touchable.enabled);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0, 0, 0, 0.85f));
        pixmap.fill();
        bgOverlayTexture = new Texture(pixmap);
        pixmap.dispose();
        setBackground(new TextureRegionDrawable(new TextureRegion(bgOverlayTexture)));

        Label.LabelStyle titleStyle = new Label.LabelStyle(GameAssetManager.trajanFont, isWin ? Color.GOLD : Color.RED);
        Label.LabelStyle infoStyle = new Label.LabelStyle(GameAssetManager.trajanFont, Color.WHITE);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = GameAssetManager.trajanFont;
        btnStyle.fontColor = Color.LIGHT_GRAY;
        btnStyle.overFontColor = Color.WHITE;

        Table contentTable = new Table();
        contentTable.center();

        String titleText = isWin ? "VICTORY ACHIEVED" : "YOU DIED";
        contentTable.add(new Label(titleText, titleStyle)).padBottom(40).row();

        long playTime = (long) AchievementManager.getInstance().getPlayTime();
        long hours = playTime / 3600;
        long minutes = (playTime % 3600) / 60;
        long secs = playTime % 60;
        String timeStr = String.format("%02d:%02d:%02d", hours, minutes, secs);

        int totalKills = AchievementManager.getInstance().getTotalKills();
        int totalDeaths = AchievementManager.getInstance().getDeathCount();

        if (isWin) {
            contentTable.add(new Label("Total Deaths: " + totalDeaths, infoStyle)).padBottom(15).row();
            contentTable.add(new Label("Enemies Defeated: " + totalKills, infoStyle)).padBottom(15).row();
            contentTable.add(new Label("Total Playtime: " + timeStr, infoStyle)).padBottom(40).row();
            AudioManager.getInstance().playWinSound();
        } else {
            AudioManager.getInstance().playFailSound();
        }

        // دکمه‌های منو
        TextButton restartBtn = new TextButton("Restart Game", btnStyle);
        TextButton menuBtn = new TextButton("Main Menu", btnStyle);

        restartBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioManager.getInstance().playConfirmSound();
                UiManager.setScreen(new GameScreen()); // شروع مجدد
            }
        });

        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioManager.getInstance().playConfirmSound();
                UiManager.setScreen(new MainMenuScreen()); // بازگشت به منوی اصلی
            }
        });

        contentTable.add(restartBtn).padBottom(20).row();
        contentTable.add(menuBtn).row();

        add(contentTable);
    }
}
