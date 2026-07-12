package com.HollowKnight.model.enemies;

import com.HollowKnight.model.player.Player;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

public class HuskHornhead extends Enemy {
    public enum State { PATROL_WALK, PATROL_REST, TURNING, ANTICIPATE, CHARGE, DYING }
    private State currentState;

    private Animation<TextureRegion> walkAnim;
    private Animation<TextureRegion> idleAnim;
    private Animation<TextureRegion> turnAnim;
    private Animation<TextureRegion> anticipateAnim;
    private Animation<TextureRegion> lungeAnim;
    private Animation<TextureRegion> deathAnim;

    private float patrolSpeed = 40f;
    private float chargeSpeed = 200f;
    private float minX, maxX;
    public float fadeAlpha = 1f;

    private float stateTimer = 0f;
    private float walkDuration = 3.0f;
    private float restDuration = 1.5f;

    private Rectangle visionRange;
    private Player player;

    public HuskHornhead(float x, float y, float minX, float maxX, Player player) {
        super(x, y, 90, 80, 5);
        this.minX = minX;
        this.maxX = maxX;
        this.player = player;
        this.currentState = State.PATROL_WALK;

        visionRange = new Rectangle(0, 0, 300, 80);

        loadAnimations();
        this.velocity.x = patrolSpeed;
        this.facingRight = true;
    }

    private void loadAnimations() {
        walkAnim = createAnimation(new Texture("player/Husk_Hornhead/Walk.png"), 1, 7, 0.1f);
        idleAnim = createAnimation(new Texture("player/Husk_Hornhead/Idle.png"), 1, 6, 0.15f);
        turnAnim = createAnimation(new Texture("player/Husk_Hornhead/Turn.png"), 1, 2, 0.15f);
        anticipateAnim = createAnimation(new Texture("player/Husk_Hornhead/Attack Anticipate.png"), 1, 5, 0.1f);
        lungeAnim = createAnimation(new Texture("player/Husk_Hornhead/Attack Lunge.png"), 1, 12, 0.1f);
        deathAnim = createAnimation(new Texture("player/Husk_Hornhead/Death Land.png"), 1, 8, 0.15f);
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
        stateTimer += delta;

        Rectangle aggroRange = new Rectangle(position.x - 300, position.y, 600 + bounds.width, 80);

        if (isDead() && currentState != State.DYING) {
            currentState = State.DYING;
            stateTimer = 0;
            velocity.x = 0;
        }

        if (currentState == State.DYING) {
            if (deathAnim.isAnimationFinished(stateTimer)) {
                fadeAlpha -= delta;
                if (fadeAlpha < 0) fadeAlpha = 0;
            }
            return;
        }

        switch (currentState) {
            case PATROL_WALK:
                position.x += velocity.x * delta;

                if (aggroRange.overlaps(player.bounds) && !player.isDead) {
                    boolean playerIsRight = player.position.x > position.x;
                    if (playerIsRight != facingRight) {
                        currentState = State.TURNING;
                        stateTimer = 0;
                        velocity.x = 0;
                    } else {
                        currentState = State.ANTICIPATE;
                        stateTimer = 0;
                        velocity.x = 0;
                    }
                } else if (stateTimer >= walkDuration) {
                    currentState = State.PATROL_REST;
                    stateTimer = 0;
                    velocity.x = 0;
                } else if (position.x <= minX || position.x >= maxX) {
                    position.x = position.x <= minX ? minX : maxX;
                    currentState = State.TURNING;
                    stateTimer = 0;
                    velocity.x = 0;
                }
                break;

            case PATROL_REST:
                if (aggroRange.overlaps(player.bounds) && !player.isDead) {
                    boolean playerIsRight = player.position.x > position.x;
                    if (playerIsRight != facingRight) {
                        currentState = State.TURNING;
                        stateTimer = 0;
                        velocity.x = 0;
                    } else {
                        currentState = State.ANTICIPATE;
                        stateTimer = 0;
                    }
                } else if (stateTimer >= restDuration) {
                    currentState = State.PATROL_WALK;
                    stateTimer = 0;
                    velocity.x = facingRight ? patrolSpeed : -patrolSpeed;
                }
                break;

            case TURNING:
                if (turnAnim.isAnimationFinished(stateTimer)) {
                    facingRight = !facingRight;
                    velocity.x = facingRight ? patrolSpeed : -patrolSpeed;
                    currentState = State.PATROL_WALK;
                    stateTimer = 0;
                }
                break;

            case ANTICIPATE:
                if (anticipateAnim.isAnimationFinished(stateTimer)) {
                    currentState = State.CHARGE;
                    stateTimer = 0;
                    velocity.x = facingRight ? chargeSpeed : -chargeSpeed;
                }
                break;

            case CHARGE:
                position.x += velocity.x * delta;
                if (position.x <= minX || position.x >= maxX) {
                    position.x = position.x <= minX ? minX : maxX;
                    currentState = State.PATROL_REST;
                    stateTimer = 0;
                    velocity.x = 0;
                }
                break;
        }

        bounds.setPosition(position.x, position.y);
    }


    @Override
    public void render(SpriteBatch batch) {
        TextureRegion frame = null;

        switch (currentState) {
            case PATROL_WALK: frame = walkAnim.getKeyFrame(stateTimer, true); break;
            case PATROL_REST: frame = idleAnim.getKeyFrame(stateTimer, true); break;
            case TURNING:     frame = turnAnim.getKeyFrame(stateTimer, false); break;
            case ANTICIPATE:  frame = anticipateAnim.getKeyFrame(stateTimer, false); break;
            case CHARGE:      frame = lungeAnim.getKeyFrame(stateTimer, true); break;
            case DYING:       frame = deathAnim.getKeyFrame(stateTimer, false); break;
        }

        if (frame != null) {
            if (hitCooldown > 0 && hitCooldown % 0.2f > 0.1f && currentState != State.DYING) {
                batch.setColor(1, 0.5f, 0.5f, 1);
            } else {
                batch.setColor(1, 1, 1, fadeAlpha);
            }

            float drawX = position.x - 20f;
            float drawWidth = 140f;
            float drawY = position.y;
            float drawHeight = 100f;

            if (facingRight) {
                drawX = drawX + drawWidth;
                drawWidth = -drawWidth;
            }

            batch.draw(frame, drawX, drawY, drawWidth, drawHeight);

            batch.setColor(Color.WHITE);
        }
    }
}
