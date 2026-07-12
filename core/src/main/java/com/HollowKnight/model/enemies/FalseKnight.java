package com.HollowKnight.model.enemies;

import com.HollowKnight.model.player.Player;
import com.HollowKnight.model.world.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import java.util.Random;

public class FalseKnight extends Enemy {
    public enum State {
        WAITING, INTRO_DROP, IDLE, RUN, TURN,
        ATTACK_ANTIC, ATTACK, ATTACK_RECOVER,
        JUMP_ANTIC, JUMP, JUMP_ATTACK, LAND,
        STUN, STUN_RECOVER,
        DEATH_FALL, DEATH_HIT, DEATH_LAND, DEAD, JUMP_BACK
    }

    private State currentState;
    private Player player;
    private World world;
    private Random random;

    private Animation<TextureRegion> idleAnim, runAnim, turnAnim;
    private Animation<TextureRegion> attackAnticAnim, attackAnim, attackRecoverAnim;
    private Animation<TextureRegion> jumpAnticAnim, jumpAnim, jumpAttackAnim, landAnim;
    private Animation<TextureRegion> stunAnim, stunRecoverAnim;
    private Animation<TextureRegion> deathFallAnim, deathHitAnim, deathLandAnim;

    private float moveSpeed = 180f;
    private float idleDelay = 0.7f;
    private boolean isPhase2 = false;
    private boolean hasBeenStunned = false;
    private int maxHealth;

    private State lastMove = State.IDLE;
    public float fadeAlpha = 1f;
    private float minX, maxX;
    private HuskHornhead triggerEnemy;

    private boolean hasDealtDamageThisAttack = false;

    private int recentHitsCount = 0;
    private float recentHitsTimer = 0f;

    public FalseKnight(float x, float y, float minX, float maxX, Player player, HuskHornhead triggerEnemy, World world) {
        super(x, y, 200, 250, 12);
        this.maxHealth = 12;
        this.health = maxHealth;
        this.player = player;
        this.world = world;
        this.minX = minX;
        this.maxX = maxX;
        this.triggerEnemy = triggerEnemy;
        this.random = new Random();
        this.facingRight = false;

        this.position.y = 1000f;
        this.currentState = State.INTRO_DROP;
        this.velocity.y = -800f;

        loadAnimations();
    }

    private void loadAnimations() {
        idleAnim = createAnim("player/False_knight/Idle.png", 1, 5, 0.15f);
        runAnim = createAnim("player/False_knight/Run.png", 1, 5, 0.09f);
        turnAnim = createAnim("player/False_knight/Turn.png", 1, 2, 0.08f);

        attackAnticAnim = createAnim("player/False_knight/Attack Antic.png", 1, 6, 0.08f);
        attackAnim = createAnim("player/False_knight/Attack.png", 1, 3, 0.09f);
        attackRecoverAnim = createAnim("player/False_knight/Attack Recover.png", 1, 5, 0.08f);

        jumpAnticAnim = createAnim("player/False_knight/Jump Antic.png", 1, 3, 0.09f);
        jumpAnim = createAnim("player/False_knight/Jump.png", 1, 4, 0.1f);
        jumpAttackAnim = createAnim("player/False_knight/Jump Attack.png", 1, 8, 0.09f);
        landAnim = createAnim("player/False_knight/Land.png", 1, 5, 0.08f);

        stunAnim = createAnim("player/False_knight/Body.png", 1, 5, 0.3f);
        stunRecoverAnim = createAnim("player/False_knight/Stun Recover.png", 1, 6, 0.12f);

        deathFallAnim = createAnim("player/False_knight/DeathFall.png", 1, 3, 0.1f);
        deathHitAnim = createAnim("player/False_knight/DeathHit.png", 1, 3, 0.1f);
        deathLandAnim = createAnim("player/False_knight/DeathLand.png", 1, 11, 0.1f);
    }

