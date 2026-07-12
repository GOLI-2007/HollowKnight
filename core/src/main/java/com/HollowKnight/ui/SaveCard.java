package com.HollowKnight.ui;

import com.HollowKnight.GameAssetManager;
import com.HollowKnight.model.GameSave;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class SaveCard extends Table {

    public interface SaveCardListener {
        void onPlayClicked(int slotIndex, GameSave save);
        void onClearClicked(int slotIndex);
    }

    public SaveCard(final int slotIndex, final GameSave save, final SaveCardListener listener) {
        super();

        Label.LabelStyle textStyle = new Label.LabelStyle(GameAssetManager.trajanFont, Color.WHITE);
        Label.LabelStyle subTextStyle = new Label.LabelStyle(GameAssetManager.trajanFont, new Color(0.7f, 0.7f, 0.7f, 1f));
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = GameAssetManager.trajanFont;
        btnStyle.fontColor = new Color(0.7f, 0.7f, 0.7f, 1f);
        btnStyle.overFontColor = Color.WHITE;

        Table cardContent = new Table();
        cardContent.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);

        if (save != null) {
            Texture bgTex = new Texture(com.badlogic.gdx.Gdx.files.internal("ui/slot_bg.png"));
            cardContent.setBackground(new TextureRegionDrawable(bgTex));

            Table leftData = new Table();

            Texture orbTex = new Texture(com.badlogic.gdx.Gdx.files.internal("ui/soul_orb_fullv020000.png"));
            Image playerIcon = new Image(orbTex);
            leftData.add(playerIcon).size(60, 60).padRight(10);

            Table statsTable = new Table();
            statsTable.add(new Label("Masks: " + save.getHealth() + "/" + save.getMaxHealth(), textStyle)).left().row();
            statsTable.add(new Label("Geo: " + save.getGeo(), textStyle)).left().padTop(5);
            leftData.add(statsTable);

            Table rightData = new Table();
            rightData.add(new Label(save.getCurrentScene().toUpperCase(), textStyle)).align(Align.right).row();
            rightData.add(new Label(save.getTimePlayed(), subTextStyle)).align(Align.right).padTop(10);

            cardContent.add(new Label(slotIndex + ".", textStyle)).padRight(15);
            cardContent.add(leftData).expandX().left().padLeft(20);
            cardContent.add(rightData).expandX().right().padRight(20);

            TextButton clearBtn = new TextButton("CLEAR SAVE", btnStyle);
            clearBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    listener.onClearClicked(slotIndex);
                }
            });

            this.add(cardContent).width(600).height(100);
            this.add(clearBtn).padLeft(20);

        } else {
            cardContent.setBackground((Drawable) null);
            cardContent.add(new Label(slotIndex + ".   NEW GAME", textStyle)).expand().center();
            this.add(cardContent).width(600).height(80);
        }

        cardContent.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.onPlayClicked(slotIndex, save);
            }
        });
    }
}
