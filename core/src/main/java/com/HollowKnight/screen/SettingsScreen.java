package com.HollowKnight.screen;

import com.HollowKnight.Localization;
import com.HollowKnight.UiManager;
import com.HollowKnight.audio.AudioManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

public class SettingsScreen extends AbstractScreen {

    private Texture menuPointerTexture;
    private Texture startPointerTexture;

    @Override
    public void show() {
        super.show();
        applyMenuBackgroundAndEffects();

        AudioManager.getInstance().playMenuMusic();

        rootTable.clear();
        rootTable.reset();
        rootTable.setFillParent(true);

        menuPointerTexture = new Texture(Gdx.files.internal("ui/pointer.png"));
        startPointerTexture = new Texture(Gdx.files.internal("ui/start_menu_pointer.png"));

        Table mainTable = new Table();
        mainTable.center();
        mainTable.setFillParent(true);

        Table titleContainer = new Table();
        titleContainer.center();

        Localization loc = Localization.getInstance();
        Label titleLabel = new Label(loc.get("settings"), commonLabelStyle);
        titleLabel.setFontScale(1.3f);
        titleContainer.add(titleLabel).center().row();

        Image pointerImage = new Image(startPointerTexture);
        pointerImage.setScaling(Scaling.fit);
        pointerImage.setColor(Color.WHITE);
        titleContainer.add(pointerImage).width(300).height(25).center().padTop(2);
        mainTable.add(titleContainer).center().padBottom(30).row();

        TextButton audioBtn = new TextButton(loc.get("audio"), commonButtonStyle);
        audioBtn.getLabel().setFontScale(0.8f);
        mainTable.add(createMenuRow(audioBtn)).padBottom(10).row();

        TextButton videoBtn = new TextButton(loc.get("video"), commonButtonStyle);
        videoBtn.getLabel().setFontScale(0.8f);
        mainTable.add(createMenuRow(videoBtn)).padBottom(10).row();

        TextButton keyboardBtn = new TextButton(loc.get("keyboard"), commonButtonStyle);
        keyboardBtn.getLabel().setFontScale(0.8f);
        mainTable.add(createMenuRow(keyboardBtn)).padBottom(10).row();

        TextButton languageBtn = new TextButton(loc.get("language"), commonButtonStyle);
        languageBtn.getLabel().setFontScale(0.8f);
        mainTable.add(createMenuRow(languageBtn)).padBottom(10).row();

        TextButton backBtn = new TextButton(loc.get("back"), commonButtonStyle);
        backBtn.getLabel().setFontScale(0.8f);
        backBtn.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                backBtn.getLabel().setColor(Color.WHITE);
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                backBtn.getLabel().setColor(new Color(0.7f, 0.7f, 0.7f, 1f));
            }
        });
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new MainMenuScreen());
            }
        });
        mainTable.add(backBtn).padTop(20).width(150).height(40).center().row();

        rootTable.add(mainTable).expand().fill();

        audioBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new AudioSettingsScreen());
            }
        });
        videoBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new VideoSettingsScreen());
            }
        });
        keyboardBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new KeyboardSettingsScreen());
            }
        });
        languageBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new LanguageSettingsScreen());
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
        leftIndicator.setScale(0.6f);
        rightIndicator.setScale(0.6f);

        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                leftIndicator.setVisible(true);
                rightIndicator.setVisible(true);
                button.getLabel().setColor(Color.WHITE);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                leftIndicator.setVisible(false);
                rightIndicator.setVisible(false);
                button.getLabel().setColor(new Color(0.7f, 0.7f, 0.7f, 1f));
            }
        });

        rowTable.add(leftIndicator).size(16, 16).padRight(12);
        rowTable.add(button);
        rowTable.add(rightIndicator).size(16, 16).padLeft(12);

        return rowTable;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (menuPointerTexture != null) menuPointerTexture.dispose();
        if (startPointerTexture != null) startPointerTexture.dispose();
    }
}
