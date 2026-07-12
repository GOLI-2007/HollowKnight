package com.HollowKnight.controller;

import com.HollowKnight.UiManager;
import com.HollowKnight.GameAssetManager;
import com.HollowKnight.ui.InventoryModal;
import com.HollowKnight.ui.PauseModal;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class GameProcessor implements InputProcessor {

    private PauseModal pauseModal;
    private InventoryModal inventoryModal;

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.ESCAPE:
                if (inventoryModal != null) {
                    inventoryModal.remove();
                    inventoryModal = null;
                    return true;
                }

                if (pauseModal == null || !pauseModal.isShowing()) {
                    pauseModal = new PauseModal();
                    pauseModal.show();
                } else {
                    pauseModal.hide();
                    pauseModal = null;
                }
                return true;

            case Input.Keys.I:
                if (pauseModal != null && pauseModal.isShowing()) {
                    return true;
                }

                if (inventoryModal == null) {
                    inventoryModal = new InventoryModal(GameAssetManager.skin);
                    UiManager.getScreen().getStage().addActor(inventoryModal);
                } else {
                    inventoryModal.remove();
                    inventoryModal = null;
                }
                return true;

            // بقیه کلیدها (حرکت، پرش، حمله، دش) توسط Player مدیریت می‌شود
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
