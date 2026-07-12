package com.HollowKnight.ui;

import com.HollowKnight.Localization;
import com.HollowKnight.model.Charm;
import com.HollowKnight.model.Inventory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

public class InventoryModal extends Table {

    private Label titleLabel;
    private Label descLabel;
    private Label statusLabel;
    private Skin skin;

    private Table notchesTable;
    private Texture notchUsedTex;
    private Texture notchEmptyTex;

    public InventoryModal(Skin skin) {
        this.skin = skin;
        setFillParent(true);

        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(new Color(0, 0, 0, 0.85f));
        bgPixmap.fill();
        setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap))));
        bgPixmap.dispose();

        Stack rootStack = new Stack();

        Table cornersTable = new Table();
        cornersTable.setFillParent(true);

        // گوشه بالا چپ
        Image topLeftCorner = createCornerImage("ui/overscan_corner_fleur009.png", false, false, Align.topLeft);
        cornersTable.add(topLeftCorner).size(80).expand().top().left().pad(15).padLeft(-37);

        // گوشه بالا راست
        Image topRightCorner = createCornerImage("ui/overscan_corner_fleur009.png", true, false, Align.topRight);
        cornersTable.add(topRightCorner).size(80).expand().top().right().pad(15).padRight(-37);
        cornersTable.row();

        // گوشه پایین چپ
        Image bottomLeftCorner = createCornerImage("ui/overscan_corner_fleur009.png", false, true, Align.bottomLeft);
        cornersTable.add(bottomLeftCorner).size(80).expand().bottom().left().pad(15).padLeft(-37);

        // گوشه پایین راست
        Image bottomRightCorner = createCornerImage("ui/overscan_corner_fleur009.png", true, true, Align.bottomRight);
        cornersTable.add(bottomRightCorner).size(80).expand().bottom().right().pad(15).padRight(-37);

        rootStack.add(cornersTable);

        Table mainContainer = new Table();

        Localization loc = Localization.getInstance();

        titleLabel = new Label(loc.get("select_item"), skin);
        titleLabel.setFontScale(1.4f);

        statusLabel = new Label("", skin);
        statusLabel.setColor(Color.LIGHT_GRAY);

        descLabel = new Label(loc.get("hover_details"), skin);
        descLabel.setWrap(true);

        Label topTitleLabel = new Label(loc.get("inventory"), skin);
        topTitleLabel.setFontScale(1.3f);
        mainContainer.add(topTitleLabel).padTop(20).row();

        Image topDecor = new Image(new Texture("ui/up_inventory.png"));
        topDecor.setScaling(Scaling.fit);
        mainContainer.add(topDecor).width(380).height(25).padBottom(20).row();

        Table bodyTable = new Table();

        Table leftColumn = new Table();
        leftColumn.top();

        Stack nailStack = new Stack();

        final Image nailGlow = new Image(new Texture("ui/charm_glow.png"));
        nailGlow.setScaling(Scaling.fit);
        nailGlow.setVisible(false);

        Image nailImg = new Image(new Texture("ui/old_nail.png"));
        nailImg.setScaling(Scaling.fit);

        Container<Image> nailContainer = new Container<>(nailImg);
        nailContainer.pad(5);

        nailStack.add(nailGlow);
        nailStack.add(nailContainer);
        leftColumn.add(nailStack).height(180).padBottom(10).row();

        Label nailTitle = new Label(loc.get("old_nail"), skin);
        nailTitle.setFontScale(1.1f);
        leftColumn.add(nailTitle).padBottom(20).row();

        notchUsedTex = new Texture("ui/notch_used.png");
        notchEmptyTex = new Texture("ui/notch_empty.png");

        notchesTable = new Table();
        updateNotches();
        leftColumn.add(notchesTable).padBottom(40).row();
        nailStack.addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                nailGlow.setVisible(true);
                titleLabel.setText(loc.get("old_nail"));
                descLabel.setText(loc.get("old_nail_desc"));
                statusLabel.setText("");
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                nailGlow.setVisible(false);
            }
        });

        Table geoTable = new Table();
        Stack geoStack = new Stack();

        final Image geoGlow = new Image(new Texture("ui/charm_glow.png"));
        geoGlow.setScaling(Scaling.fit);
        geoGlow.setVisible(false);

        Image geoImg = new Image(new Texture("ui/geo.png"));
        geoImg.setScaling(Scaling.fit);

        Container<Image> geoContainer = new Container<>(geoImg);
        geoContainer.pad(4);

        geoStack.add(geoGlow);
        geoStack.add(geoContainer);

        geoTable.add(geoStack).size(45);
        geoTable.add(new Label(String.valueOf(Inventory.getInstance().getGeo()), skin)).padLeft(8);
        leftColumn.add(geoTable).bottom().expandY();

        geoStack.addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                geoGlow.setVisible(true);
                titleLabel.setText("Geo");
                descLabel.setText("The currency of Hallownest, made\nfrom fossiled shells of various forms.\n\nCan be traded for goods or used as toll\nin various old mechanisms.");
                statusLabel.setText("");
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                geoGlow.setVisible(false);
            }
        });

        Table middleColumn = new Table();
        middleColumn.top();
        Table itemsTable = new Table();
        itemsTable.top().left();

        if (Inventory.getInstance().getCharms().isEmpty()) {
            itemsTable.add(new Label("Inventory is Empty", skin)).pad(20);
        } else {
            int colCount = 0;
            for (Charm charm : Inventory.getInstance().getCharms()) {
                itemsTable.add(createItemSlot(charm)).size(55, 55).pad(8);
                colCount++;
                if (colCount % 3 == 0) itemsTable.row();
            }
        }

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.vScroll = createScrollBackground();
        scrollStyle.vScrollKnob = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/scrollbar_fleur_new.png"))));
        scrollStyle.vScrollKnob.setMinWidth(28f);
        scrollStyle.vScrollKnob.setMinHeight(55f);

        ScrollPane scrollPane = new ScrollPane(itemsTable, scrollStyle);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setOverscroll(false, false);
        middleColumn.add(scrollPane).expand().fill();

        Table rightColumn = new Table();
        rightColumn.top().left();
        rightColumn.pad(15);

        rightColumn.add(titleLabel).left().padBottom(12).row();
        rightColumn.add(statusLabel).left().padBottom(30).row();
        rightColumn.add(descLabel).width(240).left().row();

        Texture divTex = new Texture("ui/divider.png");

        bodyTable.add(leftColumn).width(180).padRight(15);

        Image div1 = new Image(divTex);
        div1.setScaling(Scaling.stretchY);
        bodyTable.add(div1).width(10).expandY().fillY().padLeft(8).padRight(8);

        bodyTable.add(middleColumn).width(280).expandY().fillY();

        Image div2 = new Image(divTex);
        div2.setScaling(Scaling.stretchY);
        bodyTable.add(div2).width(10).expandY().fillY().padLeft(8).padRight(8);

        bodyTable.add(rightColumn).width(280).padLeft(15);

        mainContainer.add(bodyTable).expand().fill().padTop(15).padBottom(15).row();

        // فوتر
        Image bottomDecor = new Image(new Texture("ui/down_inventory.png"));
        bottomDecor.setScaling(Scaling.fit);
        mainContainer.add(bottomDecor).width(380).height(15).padTop(8).padBottom(20);

        rootStack.add(mainContainer);
        add(rootStack).expand().fill().pad(40);
    }

    private Image createCornerImage(String path, boolean flipX, boolean flipY, int alignment) {
        TextureRegion region = new TextureRegion(new Texture(path));
        region.flip(flipX, flipY);
        Image image = new Image(new TextureRegionDrawable(region));
        image.setScaling(Scaling.fit);
        image.setAlign(alignment);
        return image;
    }

    private TextureRegionDrawable createScrollBackground() {
        return new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/scrollbar_single.png")))) {
            @Override
            public void draw(com.badlogic.gdx.graphics.g2d.Batch batch, float x, float y, float width, float height) {
                float lineWidth = 2f;
                float centeredX = x + (width - lineWidth) / 2f;
                super.draw(batch, centeredX, y, lineWidth, height);
            }
        };
    }

    private Table createNotches() {
        Table table = new Table();
        for (int i = 0; i < Inventory.getInstance().getMaxNotches(); i++) {
            String tex = (i < Inventory.getInstance().getUsedNotches()) ? "ui/notch_used.png" : "ui/notch_overcharmed.png";
            table.add(new Image(new Texture(tex))).size(30).padRight(6);
        }
        return table;
    }

    private Stack createItemSlot(final Charm charm) {
        Stack slotStack = new Stack();

        final Image glowBg = new Image(new Texture("ui/charm_glow.png"));
        glowBg.setScaling(Scaling.fit);
        glowBg.setVisible(false);

        Image itemIcon = new Image(new Texture(Gdx.files.internal("ui/charm/" + charm.getId() + ".png")));
        itemIcon.setScaling(Scaling.fit);

        slotStack.add(glowBg);
        slotStack.add(itemIcon);

        slotStack.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                glowBg.setVisible(true);
                titleLabel.setText(charm.getName());
                descLabel.setText(charm.getDescription());

                if (charm.isEquipped()) {
                    statusLabel.setText(Localization.getInstance().get("equipped"));
                    statusLabel.setColor(Color.CYAN);
                } else {
                    statusLabel.setText(Localization.getInstance().get("click_to_equip"));
                    statusLabel.setColor(Color.LIGHT_GRAY);
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                glowBg.setVisible(false);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (charm.isEquipped()) {
                    Inventory.getInstance().unequipCharm(charm);
                    statusLabel.setText(Localization.getInstance().get("click_to_equip"));
                    statusLabel.setColor(Color.LIGHT_GRAY);
                    updateNotches();
                } else {
                    boolean success = Inventory.getInstance().equipCharm(charm);

                    if (success) {
                        statusLabel.setText(Localization.getInstance().get("equipped"));
                        statusLabel.setColor(Color.CYAN);
                        updateNotches();
                    } else {
                        statusLabel.setText("Not enough notches!");
                        statusLabel.setColor(Color.RED);
                    }
                }
            }
        });

        return slotStack;
    }

    private void updateNotches() {
        notchesTable.clearChildren();
        int max = Inventory.getInstance().getMaxNotches();
        int used = Inventory.getInstance().getUsedNotches();

        for (int i = 0; i < max; i++) {
            Texture tex = (i < used) ? notchUsedTex : notchEmptyTex;
            notchesTable.add(new Image(tex)).size(30).padRight(6);
        }
    }

}
