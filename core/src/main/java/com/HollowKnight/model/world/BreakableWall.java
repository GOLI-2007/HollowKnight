package com.HollowKnight.model.world;

public class BreakableWall extends Platform {
    private int health;
    private boolean isDestroyed;

    public BreakableWall(float x, float y, float width, float height, int health) {
        super(x, y, width, height);
        this.health = health;
        this.isDestroyed = false;
    }

    public void takeDamage(int amount) {
        if (!isDestroyed) {
            health -= amount;
            if (health <= 0) {
                isDestroyed = true;
                this.bounds.set(0, 0, 0, 0);
            }
        }
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }
}
