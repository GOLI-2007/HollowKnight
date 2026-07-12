package com.HollowKnight.screen;

import com.HollowKnight.GameAssetManager;
import com.HollowKnight.audio.AudioManager;
import com.HollowKnight.model.AchievementListener;
import com.HollowKnight.model.AchievementManager;
import com.HollowKnight.model.Settings;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public abstract class AbstractScreen implements Screen, AchievementListener {
    protected Stage stage;
    protected Table rootTable;
    private Stack mainStack;
    private Stack modalStack;
    private Stack toastStack;
    protected Skin skin;
    protected TextButton.TextButtonStyle commonButtonStyle;
    protected Label.LabelStyle commonLabelStyle;

    protected Stack backgroundEffectsStack;
    protected Texture globalBgTexture;
    protected Texture globalRaysTexture;
    protected Texture globalBeamTexture;

    private Image brightnessImage;
    private Texture brightnessTexture;

    public AbstractScreen() {
        commonButtonStyle = new TextButton.TextButtonStyle();
        commonButtonStyle.font = GameAssetManager.trajanFont;
        commonButtonStyle.fontColor = new Color(0.7f, 0.7f, 0.7f, 1f);
        commonButtonStyle.overFontColor = Color.WHITE;

        commonLabelStyle = new Label.LabelStyle(GameAssetManager.trajanFont, Color.WHITE);
    }

    @Override
    public void show() {
        ExtendViewport viewport = new ExtendViewport(1280, 800);

        stage = new Stage(viewport);
        skin = GameAssetManager.skin;

        mainStack = new Stack();
        mainStack.setFillParent(true);
        modalStack = new Stack();
        toastStack = new Stack();
        rootTable = new Table();
        backgroundEffectsStack = new Stack();

        //brightness
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        brightnessTexture = new Texture(pixmap);
        pixmap.dispose();

        brightnessImage = new Image(brightnessTexture);
        brightnessImage.setFillParent(true);
        brightnessImage.getColor().a = 0f;
        brightnessImage.setTouchable(Touchable.disabled);

        Settings settings = Settings.getInstance();
        updateBrightness(settings.getBrightness());

        mainStack.add(backgroundEffectsStack);
        mainStack.add(rootTable);
        mainStack.add(modalStack);
        mainStack.add(toastStack);
        mainStack.add(brightnessImage);


        stage.addActor(mainStack);
        Gdx.input.setInputProcessor(stage);

        AudioManager.getInstance().playConfirmSound();

        stage.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Actor target = event.getTarget();
                Actor button = target;

                while (button != null && !(button instanceof TextButton || button instanceof ImageButton)) {
                    button = button.getParent();
                }

                if (button != null) {
                    if (fromActor == null || !fromActor.isDescendantOf(button)) {
                        AudioManager.getInstance().playClick();
                    }
                }
            }
        });

        AchievementManager.getInstance().addListener(this);
    }

    protected void applyMenuBackgroundAndEffects() {
        String bgPath = Settings.getInstance().getCurrentBackgroundPath();
        globalBgTexture = new Texture(bgPath);
        Image bgImage = new Image(globalBgTexture);
        bgImage.setFillParent(true);
        backgroundEffectsStack.add(bgImage);

        globalBeamTexture = new Texture(Gdx.files.internal("ui/Main_Menu_GG_beam.png"));
        globalRaysTexture = new Texture(Gdx.files.internal("ui/gg_menu_radiant_0000_1.png"));

        // پرتوهای عمودی
        Stack beamsStack = new Stack();
        for (int i = 0; i < 12; i++) {
            Image beam = new Image(globalBeamTexture);
            beam.setOrigin(Align.center);

            float scaleX = MathUtils.random(0.4f, 1.5f);
            float scaleY = MathUtils.random(1.5f, 3.5f);
            beam.setScale(scaleX, scaleY);

            beam.getColor().a = MathUtils.random(0.0f, 0.15f);
            float duration = MathUtils.random(2.5f, 6.0f);
            beam.addAction(Actions.forever(Actions.sequence(
                Actions.alpha(MathUtils.random(0.2f, 0.45f), duration),
                Actions.alpha(MathUtils.random(0.0f, 0.1f), duration)
            )));

            Container<Image> beamContainer = new Container<>(beam);
            beamContainer.center();
            float offsetX = MathUtils.random(-450f, 450f);
            beamContainer.padLeft(offsetX);
            beamsStack.add(beamContainer);
        }
        backgroundEffectsStack.add(beamsStack);

        // ذرات نوری (Rays)
        Stack raysInnerStack = new Stack();
        for (int i = 0; i < 35; i++) {
            Image smallRay = new Image(globalRaysTexture);
            smallRay.setOrigin(Align.center);
            smallRay.getColor().a = 0f;

            smallRay.addAction(Actions.fadeIn(MathUtils.random(1.5f, 4.5f)));

            float scale = MathUtils.random(0.015f, 0.07f);
            smallRay.setScale(scale);
            smallRay.setRotation(MathUtils.random(0f, 360f));

            float speed = (i % 2 == 0 ? 3.0f : -3.0f) + MathUtils.random(-1.5f, 1.5f);
            float driftX = MathUtils.random(-25f, 25f);
            float driftY = MathUtils.random(-25f, 25f);
            float floatDuration = MathUtils.random(4.0f, 10.0f);

            smallRay.addAction(Actions.forever(Actions.parallel(
                Actions.rotateBy(speed, 1f),
                Actions.sequence(
                    Actions.moveBy(driftX, driftY, floatDuration, Interpolation.sineOut),
                    Actions.moveBy(-driftX, -driftY, floatDuration, Interpolation.sineOut)
                )
            )));

            Container<Image> rayContainer = new Container<>(smallRay);
            rayContainer.center();
            float offsetX = MathUtils.random(-650f, 650f);
            float offsetY = MathUtils.random(-350f, 350f);
            rayContainer.padLeft(offsetX);
            rayContainer.padBottom(offsetY);
            raysInnerStack.add(rayContainer);
        }
        backgroundEffectsStack.add(raysInnerStack);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (globalBgTexture != null) globalBgTexture.dispose();
        if (globalBeamTexture != null) globalBeamTexture.dispose();
        if (globalRaysTexture != null) globalRaysTexture.dispose();
        if (brightnessTexture != null) brightnessTexture.dispose();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {
        AchievementManager.getInstance().removeListener(this);
    }

    @Override
    public void onAchievementUnlocked(com.HollowKnight.model.Achievement achievement) {
        Table toastTable = new Table();
        toastTable.bottom().padBottom(50);
        toastTable.setFillParent(true);

        Table contentTable = new Table();
        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(new Color(0.1f, 0.1f, 0.1f, 0.9f));
        bgPixmap.fill();
        contentTable.setBackground(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new Texture(bgPixmap)));
        bgPixmap.dispose();
        contentTable.pad(15);

        Texture iconTex = null;
        try {
            iconTex = new Texture(Gdx.files.internal("ui/Achievements/" + achievement.getIconFileName()));
        } catch (Exception e) {}

        if (iconTex != null) {
            contentTable.add(new Image(iconTex)).size(50, 50).padRight(15);
        }

        Table textTable = new Table();
        Label title = new Label("Achievement Unlocked!", new Label.LabelStyle(GameAssetManager.trajanFont, Color.GOLD));
        title.setFontScale(0.8f);
        Label name = new Label(achievement.getName(), commonLabelStyle);

        textTable.add(title).left().row();
        textTable.add(name).left();
        contentTable.add(textTable);

        toastTable.add(contentTable);

        toastTable.getColor().a = 0f;
        toastTable.addAction(Actions.sequence(
            Actions.fadeIn(0.5f),
            Actions.delay(3f),
            Actions.fadeOut(0.5f),
            Actions.removeActor()
        ));

        toastStack.add(toastTable);
        com.HollowKnight.audio.AudioManager.getInstance().playConfirmSound();
    }

    public Stack getModalStack() { return modalStack; }
    public Stack getToastStack() { return toastStack; }
    public Stage getStage() { return stage; }

    public void updateBrightness(float brightness) {
        float alpha = 1.0f - Math.min(brightness, 1.0f);
        brightnessImage.getColor().a = alpha;
    }




}
