package com.HollowKnight.model.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.HollowKnight.model.player.Player;

public class Mosquito extends Enemy {

    private enum State { PATROL, CHASE, ANTICIPATE, DASH }
    private State state = State.PATROL;

    private Player target;

    private Rectangle visionBox;
    private float visionWidth = 600f;
    private float visionHeight = 400f;

    private float speed = 150f;
    private float dashSpeed = 420f;

    private float startX;
    private float spawnY;
    private float patrolRadius = 150f;
    private int patrolDirection = 1;

    private float attackTriggerRange = 140f;
    private float minAttackDistance = 50f;

    private float stateTime = 0f;
    private float stateTimer = 0f;

    private static final float ANTICIPATE_DURATION = 0.45f;
    private static final float DASH_DURATION = 0.35f;
    private static final float ATTACK_COOLDOWN = 0.8f;
    private float attackCooldownTimer = 0f;
    private Vector2 dashDirection = new Vector2();

    private boolean isDead = false;
    public float fadeAlpha = 1.0f;
    private boolean diedInAir = false;

    private Animation<TextureRegion> idleTAni, attackAni, attackAnticipateAni, deathAirAni, deathLandAni, turnAni, turn2Ani;

    public Mosquito(float x, float y, float width, float height, Player player) {
        super(x, y, width, height, 2);
        this.target = player;
        this.startX = x;
        this.spawnY = y;

        visionBox = new Rectangle(x - visionWidth / 2, y - visionHeight / 2, visionWidth, visionHeight);

        idleTAni = createAnimation(new Texture("player/mosquito/Idle.png"), 1, 8, 0.1f);
        attackAni = createAnimation(new Texture("player/mosquito/Attack.png"), 1, 3, 0.1f);
        attackAnticipateAni = createAnimation(new Texture("player/mosquito/Attack Anticipate.png"), 1, 6, 0.1f);
        deathAirAni = createAnimation(new Texture("player/mosquito/Death_Air.png"), 1, 3, 0.1f);
        deathLandAni = createAnimation(new Texture("player/mosquito/Death_Land.png"), 1, 2, 0.1f);
        turnAni = createAnimation(new Texture("player/mosquito/Turn.png"), 1, 2, 0.1f);
        turn2Ani = createAnimation(new Texture("player/mosquito/Turn2.png"), 1, 2, 0.1f);
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
    public void update(float delta) {
        stateTime += delta;
        stateTimer += delta;

        if (hitCooldown > 0) hitCooldown -= delta;
        if (attackCooldownTimer > 0) attackCooldownTimer -= delta;

        if (health <= 0) {
            if (!isDead) {
                isDead = true;
                diedInAir = (position.y > 100);
                stateTime = 0f;
            }
            fadeAlpha -= delta * 0.5f;
            if (fadeAlpha < 0) fadeAlpha = 0;
            return;
        }

        visionBox.setPosition(position.x - (visionWidth - bounds.width) / 2, position.y - (visionHeight - bounds.height) / 2);

        float centerX = position.x + bounds.width / 2f;
        float centerY = position.y + bounds.height / 2f;
        float targetCenterX = target.position.x + target.bounds.width / 2f;
        float targetCenterY = target.position.y + target.bounds.height / 2f;
        float distance = Vector2.dst(centerX, centerY, targetCenterX, targetCenterY);

        switch (state) {
            case PATROL: {
                if (visionBox.overlaps(target.bounds)) {
                    changeState(State.CHASE);
                } else {
                    position.x += patrolDirection * (speed * 0.5f) * delta;
                    if (position.x >= startX + patrolRadius) {
                        position.x = startX + patrolRadius;
                        patrolDirection = -1;
                    } else if (position.x <= startX - patrolRadius) {
                        position.x = startX - patrolRadius;
                        patrolDirection = 1;
                    }
                    facingRight = patrolDirection > 0;
                }
                break;
            }

            case CHASE: {
                if (!visionBox.overlaps(target.bounds)) {
                    changeState(State.PATROL);
                    patrolDirection = (position.x < startX) ? 1 : -1;
                    break;
                }

                if (distance < attackTriggerRange && distance > minAttackDistance && attackCooldownTimer <= 0f) {
                    changeState(State.ANTICIPATE);
                    break;
                }

                if (distance > 5f) {
                    Vector2 direction = new Vector2(targetCenterX - centerX, targetCenterY - centerY).nor();
                    position.add(direction.x * speed * delta, direction.y * speed * delta);

                    if (Math.abs(targetCenterX - centerX) > 5f) {
                        facingRight = targetCenterX > centerX;
                    }
                }
                break;
            }

            case ANTICIPATE: {
                float retreatDir = (targetCenterX > centerX) ? -1f : 1f;
                position.x += retreatDir * 40f * delta;
                facingRight = targetCenterX > centerX;

                if (stateTimer >= ANTICIPATE_DURATION) {
                    float dx = targetCenterX - centerX;
                    float dy = targetCenterY - centerY;
                    dashDirection.set(dx, dy).nor();
                    changeState(State.DASH);
                }
                break;
            }

            case DASH: {
                position.add(dashDirection.x * dashSpeed * delta, dashDirection.y * dashSpeed * delta);
                if (Math.abs(dashDirection.x) > 0.01f) {
                    facingRight = dashDirection.x > 0;
                }

                if (stateTimer >= DASH_DURATION) {
                    attackCooldownTimer = ATTACK_COOLDOWN;
                    changeState(State.CHASE);
                }
                break;
            }
        }

        bounds.setPosition(position.x, position.y);
    }

    private void changeState(State newState) {
        state = newState;
        stateTimer = 0f;
        stateTime = 0f;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (fadeAlpha <= 0) return;

        TextureRegion currentFrame;

        if (health <= 0) {
            currentFrame = diedInAir ? deathAirAni.getKeyFrame(stateTime, false) : deathLandAni.getKeyFrame(stateTime, false);
        } else if (state == State.ANTICIPATE) {
            currentFrame = attackAnticipateAni.getKeyFrame(stateTime, false);
        } else if (state == State.DASH || state == State.CHASE) {
            currentFrame = attackAni.getKeyFrame(stateTime, true);
        } else {
            currentFrame = idleTAni.getKeyFrame(stateTime, true);
        }

        float oldR = batch.getColor().r;
        float oldG = batch.getColor().g;
        float oldB = batch.getColor().b;
        float oldA = batch.getColor().a;

        if (hitCooldown > 0 && hitCooldown % 0.2f > 0.1f && health > 0) {
            batch.setColor(1, 0.5f, 0.5f, 1);
        } else {
            batch.setColor(oldR, oldG, oldB, fadeAlpha);
        }

        if (facingRight) {
            batch.draw(currentFrame, position.x + bounds.width, position.y, -bounds.width, bounds.height);
        } else {
            batch.draw(currentFrame, position.x, position.y, bounds.width, bounds.height);
        }

        batch.setColor(oldR, oldG, oldB, oldA);
    }

    @Override
    public boolean isDying() {
        return health <= 0;
    }
}
