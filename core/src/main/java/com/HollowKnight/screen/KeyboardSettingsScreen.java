package com.HollowKnight.screen;

import com.HollowKnight.Localization;
import com.HollowKnight.UiManager;
import com.HollowKnight.audio.AudioManager;
import com.HollowKnight.model.Settings;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

public class KeyboardSettingsScreen extends AbstractScreen {

    private Texture startPointerTexture;
    private Texture menuPointerTexture;
    private Settings settings;

    private boolean isWaitingForKey = false;
    private String currentActionBinding = "";
    private Label currentWaitingLabel = null;
    private InputMultiplexer multiplexer;

    private static final String ACTION_UP = "UP";
    private static final String ACTION_DOWN = "DOWN";
    private static final String ACTION_LEFT = "LEFT";
    private static final String ACTION_RIGHT = "RIGHT";
    private static final String ACTION_JUMP = "JUMP";
    private static final String ACTION_ATTACK = "ATTACK";
    private static final String ACTION_DASH = "DASH";

    @Override
    public void show() {
        super.show();
        applyMenuBackgroundAndEffects();

        AudioManager.getInstance().playMenuMusic();

        rootTable.clear();
        rootTable.reset();
        rootTable.setFillParent(true);

        startPointerTexture = new Texture(Gdx.files.internal("ui/start_menu_pointer.png"));
        menuPointerTexture = new Texture(Gdx.files.internal("ui/pointer.png"));
        settings = Settings.getInstance();

        Table mainTable = new Table();
        mainTable.center();
        mainTable.setFillParent(true);

        Table titleContainer = new Table();
        titleContainer.center();
        Localization loc = Localization.getInstance();
        Label titleLabel = new Label(loc.get("keyboard"), commonLabelStyle);
        titleLabel.setFontScale(1.3f);
        titleContainer.add(titleLabel).center().row();

        Image pointerImage = new Image(startPointerTexture);
        pointerImage.setScaling(Scaling.fit);
        pointerImage.setColor(Color.WHITE);
        titleContainer.add(pointerImage).width(300).height(25).center().padTop(2);

        mainTable.add(titleContainer).center().padBottom(30).row();

        Table form = new Table();
        form.center();
        form.defaults().space(15);

        form.add(createKeybindRow(loc.get("up"), ACTION_UP, getKeyName(settings.getKeyMoveUp()))).row();
        form.add(createKeybindRow(loc.get("down"), ACTION_DOWN, getKeyName(settings.getKeyMoveDown()))).row();
        form.add(createKeybindRow(loc.get("left"), ACTION_LEFT, getKeyName(settings.getKeyMoveLeft()))).row();
        form.add(createKeybindRow(loc.get("right"), ACTION_RIGHT, getKeyName(settings.getKeyMoveRight()))).row();
        form.add(createKeybindRow(loc.get("jump"), ACTION_JUMP, getKeyName(settings.getKeyJump()))).row();
        form.add(createKeybindRow(loc.get("attack"), ACTION_ATTACK, getKeyName(settings.getKeyAttack()))).row();
        form.add(createKeybindRow(loc.get("dash"), ACTION_DASH, getKeyName(settings.getKeyDash()))).row();

        mainTable.add(form).center().padBottom(40).row();

        TextButton resetBtn = new TextButton(loc.get("reset_defaults"), commonButtonStyle);
        resetBtn.getLabel().setFontScale(0.9f);
        mainTable.add(createMenuRow(resetBtn)).padBottom(15).row();

        TextButton backBtn = new TextButton(loc.get("back"), commonButtonStyle);
        backBtn.getLabel().setFontScale(0.9f);
        mainTable.add(createMenuRow(backBtn)).row();

        rootTable.add(mainTable).expand().fill();

        InputAdapter keyInputProcessor = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (isWaitingForKey && currentWaitingLabel != null && currentActionBinding != null) {
                    String keyName = Input.Keys.toString(keycode).toUpperCase();

                    switch (currentActionBinding) {
                        case ACTION_UP:    settings.setKeyMoveUp(keycode); break;
                        case ACTION_DOWN:  settings.setKeyMoveDown(keycode); break;
                        case ACTION_LEFT:  settings.setKeyMoveLeft(keycode); break;
                        case ACTION_RIGHT: settings.setKeyMoveRight(keycode); break;
                        case ACTION_JUMP:  settings.setKeyJump(keycode); break;
                        case ACTION_ATTACK: settings.setKeyAttack(keycode); break;
                        case ACTION_DASH:  settings.setKeyDash(keycode); break;
                    }

                    currentWaitingLabel.setText(keyName);
                    isWaitingForKey = false;
                    currentWaitingLabel = null;
                    currentActionBinding = null;
                    return true;
                }
                return false;
            }
        };

        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(keyInputProcessor);
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);

        resetBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settings.resetControls();
                UiManager.setScreen(new KeyboardSettingsScreen());
            }
        });

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isWaitingForKey) {
                    UiManager.setScreen(new SettingsScreen());
                }
            }
        });
    }

    private Table createKeybindRow(final String actionName, final String actionId, String currentKeyName) {
        Table rowTable = new Table();
        rowTable.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);

        final Image leftIndicator = new Image(menuPointerTexture);
        leftIndicator.setScaling(Scaling.fit);
        TextureRegion rightRegion = new TextureRegion(menuPointerTexture);
        rightRegion.flip(true, false);
        final Image rightIndicator = new Image(rightRegion);
        rightIndicator.setScaling(Scaling.fit);

        leftIndicator.setVisible(false);
        rightIndicator.setVisible(false);

        final Label label = new Label(actionName, commonLabelStyle);
        label.setFontScale(0.9f);

        final Label valLabel = new Label(currentKeyName, commonLabelStyle);
        valLabel.setFontScale(0.9f);
        valLabel.setAlignment(Align.right);

        rowTable.add(leftIndicator).size(16, 16).padRight(10);
        rowTable.add(label).width(200).left();
        rowTable.add(valLabel).width(150).right();
        rowTable.add(rightIndicator).size(16, 16).padLeft(10);

        rowTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isWaitingForKey) {
                    isWaitingForKey = true;
                    currentActionBinding = actionId;
                    currentWaitingLabel = valLabel;
                    valLabel.setText("PRESS KEY...");
                }
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!isWaitingForKey || currentWaitingLabel == valLabel) {
                    leftIndicator.setVisible(true);
                    rightIndicator.setVisible(true);
                    label.setColor(Color.WHITE);
                    valLabel.setColor(Color.WHITE);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                leftIndicator.setVisible(false);
                rightIndicator.setVisible(false);
                label.setColor(new Color(0.8f, 0.8f, 0.8f, 1f));
                valLabel.setColor(new Color(0.8f, 0.8f, 0.8f, 1f));
            }
        });
        return rowTable;
    }

    private String getKeyName(int keycode) {
        return Input.Keys.toString(keycode).toUpperCase();
    }

    private Table createMenuRow(final TextButton button) {
        Table rowTable = new Table();
        final Image leftIndicator = new Image(menuPointerTexture);
        leftIndicator.setScaling(Scaling.fit);
        TextureRegion rightRegion = new TextureRegion(menuPointerTexture);
        rightRegion.flip(true, false);
        final Image rightIndicator = new Image(rightRegion);
        rightIndicator.setScaling(Scaling.fit);

        leftIndicator.setVisible(false);
        rightIndicator.setVisible(false);

        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                leftIndicator.setVisible(true);
                rightIndicator.setVisible(true);
                button.getLabel().setColor(Color.WHITE);
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                leftIndicator.setVisible(false);
                rightIndicator.setVisible(false);
                button.getLabel().setColor(new Color(0.8f, 0.8f, 0.8f, 1f));
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
        if (startPointerTexture != null) startPointerTexture.dispose();
        if (menuPointerTexture != null) menuPointerTexture.dispose();
    }
}
