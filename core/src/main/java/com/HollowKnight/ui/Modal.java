package com.HollowKnight.ui;

import com.HollowKnight.GameAssetManager;
import com.HollowKnight.UiManager;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Modal extends Table {
    protected Skin skin;
    private Table wrapperTable;
    private boolean showing = false;

    public Modal() {
        skin = GameAssetManager.skin;
        wrapperTable = new Table();
        wrapperTable.setTouchable(Touchable.enabled);
        setTouchable(Touchable.enabled);
        setBackground(skin.getDrawable("window"));
        pad(10);
        wrapperTable.add(this);

        wrapperTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getTarget() == wrapperTable) {
                    hide();
                }
            }
        });
    }

    public void show() {
        if (!showing) {
            UiManager.getScreen().getModalStack().add(wrapperTable);
            wrapperTable.getStage().setKeyboardFocus(wrapperTable);
            showing = true;
        }
    }

    public void hide() {
        if (showing) {
            wrapperTable.remove();
            showing = false;
        }
    }

    public boolean isShowing() {
        return showing;
    }
}
