package com.HollowKnight.model.enemies;

import com.HollowKnight.model.player.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class WingedSentry extends Enemy {

    private Player player;
    public float fadeAlpha = 1f;

    private enum State { IDLE, CHASE, ANTIC, DIVE, RETREAT, DEATH_AIR, DEATH_LAND }
    private State currentState = State.IDLE;

    private float stateTime = 0f;
    private float stateTimer = 0f;
    private Vector2 spawnPoint;

    private Texture idleSheet, anticSheet, chargeSheet, deathAirSheet, deathLandSheet;
    private Animation<TextureRegion> idleAnim, anticAnim, chargeAnim, deathAirAnim, deathLandAnim;

    public WingedSentry(float x, float y, Player player) {
        super(x, y, 130, 145, 4);
        this.player = player;
        this.spawnPoint = new Vector2(x, y);

        loadAnimations();
    }

    private void loadAnimations() {
        idleSheet = new Texture(Gdx.files.internal("player/Winged_Sentry/Idle.png"));
        anticSheet = new Texture(Gdx.files.internal("player/Winged_Sentry/Charge Antic.png"));
        chargeSheet = new Texture(Gdx.files.internal("player/Winged_Sentry/Charge.png"));
        deathAirSheet = new Texture(Gdx.files.internal("player/Winged_Sentry/Death Air.png"));
        deathLandSheet = new Texture(Gdx.files.internal("player/Winged_Sentry/Death Land.png"));

        idleAnim = createAnimation(idleSheet, 7, 0.1f, Animation.PlayMode.LOOP);
        anticAnim = createAnimation(anticSheet, 4, 0.1f, Animation.PlayMode.NORMAL);
        chargeAnim = createAnimation(chargeSheet, 3, 0.1f, Animation.PlayMode.LOOP);
        deathAirAnim = createAnimation(deathAirSheet, 2, 0.1f, Animation.PlayMode.LOOP);
        deathLandAnim = createAnimation(deathLandSheet, 4, 0.15f, Animation.PlayMode.NORMAL);
    }

    private Animation<TextureRegion> createAnimation(Texture sheet, int frameCount, float frameDuration, Animation.PlayMode mode) {
        int frameWidth = sheet.getWidth() / frameCount;
        TextureRegion[][] tmp = TextureRegion.split(sheet, frameWidth, sheet.getHeight());
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < frameCount; i++) {
            frames.add(tmp[0][i]);
        }
        return new Animation<>(frameDuration, frames, mode);
    }

    @Override
    public void takeDamage(int damage) {
        int oldHealth = this.health;
        super.takeDamage(damage);

        if (this.health < oldHealth && !isDying()) {
            currentState = State.RETREAT;
            stateTimer = 0f;

            float pushX = (position.x > player.position.x) ? 250f : -250f;
            velocity.set(pushX, 150f);
        }
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        stateTimer += delta;

        if (hitCooldown > 0) hitCooldown -= delta;

        if (isDying()) {
            if (currentState != State.DEATH_AIR && currentState != State.DEATH_LAND) {
                currentState = State.DEATH_AIR;
                velocity.set(0, -300);
            }

            if (currentState == State.DEATH_AIR) {
                position.y += velocity.y * delta;
                if (position.y <= 64) {
                    position.y = 64;
                    currentState = State.DEATH_LAND;
                    stateTime = 0;
                    velocity.set(0, 0);
                }
            } else if (currentState == State.DEATH_LAND) {
                if (deathLandAnim.isAnimationFinished(stateTime)) {
                    fadeAlpha -= delta * 1.5f;
                }
            }
        } else {
            float distToPlayer = Vector2.dst(position.x, position.y, player.position.x, player.position.y);

            switch (currentState) {
                case IDLE:
                    velocity.x = (spawnPoint.x - position.x) * 1.5f;
                    velocity.y = (spawnPoint.y - position.y) * 1.5f;

                    if (distToPlayer < 450) {
                        currentState = State.CHASE;
                    }
                    break;

                case CHASE:
                    float targetX = player.position.x;
                    float targetY = player.position.y + 160;

                    velocity.x = (targetX - position.x) * 1.5f;
                    velocity.y = (targetY - position.y) * 1.5f;

                    if (distToPlayer > 600) {
                        currentState = State.IDLE;
                    }
                    else if (distToPlayer < 250) {
                        currentState = State.ANTIC;
                        stateTimer = 0f;
                        velocity.set(0, 0);
                    }
                    break;

                case ANTIC:
                    velocity.y = 20f;
                    if (stateTimer >= 0.4f) {
                        currentState = State.DIVE;
                        stateTimer = 0f;

                        Vector2 direction = new Vector2(player.position.x - position.x, (player.position.y - 10) - position.y);
                        direction.nor();
                        velocity.set(direction.x * 550, direction.y * 550);
                    }
                    break;

                case DIVE:
                    if (position.y <= 64 || stateTimer >= 0.7f) {
                        currentState = State.CHASE;
                        stateTimer = 0f;
                    }
                    break;

                case RETREAT:
                    if (stateTimer >= 0.5f) {
                        currentState = State.CHASE;
                        stateTimer = 0f;
                    }
                    break;
            }

            position.add(velocity.x * delta, velocity.y * delta);

            if (velocity.x > 10) facingRight = true;
            else if (velocity.x < -10) facingRight = false;
        }

        bounds.setPosition(position.x, position.y);
    }

    @Override
    public void render(SpriteBatch batch) {
        if (fadeAlpha <= 0) return;

        TextureRegion currentFrame = idleAnim.getKeyFrame(stateTime);

        if (isDying()) {
            if (currentState == State.DEATH_AIR) currentFrame = deathAirAnim.getKeyFrame(stateTime);
            else if (currentState == State.DEATH_LAND) currentFrame = deathLandAnim.getKeyFrame(stateTime);
        } else {
            switch (currentState) {
                case IDLE:
                case CHASE:
                case RETREAT:
                    currentFrame = idleAnim.getKeyFrame(stateTime); break;
                case ANTIC:
                    currentFrame = anticAnim.getKeyFrame(stateTime); break;
                case DIVE:
                    currentFrame = chargeAnim.getKeyFrame(stateTime); break;
            }
        }

        if (hitCooldown > 0 && !isDying()) batch.setColor(1, 0, 0, fadeAlpha);
        else batch.setColor(1, 1, 1, fadeAlpha);

        if (facingRight && !currentFrame.isFlipX()) currentFrame.flip(true, false);
        else if (!facingRight && currentFrame.isFlipX()) currentFrame.flip(true, false);

        batch.draw(currentFrame, position.x, position.y, bounds.width, bounds.height);

        batch.setColor(1, 1, 1, 1);
    }

    @Override
    public boolean isDying() {
        return this.health <= 0;
    }
}
