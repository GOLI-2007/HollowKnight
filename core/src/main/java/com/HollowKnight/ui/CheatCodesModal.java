package com.HollowKnight.ui;

import com.HollowKnight.GameAssetManager;
import com.HollowKnight.Localization;
import com.HollowKnight.UiManager;
import com.HollowKnight.audio.AudioManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

public class CheatCodesModal extends Table {
    private boolean isShowing = false;
    private Texture topFleurTexture;
    private Texture bottomFleurTexture;
    private Texture menuPointerTexture;
    private Texture bgOverlayTexture;

    private PauseModal parentMenu;

    public CheatCodesModal(PauseModal parentMenu) {
        super();
        this.parentMenu = parentMenu;
        setFillParent(true);
        setTouchable(Touchable.enabled);

        Pixmap overlayPix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        overlayPix.setColor(new Color(0, 0, 0, 0.65f));
        overlayPix.fill();
        bgOverlayTexture = new Texture(overlayPix);
        overlayPix.dispose();
        setBackground(new TextureRegionDrawable(new TextureRegion(bgOverlayTexture)));

        topFleurTexture = new Texture(Gdx.files.internal("ui/pause_top_fleur0008.png"));
        bottomFleurTexture = new Texture(Gdx.files.internal("ui/bottom_fleur0007.png"));
        menuPointerTexture = new Texture(Gdx.files.internal("ui/pointer.png"));

        Table menuTable = new Table();
        menuTable.center();

        Localization loc = Localization.getInstance();

        Label.LabelStyle titleStyle = new Label.LabelStyle(GameAssetManager.trajanFont, Color.WHITE);
        Label.LabelStyle textStyle = new Label.LabelStyle(GameAssetManager.trajanFont, new Color(0.85f, 0.85f, 0.85f, 1f));

        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.font = GameAssetManager.trajanFont;
        buttonStyle.fontColor = new Color(0.7f, 0.7f, 0.7f, 1f);
        buttonStyle.overFontColor = Color.WHITE;

        Image topFleur = new Image(topFleurTexture);
        topFleur.setScaling(Scaling.fit);
        menuTable.add(topFleur).width(350).padBottom(20).row();

        Label titleLabel = new Label(loc.get("cheat_codes_title"), titleStyle);
        titleLabel.setFontScale(1.2f);
        titleLabel.setAlignment(Align.center);
        menuTable.add(titleLabel).center().padBottom(25).row();

        Label teleportLabel = new Label(loc.get("cheat_teleport"), textStyle);
        Label noclipLabel = new Label(loc.get("cheat_noclip"), textStyle);
        Label soulLabel = new Label(loc.get("cheat_soul"), textStyle);
        Label godLabel = new Label(loc.get("cheat_god"), textStyle);
        Label emergencyHealLabel = new Label(loc.get("cheat_emergency_heal"), textStyle);
        Label instaKillLabel = new Label(loc.get("cheat_insta_kill"), textStyle);

        teleportLabel.setAlignment(Align.center);
        noclipLabel.setAlignment(Align.center);
        soulLabel.setAlignment(Align.center);
        godLabel.setAlignment(Align.center);
        emergencyHealLabel.setAlignment(Align.center);
        instaKillLabel.setAlignment(Align.center);

        menuTable.add(teleportLabel).padBottom(12).row();
        menuTable.add(noclipLabel).padBottom(12).row();
        menuTable.add(soulLabel).padBottom(12).row();
        menuTable.add(godLabel).padBottom(12).row();
        menuTable.add(emergencyHealLabel).padBottom(12).row();
        menuTable.add(instaKillLabel).padBottom(20).row();

        TextButton backBtn = new TextButton(loc.get("back"), buttonStyle);
        menuTable.add(createMenuRow(backBtn)).padBottom(20).row();

        Image bottomFleur = new Image(bottomFleurTexture);
        bottomFleur.setScaling(Scaling.fit);
        menuTable.add(bottomFleur).width(350).row();

        add(menuTable).center().pad(50);

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
                if (CheatCodesModal.this.parentMenu != null) {
                    CheatCodesModal.this.parentMenu.show();
                }
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
    }
}
