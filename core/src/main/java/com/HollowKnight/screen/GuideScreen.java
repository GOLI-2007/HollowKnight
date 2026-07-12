package com.HollowKnight.screen;

import com.HollowKnight.GameAssetManager;
import com.HollowKnight.Localization;
import com.HollowKnight.UiManager;
import com.HollowKnight.audio.AudioManager;
import com.HollowKnight.model.Settings;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

public class GuideScreen extends AbstractScreen {

    private Texture startPointerTexture;
    private Texture menuPointerTexture;
    private Settings settings;

    private Texture keyBgTexture;
    private Texture arrowUpTex, arrowDownTex, arrowRightTex, arrowLeftTex;

    private Texture idleSheet, runSheet, jumpSheet, dashSheet, attackSheet, focusSheet;

    private Animation<TextureRegion> idleAnim, runAnim, jumpAnim, dashAnim, attackAnim, focusAnim;

    private AnimatedPreviewCharacter previewCharacter;

    public GuideScreen() {
        super();
    }

    @Override
    public void show() {
        super.show();
        applyMenuBackgroundAndEffects();
        AudioManager.getInstance().playMenuMusic();

        rootTable.clear();
        rootTable.reset();
        rootTable.setFillParent(true);

        settings = Settings.getInstance();

        startPointerTexture = new Texture(Gdx.files.internal("ui/start_menu_pointer.png"));
        menuPointerTexture = new Texture(Gdx.files.internal("ui/pointer.png"));

        keyBgTexture = new Texture(Gdx.files.internal("ui/button_skin_0004_square_key.png"));
        arrowUpTex = new Texture(Gdx.files.internal("ui/button_skin_0004_square_arrow_u.png"));
        arrowDownTex = new Texture(Gdx.files.internal("ui/button_skin_0004_square_arrow_d.png"));
        arrowRightTex = new Texture(Gdx.files.internal("ui/button_skin_0004_square_arrow_r.png"));
        arrowLeftTex = new Texture(Gdx.files.internal("ui/button_skin_0004_square_arrow.png"));

        idleSheet = new Texture(Gdx.files.internal("player/Idle.png"));
        idleAnim = createAnimation(idleSheet, 9, 0.15f);

        runSheet = new Texture(Gdx.files.internal("player/Run.png"));
        runAnim = createAnimation(runSheet, 13, 0.1f);

        jumpSheet = new Texture(Gdx.files.internal("player/Double Jump.png"));
        jumpAnim = createAnimation(jumpSheet, 8, 0.1f);

        dashSheet = new Texture(Gdx.files.internal("player/Dash.png"));
        dashAnim = createAnimation(dashSheet, 12, 0.08f);

        attackSheet = new Texture(Gdx.files.internal("player/Slash.png"));
        attackAnim = createAnimation(attackSheet, 5, 0.08f);

        focusSheet = new Texture(Gdx.files.internal("player/Focus.png"));
        focusAnim = createAnimation(focusSheet, 4, 0.1f);

        previewCharacter = new AnimatedPreviewCharacter(idleAnim);

        Table splitTable = new Table();
        splitTable.setFillParent(true);

        Table leftTable = new Table();
        leftTable.add(previewCharacter).size(350, 350).center();

        Table rightTable = new Table();
        rightTable.center();

        Localization loc = Localization.getInstance();

        rightTable.add(createSectionTitle(loc.get("controls"))).padBottom(5).row();

        Table controlsTable = new Table();

        Actor leftMoveKey = createKeyWidget(settings.getKeyMoveLeft());
        Actor rightMoveKey = createKeyWidget(settings.getKeyMoveRight());
        addHoverListener(leftMoveKey, runAnim);
        addHoverListener(rightMoveKey, runAnim);

        controlsTable.add(createLabel("MOVE:")).align(Align.right).padRight(15).padBottom(5);
        Table moveGroup = new Table();
        moveGroup.add(leftMoveKey).padRight(5).size(30, 30);
        moveGroup.add(rightMoveKey).size(30, 30);
        controlsTable.add(moveGroup).align(Align.left).padBottom(5).row();

        Actor jumpKey = createKeyWidget(settings.getKeyJump());
        addHoverListener(jumpKey, jumpAnim);
        controlsTable.add(createLabel("JUMP:")).align(Align.right).padRight(15).padBottom(5);
        controlsTable.add(jumpKey).size(30, 30).align(Align.left).padBottom(5).row();

        Actor attackKey = createKeyWidget(settings.getKeyAttack());
        addHoverListener(attackKey, attackAnim);
        controlsTable.add(createLabel("ATTACK:")).align(Align.right).padRight(15).padBottom(5);
        controlsTable.add(attackKey).size(30, 30).align(Align.left).padBottom(5).row();

        Actor dashKey = createKeyWidget(settings.getKeyDash());
        addHoverListener(dashKey, dashAnim);
        controlsTable.add(createLabel("DASH:")).align(Align.right).padRight(15).padBottom(5);
        controlsTable.add(dashKey).size(30, 30).align(Align.left).padBottom(5).row();

        Actor focusKey = createKeyWidget(settings.getKeyFocus());
        addHoverListener(focusKey, focusAnim);
        controlsTable.add(createLabel("FOCUS:")).align(Align.right).padRight(15).padBottom(5);
        controlsTable.add(focusKey).size(30, 30).align(Align.left).padBottom(5).row();

        rightTable.add(controlsTable).padBottom(15).row();

        rightTable.add(createSectionTitle("ABILITIES")).padBottom(5).row();

        Table abilitiesTable = new Table();
        Label hLabel = new Label("- HEALTH: Represented by masks. Lost upon taking damage.", commonLabelStyle);
        hLabel.setFontScale(0.65f);
        Label sLabel = new Label("- SOUL: Fills by striking enemies. Used to cast Focus.", commonLabelStyle);
        sLabel.setFontScale(0.65f);

        abilitiesTable.add(hLabel).align(Align.left).padBottom(3).row();
        abilitiesTable.add(sLabel).align(Align.left).padBottom(10).row();
        rightTable.add(abilitiesTable).padBottom(10).row();

        rightTable.add(createSectionTitle("CHEAT CODES")).padBottom(5).row();

        Table cheatsTable = new Table();
        addCheatRow(cheatsTable, "BOSS TELEPORT:", "B");
        addCheatRow(cheatsTable, "NOCLIP MODE:", "N");
        addCheatRow(cheatsTable, "EMERGENCY HEAL:", "H");
        addCheatRow(cheatsTable, "REFILL SOUL:", "S");
        addCheatRow(cheatsTable, "GOD MODE:", "G");
        addCheatRow(cheatsTable, "INSTA-KILL (OPT):", "K");

        rightTable.add(cheatsTable).padBottom(15).row();

        TextButton backBtn = new TextButton(loc.get("back"), commonButtonStyle);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new MainMenuScreen());
            }
        });
        rightTable.add(createMenuRow(backBtn)).padTop(5).row();

        splitTable.add(leftTable).expand().align(Align.right).padRight(20);
        splitTable.add(rightTable).expand().align(Align.left).padLeft(20);

        rootTable.add(splitTable).expand().fill();
    }

    private Label createLabel(String text) {
        Label lbl = new Label(text, commonLabelStyle);
        lbl.setFontScale(0.9f);
        return lbl;
    }

    private Animation<TextureRegion> createAnimation(Texture texture, int cols, float frameDuration) {
        TextureRegion[][] tmp = TextureRegion.split(texture, texture.getWidth() / cols, texture.getHeight());
        TextureRegion[] frames = new TextureRegion[cols];
        for (int i = 0; i < cols; i++) {
            frames[i] = tmp[0][i];
        }

        Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        return animation;
    }

    private void addHoverListener(Actor actor, final Animation<TextureRegion> targetAnim) {
        actor.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                previewCharacter.setAnimation(targetAnim);
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                previewCharacter.setAnimation(idleAnim);
            }
        });
    }

    private Actor createKeyWidget(int keycode) {
        Texture bgTex = keyBgTexture;
        String text = Input.Keys.toString(keycode).toUpperCase();
        boolean hasText = true;

        if (keycode == Input.Keys.UP) { bgTex = arrowUpTex; hasText = false; }
        else if (keycode == Input.Keys.DOWN) { bgTex = arrowDownTex; hasText = false; }
        else if (keycode == Input.Keys.RIGHT) { bgTex = arrowRightTex; hasText = false; }
        else if (keycode == Input.Keys.LEFT) { bgTex = arrowLeftTex; hasText = false; }

        Container<Label> container = new Container<>();
        container.setBackground(new TextureRegionDrawable(new TextureRegion(bgTex)));
        container.minSize(30, 30);
        container.setTouchable(Touchable.enabled);

        if (hasText) {
            Label keyLabel = new Label(text, new Label.LabelStyle(GameAssetManager.trajanFont, Color.WHITE));
            if (text.length() > 2) keyLabel.setFontScale(0.45f);
            else keyLabel.setFontScale(0.7f);

            keyLabel.setAlignment(Align.center);
            container.setActor(keyLabel);
        }

        return container;
    }

    private Actor createKeyWidget(String text, float width) {
        Container<Label> container = new Container<>();
        container.setBackground(new TextureRegionDrawable(new TextureRegion(keyBgTexture)));
        container.minSize(width, 25);

        Label keyLabel = new Label(text, new Label.LabelStyle(GameAssetManager.trajanFont, Color.WHITE));
        if (text.length() > 2) {
            keyLabel.setFontScale(0.5f);
        }
        keyLabel.setAlignment(Align.center);
        container.setActor(keyLabel);

        return container;
    }

    private void addCheatRow(Table table, String name, String key) {
        Label nameLabel = new Label(name, commonLabelStyle);
        nameLabel.setFontScale(0.7f);
        table.add(nameLabel).align(Align.right).padRight(15).padBottom(5);

        Table keyGroup = new Table();
        keyGroup.add(createKeyWidget("CTRL", 35)).padRight(3).size(35, 25);

        Label plusLabel = new Label("+", commonLabelStyle);
        plusLabel.setFontScale(0.7f);
        keyGroup.add(plusLabel).padRight(3);

        keyGroup.add(createKeyWidget(key, 25)).size(25, 25);

        table.add(keyGroup).align(Align.left).padBottom(5).row();
    }

    private Table createSectionTitle(String text) {
        Table titleTable = new Table();
        Label titleLabel = new Label(text, commonLabelStyle);
        titleLabel.setFontScale(1.0f);
        titleTable.add(titleLabel).center().row();
        Image pointerImage = new Image(startPointerTexture);
        pointerImage.setScaling(Scaling.fit);
        pointerImage.setColor(Color.WHITE);
        titleTable.add(pointerImage).width(200).height(15).center().padTop(2);
        return titleTable;
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

        button.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                leftIndicator.setVisible(true); rightIndicator.setVisible(true);
                button.getLabel().setColor(Color.WHITE);
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                leftIndicator.setVisible(false); rightIndicator.setVisible(false);
                button.getLabel().setColor(new Color(0.8f, 0.8f, 0.8f, 1f));
            }
        });
        rowTable.add(leftIndicator).size(20, 20).padRight(15);
        rowTable.add(button);
        rowTable.add(rightIndicator).size(20, 20).padLeft(15);
        return rowTable;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (startPointerTexture != null) startPointerTexture.dispose();
        if (menuPointerTexture != null) menuPointerTexture.dispose();
        if (keyBgTexture != null) keyBgTexture.dispose();
        if (arrowUpTex != null) arrowUpTex.dispose();
        if (arrowDownTex != null) arrowDownTex.dispose();
        if (arrowLeftTex != null) arrowLeftTex.dispose();
        if (arrowRightTex != null) arrowRightTex.dispose();
        if (idleSheet != null) idleSheet.dispose();
        if (runSheet != null) runSheet.dispose();
        if (jumpSheet != null) jumpSheet.dispose();
        if (dashSheet != null) dashSheet.dispose();
        if (attackSheet != null) attackSheet.dispose();
        if (focusSheet != null) focusSheet.dispose();
    }

    private static class AnimatedPreviewCharacter extends Image {
        private Animation<TextureRegion> currentAnimation;
        private float stateTime = 0f;

        public AnimatedPreviewCharacter(Animation<TextureRegion> defaultAnimation) {
            super(defaultAnimation.getKeyFrame(0));
            this.currentAnimation = defaultAnimation;
            setScaling(Scaling.fit);
        }

        public void setAnimation(Animation<TextureRegion> animation) {
            if (this.currentAnimation != animation) {
                this.currentAnimation = animation;
                this.stateTime = 0f;
            }
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            stateTime += delta;
            TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime);
            setDrawable(new TextureRegionDrawable(currentFrame));
        }
    }
}
