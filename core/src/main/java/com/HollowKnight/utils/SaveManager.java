package com.HollowKnight.utils;

import com.HollowKnight.model.GameSave;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class SaveManager {
    private static final String SAVE_DIR = "saves/";
    private Json json;

    public SaveManager() {
        json = new Json();
        FileHandle dir = Gdx.files.local(SAVE_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    public GameSave loadSave(int slotIndex) {
        String path = SAVE_DIR + "save_" + slotIndex + ".json";
        FileHandle file = Gdx.files.local(path);
        if (file.exists()) {
            try {
                return json.fromJson(GameSave.class, file);
            } catch (Exception e) {
                Gdx.app.log("SaveManager", "Error loading save", e);
                return null;
            }
        }
        return null;
    }

    public void saveGame(int slotIndex, GameSave save) {
        String path = SAVE_DIR + "save_" + slotIndex + ".json";
        Gdx.files.local(path).writeString(json.prettyPrint(save), false);
    }

    public void deleteSave(int slotIndex) {
        String path = SAVE_DIR + "save_" + slotIndex + ".json";
        FileHandle file = Gdx.files.local(path);
        if (file.exists()) file.delete();
    }

    public GameSave createNewSave(int slotIndex, String saveName) {
        GameSave newSave = new GameSave(saveName, 0, "0m", "Crossroads");
        newSave.setSlotIndex(slotIndex);
        saveGame(slotIndex, newSave);
        return newSave;
    }

    public boolean saveExists(int slotIndex) {
        return Gdx.files.local(SAVE_DIR + "save_" + slotIndex + ".json").exists();
    }
}
