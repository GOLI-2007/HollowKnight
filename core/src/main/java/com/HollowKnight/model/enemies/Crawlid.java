package com.HollowKnight.model.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Color;

public class Crawlid extends Enemy {
    public enum State { WALKING, TURNING, DYING }
    private State currentState;

    private Animation<TextureRegion> walkAnim;
    private Animation<TextureRegion> turnAnim;
    private Animation<TextureRegion> deathAnim;

    private float speed = 50f;
    private float minX, maxX;
    public float fadeAlpha = 1f;

    public Crawlid(float x, float y, float walkRadius) {
        super(x, y, 80, 50, 3);
        this.minX = x - walkRadius;
        this.maxX = x + walkRadius;
        this.currentState = State.WALKING;

        loadAnimations();

        this.velocity.x = -speed;
        this.facingRight = false;
    }

    private void loadAnimations() {

        walkAnim = createAnimation(new Texture("player/crawild/Walk.png"), 1, 4, 0.1f);
        turnAnim = createAnimation(new Texture("player/crawild/Turn.png"), 1, 2, 0.15f);
        deathAnim = createAnimation(new Texture("player/crawild/Death Land.png"), 1, 2, 0.15f);
    }

    private Animation<TextureRegion> createAnimation(Texture sheet, int rows, int cols, float frameDuration) {
        TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth() / cols, sheet.getHeight() / rows);
        TextureRegion[] frames = new TextureRegion[cols * rows];
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                frames[index++] = tmp[i][j];
            }
        }
        return new Animation<>(frameDuration, frames);
    }

    @Override
    public boolean isDying() {
        return currentState == State.DYING;
    }

    @Override
    public void update(float delta) {
        if (hitCooldown > 0) hitCooldown -= delta;

        if (isDead() && currentState != State.DYING) {
            currentState = State.DYING;
            stateTime = 0;
            velocity.x = 0;
        }

        if (currentState == State.DYING) {
            stateTime += delta;
            if (deathAnim.isAnimationFinished(stateTime)) {
                fadeAlpha -= delta;
                if (fadeAlpha < 0) fadeAlpha = 0;
            }
            return;
        }

        if (currentState == State.TURNING) {
            stateTime += delta;
            if (turnAnim.isAnimationFinished(stateTime)) {
                facingRight = !facingRight;
                velocity.x = facingRight ? speed : -speed;
                currentState = State.WALKING;
                stateTime = 0;
            }
            return;
        }

        if (currentState == State.WALKING) {
            position.x += velocity.x * delta;

            if (position.x <= minX && !facingRight) {
                position.x = minX;
                currentState = State.TURNING;
                stateTime = 0;
                velocity.x = 0;
            } else if (position.x >= maxX && facingRight) {
                position.x = maxX;
                currentState = State.TURNING;
                stateTime = 0;
                velocity.x = 0;
            }
            stateTime += delta;
        }

        bounds.setPosition(position.x, position.y);
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion frame = null;

        switch (currentState) {
            case WALKING: frame = walkAnim.getKeyFrame(stateTime, true); break;
            case TURNING: frame = turnAnim.getKeyFrame(stateTime, false); break;
            case DYING:   frame = deathAnim.getKeyFrame(stateTime, false); break;
        }

        if (frame != null) {
            if ((facingRight && !frame.isFlipX()) || (!facingRight && frame.isFlipX())) {
                frame.flip(true, false);
            }

            float oldR = batch.getColor().r;
            float oldG = batch.getColor().g;
            float oldB = batch.getColor().b;
            float oldA = batch.getColor().a;

            Color c = batch.getColor();
            if (hitCooldown > 0 && hitCooldown % 0.2f > 0.1f && currentState != State.DYING) {
                batch.setColor(1, 0.5f, 0.5f, 1);
            } else {
                batch.setColor(oldR, oldG, oldB, fadeAlpha);
            }

            batch.draw(frame, position.x - 24f, position.y, 128f, 80f);
            batch.setColor(oldR, oldG, oldB, oldA);
        }
    }
}
