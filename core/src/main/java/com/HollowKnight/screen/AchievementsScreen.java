package com.HollowKnight.screen;

import com.HollowKnight.GameAssetManager;
import com.HollowKnight.Localization;
import com.HollowKnight.UiManager;
import com.HollowKnight.audio.AudioManager;
import com.HollowKnight.model.Achievement;
import com.HollowKnight.model.AchievementManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;

public class AchievementsScreen extends AbstractScreen {

    private Texture startPointerTexture;
    private Texture menuPointerTexture;
    private Texture scrollBgTexture;
    private Texture scrollKnobTexture;
    private Array<Texture> loadedIcons;

    public AchievementsScreen() {
        super();
        loadedIcons = new Array<>();
    }

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
        scrollBgTexture = new Texture(Gdx.files.internal("ui/scrollbar_single.png"));
        scrollKnobTexture = new Texture(Gdx.files.internal("ui/scrollbar_fleur_new.png"));

        Table mainTable = new Table();
        mainTable.center();
        mainTable.setFillParent(true);

        Localization loc = Localization.getInstance();
        mainTable.add(createSectionTitle(loc.get("achievements"))).padBottom(20).row();

        Table listTable = new Table();
        listTable.top().left();

        AchievementManager manager = AchievementManager.getInstance();

        for (Achievement ach : manager.getAchievements()) {
            boolean unlocked = ach.isUnlocked();

            Color nameColor = unlocked ? Color.WHITE : Color.GRAY;
            Color descColor = unlocked ? new Color(0.8f, 0.8f, 0.8f, 1f) : Color.DARK_GRAY;
            Color iconColor = unlocked ? Color.WHITE : new Color(0.3f, 0.3f, 0.3f, 1f);

            String displayName = (ach.getId().startsWith("secret") && !unlocked) ? "SECRET" : ach.getName().toUpperCase();
            String displayDesc = (ach.getId().startsWith("secret") && !unlocked) ? "This achievement is hidden" : ach.getDescription();

            Table rowTable = new Table();
            rowTable.left();

            Texture iconTex = null;
            if (ach.getIconFileName() != null && !ach.getIconFileName().isEmpty()) {
                try {
                    iconTex = new Texture(Gdx.files.internal("ui/Achievements/" + ach.getIconFileName()));
                    loadedIcons.add(iconTex);
                } catch (Exception e) {
                    System.out.println("Error loading icon: " + ach.getIconFileName());
                }
            }

            Image icon;
            if (iconTex != null) {
                icon = new Image(iconTex);
            } else {
                icon = new Image();
            }
            icon.setColor(iconColor);
            rowTable.add(icon).size(60, 60).padRight(15);

            Table textTable = new Table();
            textTable.left();

            Label nameLabel = new Label(displayName, commonLabelStyle);
            nameLabel.setColor(nameColor);
            textTable.add(nameLabel).left().row();

            Label descLabel = new Label(displayDesc, new Label.LabelStyle(GameAssetManager.trajanFont, new Color(0.7f, 0.7f, 0.7f, 1f)));
            descLabel.setColor(descColor);
            textTable.add(descLabel).left().padTop(2);

            rowTable.add(textTable).expandX().fillX();
            listTable.add(rowTable).expandX().fillX().padBottom(15).row();
        }

        // اسکرول
        ScrollPaneStyle scrollStyle = new ScrollPaneStyle();

        TextureRegionDrawable vScrollDrawable = new TextureRegionDrawable(new TextureRegion(scrollBgTexture)) {
            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {
                float lineWidth = 2f;
                float centeredX = x + (width - lineWidth) / 2f;
                super.draw(batch, centeredX, y, lineWidth, height);
            }
        };
        vScrollDrawable.setMinWidth(30f);

        TextureRegionDrawable vScrollKnobDrawable = new TextureRegionDrawable(new TextureRegion(scrollKnobTexture));
        vScrollKnobDrawable.setMinWidth(30f);
        vScrollKnobDrawable.setMinHeight(60f);

        scrollStyle.vScroll = vScrollDrawable;
        scrollStyle.vScrollKnob = vScrollKnobDrawable;

        ScrollPane scrollPane = new ScrollPane(listTable, scrollStyle);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setVariableSizeKnobs(false);

        mainTable.add(scrollPane).width(500).height(350).padBottom(30).row();

        TextButton backBtn = new TextButton(loc.get("back"), commonButtonStyle);
        backBtn.getLabel().setFontScale(1.1f);

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new MainMenuScreen());
            }
        });

        mainTable.add(createMenuRow(backBtn)).row();

        rootTable.add(mainTable).expand().fill();
    }

    private Table createSectionTitle(String text) {
        Table titleTable = new Table();
        titleTable.center();

        Label titleLabel = new Label(text, commonLabelStyle);
        titleLabel.setFontScale(1.3f);
        titleTable.add(titleLabel).center().row();

        Image pointerImage = new Image(startPointerTexture);
        pointerImage.setScaling(Scaling.fit);
        pointerImage.setColor(Color.WHITE);
        titleTable.add(pointerImage).width(350).height(30).center().padTop(5);

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

        rowTable.add(leftIndicator).size(25, 25).padRight(15);
        rowTable.add(button);
        rowTable.add(rightIndicator).size(25, 25).padLeft(15);

        return rowTable;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (startPointerTexture != null) startPointerTexture.dispose();
        if (menuPointerTexture != null) menuPointerTexture.dispose();
        if (scrollBgTexture != null) scrollBgTexture.dispose();
        if (scrollKnobTexture != null) scrollKnobTexture.dispose();
        for (Texture tex : loadedIcons) {
            tex.dispose();
        }
        loadedIcons.clear();
    }
}