    private Animation<TextureRegion> createAnim(String path, int rows, int cols, float duration) {
        Texture sheet = new Texture(path);
        TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth() / cols, sheet.getHeight() / rows);
        TextureRegion[] frames = new TextureRegion[cols * rows];
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) { frames[index++] = tmp[i][j]; }
        }
        return new Animation<>(duration, frames);
    }

    @Override
    public void takeDamage(int damage) {
        if (currentState == State.WAITING || currentState == State.INTRO_DROP || currentState == State.DEAD) return;

        super.takeDamage(damage);

        if (health <= 0) {
            com.HollowKnight.audio.AudioManager.getInstance().playBossFinalHit();
        } else {
            recentHitsCount++;
            recentHitsTimer = 2.0f;
        }
    }


    @Override
    public boolean isDying() {
        return currentState == State.DEATH_FALL || currentState == State.DEATH_HIT || currentState == State.DEATH_LAND || currentState == State.DEAD;
    }

    @Override
    public void update(float delta) {
        if (hitCooldown > 0) hitCooldown -= delta;
        stateTime += delta;

        if (recentHitsTimer > 0) {
            recentHitsTimer -= delta;
            if (recentHitsTimer <= 0) recentHitsCount = 0;
        }

        if (currentState == State.WAITING || currentState == State.INTRO_DROP) {
            bounds.set(-1000, -1000, 0, 0);
        } else {
            float hitboxWidth = 120f;
            float hitboxHeight = 220f;
            float offsetX = 40f;
            bounds.set(position.x + offsetX, position.y, hitboxWidth, hitboxHeight);
        }

        if (health <= 0 && !isDying()) {
            changeState(State.DEATH_FALL);
            return;
        }

        if (!hasBeenStunned && health <= maxHealth / 2 && !isDying() && currentState != State.STUN && currentState != State.STUN_RECOVER) {
            hasBeenStunned = true;
            isPhase2 = true;
            moveSpeed = 260f;
            idleDelay = 0.35f;
            velocity.set(0, 0);
            position.y = 64;
            changeState(State.STUN);
            return;
        }

        float distanceToPlayer = Math.abs(player.position.x - this.position.x);
        boolean playerIsRight = player.position.x > this.position.x;

        switch (currentState) {
            case WAITING:
                position.y = 1000f;
                if (triggerEnemy != null && triggerEnemy.fadeAlpha <= 0) {
                    changeState(State.INTRO_DROP);
                    velocity.y = -500f;
                }
                break;

            case INTRO_DROP:
                position.y += velocity.y * delta;
                velocity.y -= 1200f * delta;

                if (position.y <= 64) {
                    position.y = 64;
                    velocity.set(0, 0);
                    world.screenShakeTime = 0.8f;
                    com.HollowKnight.audio.AudioManager.getInstance().playBossMusic();
                    changeState(State.LAND);
                }
                break;

            case IDLE:
                if (stateTime > idleDelay) {
                    if (playerIsRight != facingRight) changeState(State.TURN);
                    else decideNextMove(distanceToPlayer);
                }
                break;

            case TURN:
                if (turnAnim.isAnimationFinished(stateTime)) {
                    facingRight = !facingRight;
                    changeState(State.IDLE);
                }
                break;

            case RUN:
                velocity.x = facingRight ? moveSpeed : -moveSpeed;
                position.x += velocity.x * delta;
                if (distanceToPlayer < 140 || position.x <= minX || position.x >= maxX) {
                    velocity.x = 0;
                    changeState(State.ATTACK_ANTIC);
                }
                break;

            case ATTACK_ANTIC:
                if (attackAnticAnim.isAnimationFinished(stateTime)) changeState(State.ATTACK);
                break;

            case ATTACK:
                if (stateTime > 0.12f && !hasDealtDamageThisAttack) {
                    checkClubDamage();
                }
                if (attackAnim.isAnimationFinished(stateTime)) {
                    world.screenShakeTime = 0.35f;
                    changeState(State.ATTACK_RECOVER);
                }
                break;

            case ATTACK_RECOVER:
                if (attackRecoverAnim.isAnimationFinished(stateTime)) changeState(State.IDLE);
                break;

            case JUMP_ANTIC:
                if (jumpAnticAnim.isAnimationFinished(stateTime)) {
                    velocity.y = 420f;
                    velocity.x = facingRight ? moveSpeed * 1.15f : -moveSpeed * 1.15f;
                    changeState(State.JUMP);
                }
                break;

            case JUMP:
                position.x += velocity.x * delta;
                position.y += velocity.y * delta;
                velocity.y -= 850f * delta;

                if (position.y <= 64) {
                    position.y = 64;
                    velocity.set(0, 0);
                    world.screenShakeTime = 0.25f;
                    changeState(State.LAND);
                }
                break;

            case JUMP_BACK:
                position.x += velocity.x * delta;
                position.y += velocity.y * delta;
                velocity.y -= 850f * delta;

                if (position.y <= 64) {
                    position.y = 64;
                    velocity.set(0, 0);
                    changeState(State.IDLE);
                }
                break;

            case JUMP_ATTACK:
                position.x += velocity.x * delta;
                position.y += velocity.y * delta;
                velocity.y -= 950f * delta;

                if (stateTime > 0.18f && !hasDealtDamageThisAttack) checkClubDamage();

                if (position.y <= 64) {
                    position.y = 64;
                    velocity.set(0, 0);
                    world.screenShakeTime = 0.5f;
                    changeState(State.LAND);
                }
                break;

            case LAND:
                if (landAnim.isAnimationFinished(stateTime)) changeState(State.IDLE);
                break;

            case STUN:
                position.y = 64;
                velocity.set(0, 0);
                if (stateTime > 3.2f) changeState(State.STUN_RECOVER);
                break;

            case STUN_RECOVER:
                if (stunRecoverAnim.isAnimationFinished(stateTime)) changeState(State.IDLE);
                break;

            case DEATH_FALL:
                if (deathFallAnim.isAnimationFinished(stateTime)) changeState(State.DEATH_HIT);
                break;
            case DEATH_HIT:
                if (deathHitAnim.isAnimationFinished(stateTime)) changeState(State.DEATH_LAND);
                break;
            case DEATH_LAND:
                if (deathLandAnim.isAnimationFinished(stateTime)) {
                    fadeAlpha -= delta;
                    if (fadeAlpha <= 0) currentState = State.DEAD;
                }
                break;
        }

        if (position.x < minX) {
            position.x = minX;
            velocity.x = 0;
        }
        if (position.x > maxX) {
            position.x = maxX;
            velocity.x = 0;
        }
    }

    private void checkClubDamage() {
        float clubRange = 150f;
        float clubX = facingRight ? position.x + 90 : position.x - 40;
        Rectangle clubHitbox = new Rectangle(clubX, position.y, clubRange, 160f);

        if (clubHitbox.overlaps(player.bounds)) {
            player.takeDamage(1);
            hasDealtDamageThisAttack = true;
        }
    }

    private void decideNextMove(float distance) {
        State nextMove;

        if (distance < 180) {
            if (recentHitsCount >= 3 || random.nextInt(100) < 15) {
                nextMove = State.JUMP_BACK;
                recentHitsCount = 0;
                velocity.y = 380f;
                velocity.x = facingRight ? -moveSpeed * 0.9f : moveSpeed * 0.9f;
            } else {
                nextMove = State.ATTACK_ANTIC;
            }
        }
        else if (distance < 450) {
            nextMove = random.nextInt(100) < 65 ? State.RUN : State.JUMP_ANTIC;
        }
        else {
            nextMove = random.nextInt(100) < 55 ? State.RUN : State.JUMP_ANTIC;
            if (isPhase2 && random.nextInt(100) < 45) {
                nextMove = State.JUMP_ATTACK;
                velocity.y = 480f;
                velocity.x = facingRight ? moveSpeed * 1.3f : -moveSpeed * 1.3f;
            }
        }

        if (nextMove == lastMove && nextMove != State.ATTACK_ANTIC && nextMove != State.JUMP_BACK) {
            nextMove = (nextMove == State.RUN) ? State.JUMP_ANTIC : State.RUN;
        }

        lastMove = nextMove;
        changeState(nextMove);
    }

    private void changeState(State newState) {
        this.currentState = newState;
        this.stateTime = 0;
        this.hasDealtDamageThisAttack = false;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (currentState == State.DEAD) return;

        TextureRegion frame = idleAnim.getKeyFrame(0);

        switch (currentState) {
            case WAITING: frame = idleAnim.getKeyFrame(0); break;
            case INTRO_DROP: frame = jumpAnim.getKeyFrame(stateTime, false); break;
            case IDLE: frame = idleAnim.getKeyFrame(stateTime, true); break;
            case TURN: frame = turnAnim.getKeyFrame(stateTime, false); break;
            case RUN: frame = runAnim.getKeyFrame(stateTime, true); break;
            case ATTACK_ANTIC: frame = attackAnticAnim.getKeyFrame(stateTime, false); break;
            case ATTACK: frame = attackAnim.getKeyFrame(stateTime, false); break;
            case ATTACK_RECOVER: frame = attackRecoverAnim.getKeyFrame(stateTime, false); break;
            case JUMP_ANTIC: frame = jumpAnticAnim.getKeyFrame(stateTime, false); break;
            case JUMP: frame = jumpAnim.getKeyFrame(stateTime, true); break;
            case JUMP_ATTACK: frame = jumpAttackAnim.getKeyFrame(stateTime, true); break;
            case LAND: frame = landAnim.getKeyFrame(stateTime, false); break;
            case STUN: frame = stunAnim.getKeyFrame(stateTime, true); break;
            case STUN_RECOVER: frame = stunRecoverAnim.getKeyFrame(stateTime, false); break;
            case DEATH_FALL: frame = deathFallAnim.getKeyFrame(stateTime, false); break;
            case DEATH_HIT: frame = deathHitAnim.getKeyFrame(stateTime, false); break;
            case DEATH_LAND: frame = deathLandAnim.getKeyFrame(stateTime, false); break;
            case JUMP_BACK: frame = jumpAnim.getKeyFrame(stateTime, false); break;
        }

        if (frame.isFlipX() != facingRight) {
            frame.flip(true, false);
        }
        if (hitCooldown > 0 && hitCooldown % 0.2f > 0.1f && !isDying()) batch.setColor(1, 0.5f, 0.5f, 1);
        else batch.setColor(1, 1, 1, fadeAlpha);

        float drawX = position.x - 50f;
        float drawY = position.y;
        float drawWidth = 300f;
        float drawHeight = 250f;

        batch.draw(frame, drawX, drawY, drawWidth, drawHeight);
        batch.setColor(Color.WHITE);
    }
}
