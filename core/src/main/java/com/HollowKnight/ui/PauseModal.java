package com.HollowKnight.ui;

import com.HollowKnight.Localization;
import com.HollowKnight.UiManager;
import com.HollowKnight.audio.AudioManager;
import com.HollowKnight.screen.MainMenuScreen;
import com.HollowKnight.screen.SettingsScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

public class PauseModal extends Table {

    private Texture topFleurTexture;
    private Texture bottomFleurTexture;
    private Texture menuPointerTexture;
    private Texture bgOverlayTexture;
    private BitmapFont font;

    private boolean isShowing = false;

    public PauseModal() {
        super();
        setFillParent(true);
        setTouchable(Touchable.enabled);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0, 0, 0, 0.50f));
        pixmap.fill();
        bgOverlayTexture = new Texture(pixmap);
        pixmap.dispose();
        setBackground(new TextureRegionDrawable(new TextureRegion(bgOverlayTexture)));

        topFleurTexture = new Texture(Gdx.files.internal("ui/pause_top_fleur0008.png"));
        bottomFleurTexture = new Texture(Gdx.files.internal("ui/bottom_fleur0007.png"));
        menuPointerTexture = new Texture(Gdx.files.internal("ui/pointer.png"));

        font = new BitmapFont();

        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = new Color(0.7f, 0.7f, 0.7f, 1f);
        buttonStyle.overFontColor = Color.WHITE;

        Table menuTable = new Table();
        menuTable.center();

        Image topFleur = new Image(topFleurTexture);
        topFleur.setScaling(Scaling.fit);
        menuTable.add(topFleur).width(350).padBottom(25).row();

        Localization loc = Localization.getInstance();
        TextButton continueBtn = new TextButton(loc.get("resume"), buttonStyle);
        TextButton optionsBtn = new TextButton(loc.get("options"), buttonStyle);
        TextButton cheatBtn = new TextButton(loc.get("cheat_codes"), buttonStyle);
        TextButton quitBtn = new TextButton(loc.get("quit_to_menu"), buttonStyle);
        TextButton saveAndQuitButton = new TextButton("Save and Quit", buttonStyle);


        menuTable.add(createMenuRow(continueBtn)).padBottom(15).row();
        menuTable.add(createMenuRow(optionsBtn)).padBottom(15).row();
        menuTable.add(createMenuRow(cheatBtn)).padBottom(15).row();
        menuTable.add(createMenuRow(quitBtn)).padBottom(25).row();
        menuTable.add(createMenuRow(saveAndQuitButton)).padBottom(25).row();

        Image bottomFleur = new Image(bottomFleurTexture);
        bottomFleur.setScaling(Scaling.fit);
        menuTable.add(bottomFleur).width(350).row();

        add(menuTable).center();

        continueBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        optionsBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
                UiManager.setScreen(new SettingsScreen());
            }
        });

        cheatBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
                CheatCodesModal cheatModal = new CheatCodesModal(PauseModal.this);
                cheatModal.show();
            }
        });

        quitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
                UiManager.setScreen(new MainMenuScreen());
            }
        });

        saveAndQuitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
                if (UiManager.getScreen() instanceof com.HollowKnight.screen.GameScreen) {
                    ((com.HollowKnight.screen.GameScreen) UiManager.getScreen()).saveCurrentState();
                }
                UiManager.setScreen(new MainMenuScreen());
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

        rowTable.add(leftIndicator).size(20, 20).padRight(15);
        rowTable.add(button);
        rowTable.add(rightIndicator).size(20, 20).padLeft(15);

        return rowTable;
    }

    public void show() {
        if (!isShowing) {
            UiManager.getScreen().getStage().addActor(this);
            isShowing = true;

            AudioManager.getInstance().playConfirmSound();
        }
    }

    public void hide() {
        if (isShowing) {
            this.remove();
            isShowing = false;
        }
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void dispose() {
        if (topFleurTexture != null) topFleurTexture.dispose();
        if (bottomFleurTexture != null) bottomFleurTexture.dispose();
        if (menuPointerTexture != null) menuPointerTexture.dispose();
        if (bgOverlayTexture != null) bgOverlayTexture.dispose();
        if (font != null) font.dispose();
    }
}
