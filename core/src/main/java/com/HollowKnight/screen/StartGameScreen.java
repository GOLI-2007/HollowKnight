package com.HollowKnight.screen;

import com.HollowKnight.GameAssetManager;
import com.HollowKnight.Localization;
import com.HollowKnight.UiManager;
import com.HollowKnight.audio.AudioManager;
import com.HollowKnight.model.GameSave;
import com.HollowKnight.ui.SaveCard;
import com.HollowKnight.utils.SaveManager;
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

public class StartGameScreen extends AbstractScreen {

    private Texture slotBgTexture;
    private Texture menuPointerTexture;
    private Texture pointerTexture;
    private SaveManager saveManager;

    @Override
    public void show() {
        super.show();
        applyMenuBackgroundAndEffects();

        AudioManager.getInstance().playMenuMusic();

        rootTable.clear();
        rootTable.reset();
        rootTable.setFillParent(true);

        slotBgTexture = new Texture(Gdx.files.internal("ui/slot_bg.png"));
        menuPointerTexture = new Texture(Gdx.files.internal("ui/pointer.png"));
        pointerTexture = new Texture(Gdx.files.internal("ui/start_menu_pointer.png"));

        saveManager = new SaveManager();

        rootTable.setFillParent(true);

        Table mainTable = new Table();
        Table titleContainer = new Table();
        titleContainer.center();

        Localization loc = Localization.getInstance();
        Label titleLabel = new Label(loc.get("select_profile"), commonLabelStyle);
        titleLabel.setFontScale(1.3f);
        titleContainer.add(titleLabel).center().row();

        Image pointerImage = new Image(pointerTexture);
        pointerImage.setScaling(Scaling.fit);
        pointerImage.setColor(Color.WHITE);
        titleContainer.add(pointerImage).width(300).height(25).center().padTop(5);

        mainTable.add(titleContainer).center().padBottom(30).row();

        Table slotsTable = new Table();

        for (int i = 1; i <= 4; i++) {
                final int slotIndex = i;
                GameSave save = saveManager.loadSave(slotIndex - 1);
                boolean hasSave = (save != null);

                SaveCard saveCard = new SaveCard(slotIndex, hasSave ? save : null, new SaveCard.SaveCardListener() {
                    @Override
                    public void onPlayClicked(int slot, GameSave clickedSave) {
                        if (clickedSave != null) {
                            UiManager.setScreen(new GameScreen(clickedSave));
                        } else {
                            GameSave newSave = saveManager.createNewSave(slot - 1, "Save " + slot);
                            newSave.setSlotIndex(slot - 1);
                            UiManager.setScreen(new GameScreen(newSave));
                        }
                    }

                    @Override
                    public void onClearClicked(int slot) {
                        saveManager.deleteSave(slot - 1);
                        UiManager.setScreen(new StartGameScreen());
                    }
                });

                slotsTable.add(saveCard).pad(10).row();
            }

        mainTable.add(slotsTable).padBottom(30).row();


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

        rootTable.add(mainTable).expand().fill().center();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (slotBgTexture != null) slotBgTexture.dispose();
        if (menuPointerTexture != null) menuPointerTexture.dispose();
        if (pointerTexture != null) pointerTexture.dispose();
    }
}
