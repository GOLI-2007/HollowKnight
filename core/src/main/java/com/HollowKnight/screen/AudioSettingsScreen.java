package com.HollowKnight.screen;

import com.HollowKnight.Localization;
import com.HollowKnight.UiManager;
import com.HollowKnight.audio.AudioManager;
import com.HollowKnight.model.Settings;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

public class AudioSettingsScreen extends AbstractScreen {

    private Texture startPointerTexture;
    private Texture menuPointerTexture;
    private Settings settings;
    private AudioManager audioManager;

    private Label masterValLabel;
    private Label soundValLabel;
    private Label musicValLabel;

    @Override
    public void show() {
        super.show();
        applyMenuBackgroundAndEffects();

        audioManager = AudioManager.getInstance();
        audioManager.playMenuMusic();

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
        Label titleLabel = new Label(loc.get("audio"), commonLabelStyle);
        titleLabel.setFontScale(1.3f);
        titleContainer.add(titleLabel).center().row();

        Image pointerImage = new Image(startPointerTexture);
        pointerImage.setScaling(Scaling.fit);
        pointerImage.setColor(Color.WHITE);
        titleContainer.add(pointerImage).width(300).height(25).center().padTop(2);

        mainTable.add(titleContainer).center().padBottom(40).row();

        // فرم تنظیمات
        Table form = new Table();
        form.center();
        form.defaults().space(20);

        // اسلایدر Master
        Slider masterSlider = new Slider(0f, 10f, 1f, false, skin);
        masterSlider.setValue(10f); // مقدار اولیه
        Table masterRow = createSettingRow(loc.get("master_volume"), masterSlider, "10");
        masterValLabel = (Label) masterRow.getChildren().get(3);
        form.add(masterRow).row();

        // اسلایدر Sound
        Slider soundSlider = new Slider(0f, 10f, 1f, false, skin);
        soundSlider.setValue(settings.isSfxOn() ? settings.getSoundVolume() * 10 : 0f);
        Table soundRow = createSettingRow(loc.get("sound_volume"), soundSlider, String.valueOf((int)soundSlider.getValue()));
        soundValLabel = (Label) soundRow.getChildren().get(3);
        form.add(soundRow).row();

        // اسلایدر Music
        Slider musicSlider = new Slider(0f, 10f, 1f, false, skin);
        musicSlider.setValue(settings.isMusicOn() ? settings.getMusicVolume() * 10 : 0f);
        Table musicRow = createSettingRow(loc.get("music_volume"), musicSlider, String.valueOf((int)musicSlider.getValue()));
        musicValLabel = (Label) musicRow.getChildren().get(3);
        form.add(musicRow).row();

        mainTable.add(form).center().padBottom(50).row();

        Table sfxRow = new Table();
        Label sfxLabel = new Label(loc.get("sfx_toggle"), commonLabelStyle);
        sfxLabel.setFontScale(0.9f);

        TextButton sfxToggleBtn = new TextButton(settings.isSfxOn() ? "ON" : "OFF", commonButtonStyle);
        sfxToggleBtn.getLabel().setFontScale(0.9f);

        sfxRow.add(sfxLabel).width(250).left();
        sfxRow.add(sfxToggleBtn).width(100).center();
        form.add(sfxRow).row();

        sfxToggleBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean newState = !settings.isSfxOn();
                settings.setSfxOn(newState);
                sfxToggleBtn.setText(newState ? "ON" : "OFF");

                soundSlider.setDisabled(!newState);
                if (!newState) {
                    soundSlider.setValue(0);
                    updateLabel(soundValLabel, 0);
                    settings.setSoundVolume(0);
                } else {
                    soundSlider.setValue(10);
                    updateLabel(soundValLabel, 10);
                    settings.setSoundVolume(1.0f);
                }
            }
        });

        soundSlider.setDisabled(!settings.isSfxOn());

        TextButton resetBtn = new TextButton(loc.get("reset_defaults"), commonButtonStyle);
        resetBtn.getLabel().setFontScale(0.9f);
        mainTable.add(createMenuRow(resetBtn)).padBottom(15).row();

        TextButton backBtn = new TextButton(loc.get("back"), commonButtonStyle);
        backBtn.getLabel().setFontScale(0.9f);
        mainTable.add(createMenuRow(backBtn)).row();

        rootTable.add(mainTable).expand().fill();

        masterSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float val = masterSlider.getValue();
                soundSlider.setValue(val);
                musicSlider.setValue(val);
                updateLabel(masterValLabel, val);
                updateLabel(soundValLabel, val);
                updateLabel(musicValLabel, val);
                applySoundVolume(val);
                applyMusicVolume(val);
                audioManager.updateVolume();
            }
        });

        soundSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float val = soundSlider.getValue();
                updateLabel(soundValLabel, val);
                applySoundVolume(val);
                settings.setSfxOn(val > 0);
            }
        });

        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float val = musicSlider.getValue();
                updateLabel(musicValLabel, val);
                applyMusicVolume(val);
                settings.setMusicOn(val > 0);
                audioManager.updateVolume();
            }
        });

        resetBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                masterSlider.setValue(10f);
                soundSlider.setValue(10f);
                musicSlider.setValue(10f);
                updateLabel(masterValLabel, 10f);
                updateLabel(soundValLabel, 10f);
                updateLabel(musicValLabel, 10f);
                settings.setMusicVolume(1.0f);
                settings.setMusicOn(true);
                settings.setSoundVolume(1.0f);
                settings.setSfxOn(true);
                audioManager.updateVolume();
            }
        });

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new SettingsScreen());
            }
        });
    }

    private void updateLabel(Label label, float value) {
        label.setText(String.valueOf((int)value));
    }

    private void applySoundVolume(float val) {
        float volume = val / 10f;
        settings.setSoundVolume(volume);
        if (val > 0) settings.setSfxOn(true);
        else settings.setSfxOn(false);
    }

    private void applyMusicVolume(float val) {
        float volume = val / 10f;
        settings.setMusicVolume(volume);
        if (val > 0) settings.setMusicOn(true);
        else settings.setMusicOn(false);
    }

    private Table createSettingRow(String labelText, Actor controlActor, String initialValue) {
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

        Label label = new Label(labelText, commonLabelStyle);
        label.setFontScale(0.9f);

        Label valLabel = new Label(initialValue, commonLabelStyle);
        valLabel.setFontScale(0.8f);
        valLabel.setAlignment(Align.right);

        rowTable.add(leftIndicator).size(16, 16).padRight(10);
        rowTable.add(label).width(250).left();
        rowTable.add(controlActor).width(150).center().padLeft(20).padRight(20);
        rowTable.add(valLabel).width(30).right();
        rowTable.add(rightIndicator).size(16, 16).padLeft(10);

        rowTable.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                leftIndicator.setVisible(true);
                rightIndicator.setVisible(true);
                label.setColor(Color.WHITE);
                valLabel.setColor(Color.WHITE);
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
