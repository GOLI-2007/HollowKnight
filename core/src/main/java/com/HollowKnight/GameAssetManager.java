package com.HollowKnight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class GameAssetManager {
    public static Skin skin;
    private AssetManager manager;

    public static BitmapFont trajanFont;

    public GameAssetManager() {
        manager = new AssetManager();
    }

    public static void init() {
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/TrajanPro-Bold.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 20;
        parameter.color = Color.WHITE;

        trajanFont = generator.generateFont(parameter);

        generator.dispose();
    }

    public void loadRoom01Assets() {
        manager.load("map/startRoom/bg_room.png", Texture.class);
        manager.load("map/startRoom/ground_tile.png", Texture.class);
        manager.load("map/startRoom/wall_tile.png", Texture.class);
        manager.load("map/startRoom/bench.png", Texture.class);
        manager.load("map/startRoom/broken_bench.png", Texture.class);
        manager.load("map/startRoom/soul_totem.png", Texture.class);
        manager.load("map/startRoom/lamp.png", Texture.class);
        manager.load("map/startRoom/ruin_lamp.png", Texture.class);
        manager.load("map/startRoom/fung0.png", Texture.class);
        manager.load("map/startRoom/fung1.png", Texture.class);
        manager.load("map/startRoom/wall_top.png", Texture.class);
        manager.load("map/startRoom/broken_wall.png", Texture.class);

    }

    public void loadRoom02Assets() {
        manager.load("map/startRoom/bg_room.png", Texture.class);
        manager.load("map/startRoom/ground_tile.png", Texture.class);
        manager.load("map/startRoom/wall_tile.png", Texture.class);
        manager.load("map/room2/spike2.png", Texture.class);
        manager.load("map/startRoom/lamp.png", Texture.class);
        manager.load("map/room2/break.png", Texture.class);
        manager.load("map/room2/lamp_cave_01.png", Texture.class);
        manager.load("map/room2/lamp_cave_02.png", Texture.class);

    }

    public void loadRoom03Assets() {
        manager.load("map/startRoom/bg_room.png", Texture.class);
        manager.load("map/startRoom/ground_tile.png", Texture.class);
        manager.load("map/startRoom/wall_tile.png", Texture.class);
        manager.load("map/room2/break.png", Texture.class);
        manager.load("map/room3/breakable_wall.png", Texture.class);
        manager.load("map/room3/spike3.png", Texture.class);
        manager.load("map/room3/stag_door.png", Texture.class);
        manager.load("map/room3/plat_float_08.png", Texture.class);
        manager.load("map/room3/cliffs_lamp.png", Texture.class);
    }

    public void loadRoom04Assets() {
        manager.load("map/startRoom/bg_room.png", Texture.class);
        manager.load("map/startRoom/ground_tile.png", Texture.class);
        manager.load("map/startRoom/wall_tile.png", Texture.class);
        manager.load("map/room4/cocoon_base_01.png", Texture.class);

        for(int i = 7; i <= 22; i++) {
            String num = String.format("%03d", i);
            manager.load("map/room4/health_plant_020" + num + ".png", Texture.class);
        }
    }

    public void loadRoom05Assets() {
        manager.load("map/room5/bg_room5.png", Texture.class);
        manager.load("map/startRoom/ground_tile.png", Texture.class);
        manager.load("map/startRoom/wall_tile.png", Texture.class);
        manager.load("map/startRoom/lamp.png", Texture.class);
        manager.load("map/room5/pillar.png", Texture.class);
    }


    public AssetManager getManager() {
        return manager;
    }

    public static void dispose() {
        if (skin != null) skin.dispose();
        if (trajanFont != null) trajanFont.dispose();
    }

    public void disposeManager() {
        if (manager != null) manager.dispose();
    }

    public static Skin getSkin() {
        return skin;
    }
}
