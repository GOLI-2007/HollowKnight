package com.HollowKnight.model.enemies;

import com.HollowKnight.model.player.Player;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Crystallized extends Enemy {
    private Player player;

    public enum State { IDLE, SHOOTING, ENRAGED, EVADING, TURNING, DYING }
    public State state = State.IDLE;

    private Animation<TextureRegion> deathAirAnim, EvadeAni, TurnAni, deathLandAnim, runAnim, shootAnim, idleAnim;

    private float stateTime = 0f;

    private TextureRegion laserBeamRegion;
    private Texture crystalLaserTex;
    private Texture laserCircleTex;

    private float timer = 0f;
    private float shootDelay = 0.5f;

    private Rectangle visionBounds;
    private Rectangle laserBounds;
    public float fadeAlpha = 1f;
    private boolean diedInAir = false;

    private float speed = 120f;
    private float attackRange = 350f;
    private float evadeDistance = 100f;
    private float laserTimer = 0f;
    private boolean isTelegraphing = false;

    private float laserCooldownTimer = 0f;
    private final float LASER_COOLDOWN_DURATION = 5.0f;

    public Crystallized(float x, float y, Player player) {
        super(x, y, 90, 110, 5);
        this.player = player;

        visionBounds = new Rectangle(x - 500, y - 200, 1000, 400);
        laserBounds = new Rectangle(x, y, 800, 40);

        crystalLaserTex = new Texture("player/crystallized/CrystalLaser.png");
        laserCircleTex = new Texture("player/crystallized/LaserCircle.png");
        laserBeamRegion = new TextureRegion(crystalLaserTex);
        idleAnim = createAnimation(new Texture("player/crystallized/Idle.png"), 1, 5, 0.1f);
        runAnim = createAnimation(new Texture("player/crystallized/Run.png"), 1, 6, 0.1f);
        shootAnim = createAnimation(new Texture("player/crystallized/Shoot.png"), 1, 7, 0.1f);
        EvadeAni = createAnimation(new Texture("player/crystallized/Evade.png"), 1, 7, 0.1f);
        TurnAni = createAnimation(new Texture("player/crystallized/Turn.png"), 1, 3, 0.1f);
        deathLandAnim = createAnimation(new Texture("player/crystallized/Death_Land.png"), 1, 3, 0.1f);
        deathAirAnim = createAnimation(new Texture("player/crystallized/Death_Air.png"), 1, 3, 0.1f);
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
        timer += delta;

        if (hitCooldown > 0) hitCooldown -= delta;
        if (laserCooldownTimer > 0) laserCooldownTimer -= delta;

        if (health <= 0) {
            if (state != State.DYING) {
                state = State.DYING;
                diedInAir = (position.y > 100);
                stateTime = 0f;
                laserBounds.set(0, 0, 0, 0);
            }
            fadeAlpha -= delta * 0.5f;
            if (fadeAlpha < 0) fadeAlpha = 0;
            return;
        }

        visionBounds.setPosition(position.x - visionBounds.width / 2, position.y - visionBounds.height / 2);

        float distanceToPlayer = Vector2.dst(position.x, position.y, player.position.x, player.position.y);
        boolean playerToRight = player.position.x > position.x;

        switch (state) {
            case IDLE:
                if (distanceToPlayer < evadeDistance) {
                    state = State.EVADING;
                    stateTime = 0f;
                } else if (distanceToPlayer < attackRange && Math.abs(player.position.y - position.y) < 50 && laserCooldownTimer <= 0) {
                    state = State.SHOOTING;
                    stateTime = 0f;
                    laserTimer = 0f;
                    isTelegraphing = true;
                    facingRight = playerToRight;
                } else if (visionBounds.overlaps(player.bounds)) {
                    state = State.ENRAGED;
                    stateTime = 0f;
                }
                break;

            case ENRAGED:
                if (distanceToPlayer < evadeDistance) {
                    state = State.EVADING;
                    stateTime = 0f;
                } else if (distanceToPlayer < attackRange && Math.abs(player.position.y - position.y) < 50 && laserCooldownTimer <= 0) {
                    state = State.SHOOTING;
                    stateTime = 0f;
                    laserTimer = 0f;
                    isTelegraphing = true;
                    facingRight = playerToRight;
                } else {
                    facingRight = playerToRight;
                    float moveDir = facingRight ? 1 : -1;
                    position.x += moveDir * speed * delta;

                    if (!visionBounds.overlaps(player.bounds)) {
                        state = State.IDLE;
                    }
                }
                break;

            case EVADING:
                if (stateTime < 0.5f) {
                    float evadeDir = playerToRight ? -1 : 1;
                    position.x += evadeDir * (speed * 1.5f) * delta;
                } else {
                    if (laserCooldownTimer <= 0) {
                        state = State.SHOOTING;
                        stateTime = 0f;
                        laserTimer = 0f;
                        isTelegraphing = true;
                        facingRight = playerToRight;
                    } else {
                        state = State.IDLE;
                    }
                }
                break;

            case TURNING:
                if (TurnAni.isAnimationFinished(stateTime)) {
                    facingRight = playerToRight;
                    state = State.ENRAGED;
                    stateTime = 0f;
                }
                break;

            case SHOOTING:
                laserTimer += delta;
                if (isTelegraphing) {
                    if (laserTimer > shootDelay) {
                        isTelegraphing = false;
                    }
                } else {
                    float laserX = facingRight ? position.x + bounds.width : position.x - 800;
                    laserBounds.set(laserX, position.y + 30, 800, 40);

                    if (laserBounds.overlaps(player.bounds)) {
                        player.takeDamage(1);
                    }

                    if (laserTimer > shootDelay + 0.5f) {
                        state = State.IDLE;
                        stateTime = 0f;
                        laserCooldownTimer = LASER_COOLDOWN_DURATION;
                        laserBounds.set(0, 0, 0, 0);
                    }
                }
                break;
        }
        bounds.setPosition(position.x, position.y);
    }

    @Override
    public void render(SpriteBatch batch) {
        if (fadeAlpha <= 0) return;

        TextureRegion currentFrame;

        if (state == State.DYING) {
            currentFrame = diedInAir ? deathAirAnim.getKeyFrame(stateTime, false) : deathLandAnim.getKeyFrame(stateTime, false);
        } else if (state == State.SHOOTING) {
            currentFrame = shootAnim.getKeyFrame(stateTime, false);
        } else if (state == State.ENRAGED) {
            currentFrame = runAnim.getKeyFrame(stateTime, true);
        } else if (state == State.EVADING) {
            currentFrame = EvadeAni.getKeyFrame(stateTime, false);
        } else if (state == State.TURNING) {
            currentFrame = TurnAni.getKeyFrame(stateTime, false);
        } else {
            currentFrame = idleAnim.getKeyFrame(stateTime, true);
        }

        float oldR = batch.getColor().r;
        float oldG = batch.getColor().g;
        float oldB = batch.getColor().b;
        float oldA = batch.getColor().a;

        if (state == State.SHOOTING) {
            float laserOriginX = facingRight ? position.x + bounds.width : position.x - 800;
            float laserOriginY = position.y + 30;

            if (isTelegraphing) {
                batch.setColor(1, 1, 1, 0.5f);
                float circleOriginX = facingRight ? position.x + bounds.width : position.x;
                batch.draw(laserCircleTex, circleOriginX - 15, laserOriginY - 15, 30, 30);
                batch.setColor(oldR, oldG, oldB, oldA);
            } else {
                if ((facingRight && !currentFrame.isFlipX()) || (!facingRight && currentFrame.isFlipX())) {
                    currentFrame.flip(true, false);
                }

                batch.draw(laserBeamRegion, laserOriginX, laserOriginY, 800, 40);
            }
        }

        if (hitCooldown > 0 && hitCooldown % 0.2f > 0.1f && state != State.DYING) {
            batch.setColor(1, 0.5f, 0.5f, 1);
        } else {
            batch.setColor(oldR, oldG, oldB, fadeAlpha);
        }

        if (currentFrame != null) {
            if ((facingRight && !currentFrame.isFlipX()) || (!facingRight && currentFrame.isFlipX())) {
                currentFrame.flip(true, false);
            }

            batch.draw(currentFrame, position.x, position.y, bounds.width, bounds.height);
        }

        batch.setColor(oldR, oldG, oldB, oldA);
    }

    @Override
    public boolean isDying() {
        return health <= 0;
    }
}
