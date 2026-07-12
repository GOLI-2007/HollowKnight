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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

public class LanguageSettingsScreen extends AbstractScreen {

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
        Label titleLabel = new Label(Localization.getInstance().get("language"), commonLabelStyle);
        titleLabel.setFontScale(1.3f);
        titleContainer.add(titleLabel).center().row();

        Image pointerImage = new Image(startPointerTexture);
        pointerImage.setScaling(Scaling.fit);
        pointerImage.setColor(Color.WHITE);
        titleContainer.add(pointerImage).width(300).height(25).center().padTop(2);

        mainTable.add(titleContainer).center().padBottom(50).row();

        Table form = new Table();
        form.center();

        TextButton langBtn = new TextButton(
            "Current: " + settings.getLanguage(), commonButtonStyle
        );
        langBtn.getLabel().setFontScale(1.0f);
        langBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settings.cycleLanguage();
            }
        });
        form.add(langBtn).padBottom(30).row();

        TextButton backBtn = new TextButton(Localization.getInstance().get("back"), commonButtonStyle);
        backBtn.getLabel().setFontScale(0.9f);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new SettingsScreen());
            }
        });
        form.add(backBtn).width(150).height(40);

        mainTable.add(form).center();

        rootTable.add(mainTable).expand().fill();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (startPointerTexture != null) startPointerTexture.dispose();
        if (menuPointerTexture != null) menuPointerTexture.dispose();
    }
}
