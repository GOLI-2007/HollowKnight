package com.HollowKnight.model.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Platform {
    public Rectangle bounds;
    private Texture texture;

    public Platform(float x, float y, float width, float height) {
        bounds = new Rectangle(x, y, width, height);
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void render(SpriteBatch batch) {
        if (texture != null) {
            float texWidth = texture.getWidth();
            float texHeight = texture.getHeight();

            for (float x = bounds.x; x < bounds.x + bounds.width; x += texWidth) {
                for (float y = bounds.y; y < bounds.y + bounds.height; y += texHeight) {
                    float drawWidth = Math.min(texWidth, (bounds.x + bounds.width) - x);
                    float drawHeight = Math.min(texHeight, (bounds.y + bounds.height) - y);

                    int srcWidth = (int) drawWidth;
                    int srcHeight = (int) drawHeight;

                    batch.draw(texture, x, y, drawWidth, drawHeight,
                        0, 0, srcWidth, srcHeight, false, false);
                }
            }
        }
    }
}
