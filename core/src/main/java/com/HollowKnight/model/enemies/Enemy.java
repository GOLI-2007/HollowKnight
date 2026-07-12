package com.HollowKnight.model.enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Enemy {
    public Vector2 position;
    public Vector2 velocity;
    public Rectangle bounds;
    public int health;
    protected float stateTime = 0f;
    protected boolean facingRight = false;
    protected float hitCooldown = 0f;

    public Enemy(float x, float y, float width, float height, int health) {
        position = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        bounds = new Rectangle(x, y, width, height);
        this.health = health;
    }

    public abstract void update(float delta);
    public abstract void render(SpriteBatch batch);
    public abstract boolean isDying();

    public void takeDamage(int damage) {
        if (hitCooldown <= 0 && !isDying()) {
            this.health -= damage;
            hitCooldown = 0.5f;
        }
    }


    public boolean isDead() {
        return this.health <= 0;
    }
}
