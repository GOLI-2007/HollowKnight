package com.HollowKnight.screen;

import com.HollowKnight.Localization;
import com.HollowKnight.UiManager;
import com.HollowKnight.audio.AudioManager;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;

public class MainMenuScreen extends AbstractScreen {

    private Texture titleTexture;
    private Texture cursorTexture;
    private Texture menuPointerTexture;
    private Texture dlcTexture;

    @Override
    public void show() {
        super.show();

        applyMenuBackgroundAndEffects();

        AudioManager.getInstance().playMenuMusic();

        rootTable.clear();
        rootTable.reset();
        rootTable.setFillParent(true);
        rootTable.center();

        titleTexture = new Texture("ui/vheart_title.png");
        cursorTexture = new Texture(Gdx.files.internal("ui/Cursor.png"));
        menuPointerTexture = new Texture(Gdx.files.internal("ui/pointer.png"));
        dlcTexture = new Texture("ui/Logo_mainMenu.png");

        setupCustomCursor();

        TextButtonStyle hollowKnightButtonStyle = new TextButtonStyle();
        hollowKnightButtonStyle.fontColor = new Color(0.7f, 0.7f, 0.7f, 1f);
        hollowKnightButtonStyle.overFontColor = Color.WHITE;

        Image titleImage = new Image(new Texture(Localization.getInstance().getTitleImagePath()));
        titleImage.setScaling(com.badlogic.gdx.utils.Scaling.fit);

        TextButton startBtn = new TextButton(Localization.getInstance().get("start_game"), commonButtonStyle);
        TextButton settingBtn = new TextButton(Localization.getInstance().get("settings"), commonButtonStyle);
        TextButton guideBtn = new TextButton(Localization.getInstance().get("guide"), commonButtonStyle);
        TextButton achievementsBtn = new TextButton(Localization.getInstance().get("achievements"), commonButtonStyle);
        TextButton exitBtn = new TextButton(Localization.getInstance().get("quit"), commonButtonStyle);

        Table menuTable = new Table();
        menuTable.add(createMenuRow(startBtn)).padBottom(15).row();
        menuTable.add(createMenuRow(settingBtn)).padBottom(15).row();
        menuTable.add(createMenuRow(guideBtn)).padBottom(15).row();
        menuTable.add(createMenuRow(achievementsBtn)).padBottom(15).row();
        menuTable.add(createMenuRow(exitBtn));

        com.badlogic.gdx.scenes.scene2d.ui.Stack mainLayoutStack = new com.badlogic.gdx.scenes.scene2d.ui.Stack();

        Table centerTable = new Table();

        centerTable.add(titleImage)
            .width(com.badlogic.gdx.scenes.scene2d.ui.Value.percentWidth(0.5f, rootTable))
            .height(com.badlogic.gdx.scenes.scene2d.ui.Value.percentHeight(0.35f, rootTable))
            .padTop(50).padBottom(20).row();

        centerTable.add(menuTable).padTop(10).row();
        centerTable.add().expandY();

        Table cornerTable = new Table();
        Image dlcIcons = new Image(dlcTexture);
        dlcIcons.setScaling(Scaling.fit);

        cornerTable.add(dlcIcons).expand().left().bottom().padLeft(20).padBottom(60).width(100);

        mainLayoutStack.add(centerTable);
        mainLayoutStack.add(cornerTable);

        rootTable.add(mainLayoutStack).expand().fill();

        startBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new StartGameScreen());
            }
        });

        settingBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new SettingsScreen());
            }
        });

        guideBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new GuideScreen());
            }
        });

        achievementsBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new AchievementsScreen());
            }
        });

        exitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

    private Table createMenuRow(final TextButton button) {
        Table rowTable = new Table();

        final Image leftIndicator = new Image(menuPointerTexture);
        TextureRegion rightRegion = new TextureRegion(menuPointerTexture);
        rightRegion.flip(true, false);
        final Image rightIndicator = new Image(rightRegion);

        leftIndicator.setVisible(false);
        rightIndicator.setVisible(false);
        leftIndicator.setSize(20, 20);
        rightIndicator.setSize(20, 20);

        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                leftIndicator.setVisible(true);
                rightIndicator.setVisible(true);
                leftIndicator.clearActions();
                rightIndicator.clearActions();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                leftIndicator.setVisible(false);
                rightIndicator.setVisible(false);
            }
        });

        rowTable.add(leftIndicator).size(20, 20).padRight(15);
        rowTable.add(button);
        rowTable.add(rightIndicator).size(20, 20).padLeft(15);

        return rowTable;
    }

    private void setupCustomCursor() {
        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            Pixmap pixmap = new Pixmap(Gdx.files.internal("ui/Cursor.png"));
            Cursor customCursor = Gdx.graphics.newCursor(pixmap, 0, 0);
            Gdx.graphics.setCursor(customCursor);
            pixmap.dispose();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (titleTexture != null) titleTexture.dispose();
        if (cursorTexture != null) cursorTexture.dispose();
        if (menuPointerTexture != null) menuPointerTexture.dispose();
        if (dlcTexture != null) dlcTexture.dispose();
    }
}
