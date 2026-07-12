package com.HollowKnight.model.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;

public class GeoItem {
    public Vector2 position;
    public Vector2 velocity;
    public Rectangle bounds;
    private Texture texture;
    public boolean isCollected = false;
    private boolean isGrounded = false;

    public GeoItem(float x, float y, Texture tex) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(MathUtils.random(-100, 100), MathUtils.random(150, 300));
        this.bounds = new Rectangle(x, y, 20, 20);
        this.texture = tex;
    }

    public void update(float delta, ArrayList<Platform> platforms) {
        if (isCollected) return;

        // اعمال جاذبه
        velocity.y -= 800f * delta;
        position.add(velocity.x * delta, velocity.y * delta);

        // برخورد ساده با زمین (پلتفرم‌ها)
        isGrounded = false;
        for (Platform p : platforms) {
            if (position.y <= p.bounds.y + p.bounds.height && position.y >= p.bounds.y &&
                position.x >= p.bounds.x && position.x <= p.bounds.x + p.bounds.width) {
                position.y = p.bounds.y + p.bounds.height;
                velocity.set(0, 0);
                isGrounded = true;
                break;
            }
        }
        bounds.setPosition(position.x, position.y);
    }

    public void render(SpriteBatch batch) {
        if (!isCollected) {
            batch.draw(texture, position.x, position.y, 20, 20);
        }
    }
}
