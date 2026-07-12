package com.HollowKnight.ui;

import com.HollowKnight.GameAssetManager;
import com.HollowKnight.model.player.Player;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class HUD {
    private Texture maskFrame;
    private Texture maskFull;
    private Texture maskEmpty;
    private Texture soulVessel;
    private Texture soulFillTexture;
    private Texture focusStart, focusLoop, focusEnd, focusGet;
    private Texture geoIcon;

    private float displaySoul = 0;

    private static final float MASK_SIZE = 30;
    private static final float MASK_SPACING = 35;

    private static final float MASK_START_X = 20;
    private static final float MASK_START_Y = 750;

    private static final float SOUL_X = 1180;
    private static final float SOUL_Y = 750;
    private static final float SOUL_SIZE = 40;

    public HUD() {
        maskFrame = new Texture("ui/select_gameHUD_0002_health_frame.png");
        maskFull = new Texture("ui/select_gameHUD_0001_health.png");
        maskEmpty = new Texture("ui/select_gameHUD_0001_health_steel.png");
        soulVessel = new Texture("ui/soul_vessel.png");
        soulFillTexture = new Texture("ui/soul_fill.png");
        geoIcon = new Texture("ui/geo.png");
        focusStart = new Texture("player/Focus Start.png");
        focusLoop = new Texture("player/Focus.png");
        focusGet = new Texture("player/Focus Get.png");
        focusEnd = new Texture("player/Focus End.png");
    }

    public void render(SpriteBatch batch, Player player) {
        float vesselX = 40;
        float vesselY = 620;
        float vesselSize = 90;

        displaySoul += (player.soul - displaySoul) * 5f * com.badlogic.gdx.Gdx.graphics.getDeltaTime();

        float fillRatio = displaySoul / Player.MAX_SOUL;

        if (fillRatio > 0.01f) {
            int fillHeight = (int) (soulFillTexture.getHeight() * fillRatio);
            TextureRegion soulRegion = new TextureRegion(soulFillTexture, 0, soulFillTexture.getHeight() - fillHeight, soulFillTexture.getWidth(), fillHeight);
            batch.draw(soulRegion, vesselX, vesselY, vesselSize, vesselSize * fillRatio);
        }

        if (soulVessel != null) {
            batch.draw(soulVessel, vesselX, vesselY, vesselSize, vesselSize);
        }

        float maskStartX = vesselX + vesselSize + 10;
        float maskY = vesselY + 60;
        float maskSize = 45;
        float spacing = 50;

        batch.draw(maskFrame, maskStartX - 60, maskY - 10, maskSize + 40, maskSize + 40);

        for (int i = 0; i < player.maxHealth; i++) {
            float currentX = maskStartX + (i * spacing);
            batch.draw(maskEmpty, currentX, maskY, maskSize, maskSize);

            if (i < player.health) {
                batch.draw(maskFull, currentX, maskY, maskSize, maskSize);
            }
        }

        float geoX = vesselX + 15;
        float geoY = vesselY - 30;

        if (geoIcon != null) {
            batch.draw(geoIcon, geoX, geoY, 25, 25);
        }

        if (GameAssetManager.trajanFont != null) {
            GameAssetManager.trajanFont.draw(batch, String.valueOf(player.geo), geoX + 35, geoY + 20);

            String soulText = player.soul + " / 99";
            float textX = vesselX + vesselSize + 10;
            float textY = vesselY + 30;
            GameAssetManager.trajanFont.draw(batch, soulText, textX, textY);
        }
    }

    public void dispose() {
        if (maskFrame != null) maskFrame.dispose();
        if (maskFull != null) maskFull.dispose();
        if (maskEmpty != null) maskEmpty.dispose();
        if (soulVessel != null) soulVessel.dispose();
        if (soulFillTexture != null) soulFillTexture.dispose();
        if (geoIcon != null) geoIcon.dispose();
    }
}
