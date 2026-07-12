package com.HollowKnight.screen;

import com.HollowKnight.Localization;
import com.HollowKnight.UiManager;
import com.HollowKnight.audio.AudioManager;
import com.HollowKnight.model.Settings;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import javax.swing.event.ChangeEvent;

public class VideoSettingsScreen extends AbstractScreen {

    private Texture startPointerTexture;
    private Texture menuPointerTexture;
    private Settings settings;

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
        Label titleLabel = new Label(loc.get("video"), commonLabelStyle);
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

        form.add(createSettingRowText(loc.get("resolution"), "1920 x 1080 @ 60HZ")).row();
        form.add(createSettingRowText(loc.get("fullscreen"), "BORDERLESS")).row();
        form.add(createSettingRowText(loc.get("vsync"), "OFF")).row();
        form.add(createSettingRowText(loc.get("particles"), "NORMAL")).row();
        form.add(createSettingRowText(loc.get("blur"), "MEDIUM")).row();
        form.add(createSettingRowText(loc.get("fps_cap"), "ON")).row();

        // ================= ساخت دکمه تغییر تم بک‌گراند =================
        String currentThemeName = settings.getCurrentBgIndex() == 0 ? "DEFAULT" : "THEME " + settings.getCurrentBgIndex();
        TextButton bgCycleBtn = new TextButton(currentThemeName, commonButtonStyle);
        bgCycleBtn.getLabel().setFontScale(0.9f);

        bgCycleBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settings.cycleBackground(); // رفتن به بک‌گراند بعدی
                // ریلود کردن سریع همین صفحه برای اینکه کاربر تغییر رو آنی ببینه
                UiManager.setScreen(new VideoSettingsScreen());
            }
        });

        form.add(createSettingRowActor("BACKGROUND THEME", bgCycleBtn)).row();

        Slider brightnessSlider = new Slider(0.3f, 1.5f, 0.05f, false, skin);
        brightnessSlider.setValue(settings.getBrightness());
        updateBrightness(settings.getBrightness());
        form.add(createSettingRowActor(loc.get("brightness"), brightnessSlider)).row();

        mainTable.add(form).center().padBottom(40).row();

        TextButton resetBtn = new TextButton(loc.get("reset_defaults"), commonButtonStyle);
        resetBtn.getLabel().setFontScale(0.9f);
        mainTable.add(createMenuRow(resetBtn)).padBottom(15).row();

        TextButton backBtn = new TextButton(loc.get("back"), commonButtonStyle);
        backBtn.getLabel().setFontScale(0.9f);
        mainTable.add(createMenuRow(backBtn)).row();

        rootTable.add(mainTable).expand().fill();

        brightnessSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float val = brightnessSlider.getValue();
                settings.setBrightness(val);
                updateBrightness(val);
            }
        });

        resetBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settings.setBrightness(1.0f);
                brightnessSlider.setValue(1.0f);
                updateBrightness(1.0f);
            }
        });

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new SettingsScreen());
            }
        });
    }

    private Table createSettingRowText(String labelText, String valueText) {
        Label valLabel = new Label(valueText, commonLabelStyle);
        valLabel.setFontScale(0.9f);
        valLabel.setAlignment(Align.right);
        return createSettingRowActor(labelText, valLabel);
    }

    private Table createSettingRowActor(String labelText, Actor rightActor) {
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

        rowTable.add(leftIndicator).size(16, 16).padRight(10);
        rowTable.add(label).width(250).left();
        rowTable.add(rightActor).width(250).right();
        rowTable.add(rightIndicator).size(16, 16).padLeft(10);

        rowTable.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                leftIndicator.setVisible(true);
                rightIndicator.setVisible(true);
                label.setColor(Color.WHITE);
                if (rightActor instanceof Label) {
                    ((Label) rightActor).setColor(Color.WHITE);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                leftIndicator.setVisible(false);
                rightIndicator.setVisible(false);
                label.setColor(new Color(0.8f, 0.8f, 0.8f, 1f));
                if (rightActor instanceof Label) {
                    ((Label) rightActor).setColor(new Color(0.8f, 0.8f, 0.8f, 1f));
                }
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
