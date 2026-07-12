package com.HollowKnight.model.player;

import com.HollowKnight.model.Inventory;
import com.HollowKnight.model.Settings;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {
    public Vector2 position;
    public Vector2 velocity;
    public Rectangle bounds;

    public boolean isDead = false;

    public static final float WIDTH = 30;
    public static final float HEIGHT = 45;

    public int maxHealth = 5;
    public int health = maxHealth;

    public static final int MAX_SOUL = 99;
    public int soul = 0;
    private static final int SOUL_PER_HIT = 3;

    private float invincibleTimer = 0;
    private static final float INVINCIBLE_DURATION = 1.0f;

    public boolean isGodMode = false;
    public boolean isNoclip = false;

    public boolean isGrounded;
    public boolean isFacingRight = true;
    private static final float GRAVITY = -800f;
    private static final float JUMP_SPEED = 450f;
    private static final float MOVE_SPEED = 250f;

    public boolean hasDoubleJumped = false;

    public boolean isDashing = false;
    private float dashTimer = 0;
    private float dashCooldown = 0;
    private static final float DASH_DURATION = 0.25f;
    private static final float DASH_SPEED = 600f;

    public boolean isAttacking = false;
    private float attackTimer = 0;
    private static final float ATTACK_DURATION = 0.3f;
    public enum AttackType { NORMAL, UP, DOWN }
    public AttackType currentAttackType = AttackType.NORMAL;

    public boolean isLookingUp = false;
    public boolean isLookingDown = false;

    public boolean hasUsedEmergencyHeal = false;

    public enum State {
        IDLE, RUNNING, JUMPING, FALLING, FOCUS,
        LOOK_UP, LOOK_DOWN, DOUBLE_JUMPING, DASHING,
        ATTACK_NORMAL, ATTACK_UP, ATTACK_DOWN, DEATH }

    public State currentState;
    public State previousState;
    private float stateTimer;

    private boolean useAlternateSlash = false;

    private Texture idleSheet, runSheet, airborneSheet;
    private Texture lookUpSheet, lookDownSheet, doubleJumpSheet, dashSheet;
    private Texture slashSheet, slashAltSheet, upSlashSheet, downSlashSheet;
    private Texture slashEffectSheet, slashEffectAltSheet;
    private Texture deathSheet;
    private Texture focusStartSheet, focusLoopSheet, focusGetSheet, focusEndSheet;

    private Animation<TextureRegion> idleAnimation, runAnimation, jumpAnimation, fallAnimation;
    private Animation<TextureRegion> lookUpAnim, lookDownAnim, doubleJumpAnim, dashAnim;
    private Animation<TextureRegion> slashAnim, slashAltAnim, upSlashAnim, downSlashAnim;
    private Animation<TextureRegion> slashEffectAnim, slashEffectAltAnim;
    private Animation<TextureRegion> deathAnim;
    private Animation<TextureRegion> focusStartAnim, focusLoopAnim, focusGetAnim, focusEndAnim;

    public boolean isFocusing = false;
    public boolean focusSuccess = false;
    public float focusTimer = 0f;
    public float focusEffectTimer = 0f;
    private static final float FOCUS_EFFECT_DURATION = 0.4f;
    private static final int FOCUS_SOUL_COST = 33;
    private static final float FOCUS_DURATION = 1.5f;

    public int geo = 0;
    public boolean hasHitEnemyThisSwing = false;

    private float coyoteTimeCounter = 0;
    private static final float COYOTE_TIME = 0.15f;

    private Texture sharpShadowDashSheet;
    private Animation<TextureRegion> sharpShadowDashAnim;

    public Player(float x, float y) {
        position = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        bounds = new Rectangle(x, y, WIDTH, HEIGHT);

        currentState = State.IDLE;
        previousState = State.IDLE;
        stateTimer = 0;

        loadAnimations();
    }

    private void loadAnimations() {
        idleSheet = new Texture("player/Idle.png");
        runSheet = new Texture("player/Run.png");
        airborneSheet = new Texture("player/Airborne.png");
        lookUpSheet = new Texture("player/LookUp.png");
        lookDownSheet = new Texture("player/LookDown.png");
        doubleJumpSheet = new Texture("player/Double Jump.png");
        dashSheet = new Texture("player/Dash.png");
        slashSheet = new Texture("player/Slash.png");
        upSlashSheet = new Texture("player/UpSlash.png");
        downSlashSheet = new Texture("player/DownSlash.png");
        slashAltSheet = new Texture("player/SlashAlt.png");
        slashEffectSheet = new Texture("player/SlashEffect.png");
        slashEffectAltSheet = new Texture("player/SlashEffectAlt.png");
        deathSheet = new Texture("player/Death.png");
        focusStartSheet = new Texture("player/Focus Start.png");
        focusLoopSheet = new Texture("player/Focus.png");
        focusGetSheet = new Texture("player/Focus Get.png");
        focusEndSheet = new Texture("player/Focus End.png");
        sharpShadowDashSheet = new Texture("player/ShadowBall.png");

        focusStartAnim = createAnimation(focusStartSheet, 1, 3, 0.1f);
        focusLoopAnim = createAnimation(focusLoopSheet, 1, 4, 0.1f);
        focusGetAnim = createAnimation(focusGetSheet, 1, 6, 0.1f);
        focusEndAnim = createAnimation(focusEndSheet, 1, 3, 0.1f);

        idleAnimation = createAnimation(idleSheet, 1, 9, 0.1f);
        runAnimation = createAnimation(runSheet, 1, 13, 0.08f);
        jumpAnimation = createAnimation(airborneSheet, 1, 12, 0.1f);
        fallAnimation = jumpAnimation;

        lookUpAnim = createAnimation(lookUpSheet, 1, 6, 0.08f);
        lookDownAnim = createAnimation(lookDownSheet, 1, 6, 0.08f);
        doubleJumpAnim = createAnimation(doubleJumpSheet, 1, 8, 0.1f);
        dashAnim = createAnimation(dashSheet, 1, 12, 0.06f);
        slashAnim = createAnimation(slashSheet, 1, 5, 0.08f);
        upSlashAnim = createAnimation(upSlashSheet, 1, 5, 0.08f);
        downSlashAnim = createAnimation(downSlashSheet, 1, 5, 0.08f);
        slashAltAnim = createAnimation(slashAltSheet, 1, 5, 0.08f);
        slashEffectAnim = createAnimation(slashEffectSheet, 1, 6, 0.08f);
        slashEffectAltAnim = createAnimation(slashEffectAltSheet, 1, 6, 0.08f);
        deathAnim = createAnimation(deathSheet, 1, 18, 0.15f);
        sharpShadowDashAnim = createAnimation(sharpShadowDashSheet, 1, 6, 0.04f);

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

    public void update(float delta) {
        handleInput(delta);

        if (getState() == State.RUNNING) {
            com.HollowKnight.audio.AudioManager.getInstance().playHeroRun();
        } else {
            com.HollowKnight.audio.AudioManager.getInstance().stopHeroRun();
        }

        if (!isNoclip) {
            velocity.y += GRAVITY * delta;
            if (!isGrounded) {
                coyoteTimeCounter -= delta;
            }
            if (velocity.y < -600) velocity.y = -600;
        } else {
            velocity.y = 0;
        }

        position.x += velocity.x * delta;
        position.y += velocity.y * delta;
        bounds.setPosition(position.x, position.y);

        if (invincibleTimer > 0) invincibleTimer -= delta;
        if (dashCooldown > 0) dashCooldown -= delta;

        if (!isFocusing && focusEffectTimer < FOCUS_EFFECT_DURATION) {
            focusEffectTimer += delta;
        }

        if (isFocusing && !isGrounded) {
            cancelFocus();
        }
    }
    private void handleInput(float delta) {
        if (isDead) {
            velocity.x = 0;
            return;
        }
        Settings set = Settings.getInstance();

        boolean jumpJustPressed = Gdx.input.isKeyJustPressed(set.getKeyJump());
        boolean dashJustPressed = Gdx.input.isKeyJustPressed(set.getKeyDash());
        boolean attackJustPressed = Gdx.input.isKeyJustPressed(set.getKeyAttack());

        boolean focusPressed = Gdx.input.isKeyPressed(Input.Keys.F);
        boolean upPressed = Gdx.input.isKeyPressed(set.getKeyMoveUp()) || Gdx.input.isKeyPressed(Input.Keys.W);
        boolean downPressed = Gdx.input.isKeyPressed(set.getKeyMoveDown()) || Gdx.input.isKeyPressed(Input.Keys.S);
        boolean leftPressed = Gdx.input.isKeyPressed(set.getKeyMoveLeft()) || Gdx.input.isKeyPressed(Input.Keys.A);
        boolean rightPressed = Gdx.input.isKeyPressed(set.getKeyMoveRight()) || Gdx.input.isKeyPressed(Input.Keys.D);


        if (isNoclip) {
            float noclipSpeed = 500f;
            velocity.x = 0;
            velocity.y = 0;
            if (leftPressed) { velocity.x = -noclipSpeed; isFacingRight = false; }
            if (rightPressed) { velocity.x = noclipSpeed; isFacingRight = true; }
            if (upPressed) velocity.y = noclipSpeed;
            if (downPressed) velocity.y = -noclipSpeed;
            return;
        }

        isLookingUp = false;
        isLookingDown = false;

        if (focusPressed && isGrounded && soul >= FOCUS_SOUL_COST && health < maxHealth && !isAttacking && !isDashing) {
            if (!isFocusing) {
                isFocusing = true;
                focusTimer = 0;
                velocity.x = 0;
            }
        } else {
            cancelFocus();
        }
        float currentFocusDur = Inventory.getInstance().isCharmEquipped("quick_focus") ? FOCUS_DURATION / 2f : FOCUS_DURATION;

        if (isFocusing) {
            velocity.x = 0;
            focusTimer += delta;

            if (focusTimer >= currentFocusDur) {
                heal(1);
                soul -= FOCUS_SOUL_COST;
                if (soul < 0) soul = 0;

                isFocusing = false;
                focusSuccess = true;
                focusEffectTimer = 0f;
                focusTimer = 0f;
            }
            return;
        }

        float currentDashCooldown = Inventory.getInstance().isCharmEquipped("dashmaster") ? 0.5f : 1.0f;

        if (dashJustPressed && !isDashing && dashCooldown <= 0) {
            isDashing = true;
            dashTimer = DASH_DURATION;
            dashCooldown = currentDashCooldown;
            velocity.y = 0;
            com.HollowKnight.audio.AudioManager.getInstance().playHeroDash();
        }

        float currentDashSpeed = Inventory.getInstance().isCharmEquipped("sharp_shadow") ? DASH_SPEED * 1.2f : DASH_SPEED;
        if (isDashing) {
            velocity.x = isFacingRight ? currentDashSpeed : -currentDashSpeed;
            dashTimer -= delta;
            if (dashTimer <= 0) isDashing = false;
            return;
        }

        float currentAttackDur = Inventory.getInstance().isCharmEquipped("quick_slash") ? 0.15f : ATTACK_DURATION;

        if (attackJustPressed && !isAttacking) {
            isAttacking = true;
            hasHitEnemyThisSwing = false;
            attackTimer = ATTACK_DURATION;
            stateTimer = 0;

            com.HollowKnight.audio.AudioManager.getInstance().playAttackSound();

            if (upPressed) {
                currentAttackType = AttackType.UP;
            } else if (downPressed && !isGrounded) {
                currentAttackType = AttackType.DOWN;
            } else {
                currentAttackType = AttackType.NORMAL;
                useAlternateSlash = !useAlternateSlash;
            }
        }

        if (isAttacking) {
            attackTimer -= delta;
            if (attackTimer <= 0) isAttacking = false;
        }

        float moveX = 0;
        if (leftPressed && !focusPressed) { moveX = -1; isFacingRight = false; }
        if (rightPressed) { moveX = 1; isFacingRight = true; }
        velocity.x = moveX * MOVE_SPEED;

        if (moveX == 0 && isGrounded && !isAttacking) {
            if (upPressed) isLookingUp = true;
            if (downPressed) isLookingDown = true;
        }

        if (jumpJustPressed) {
            if (isGrounded || coyoteTimeCounter > 0f) {
                velocity.y = JUMP_SPEED;
                isGrounded = false;
                coyoteTimeCounter = 0f;
                hasDoubleJumped = false;
                com.HollowKnight.audio.AudioManager.getInstance().playHeroJump();
            } else if (!hasDoubleJumped) {
                velocity.y = JUMP_SPEED * 0.9f;
                hasDoubleJumped = true;
                stateTimer = 0;
                com.HollowKnight.audio.AudioManager.getInstance().playHeroJump();
            }
        }
    }

    private void cancelFocus() {
        if (isFocusing) {
            isFocusing = false;
            focusSuccess = false;
            focusEffectTimer = 0f;
            focusTimer = 0f;
        }
    }

    public void takeDamage(int damage) {
        if (invincibleTimer > 0 || isDead) return;
        com.HollowKnight.audio.AudioManager.getInstance().playHeroDamage();
        health -= damage;

        cancelFocus();

        if (health <= 0) {
            health = 0;
            die();
        } else {
            invincibleTimer = INVINCIBLE_DURATION;
        }
    }

    private void die() {
        if (!isDead) {
            com.HollowKnight.audio.AudioManager.getInstance().playHeroDeath();
            com.HollowKnight.model.AchievementManager.getInstance().incrementDeathCount();

            isDead = true;
            stateTimer = 0;
            isAttacking = false;
            isDashing = false;
            cancelFocus();
        }
    }

    public void heal(int amount) {
        health += amount;
        if (health > maxHealth) health = maxHealth;
    }

    public void addSoul(int amount) {
        soul += amount;
        if (soul > MAX_SOUL) soul = MAX_SOUL;
    }

    public void addSoulOnHit() {
        int amount = SOUL_PER_HIT;
        if (com.HollowKnight.model.Inventory.getInstance().isCharmEquipped("soul_catcher")) {
            amount += 3;
        }
        addSoul(amount);
    }

    public boolean isInvincible() {
        return invincibleTimer > 0;
    }

    public boolean isFocusing() {
        return isFocusing;
    }

    public float getFocusProgress() {
        if (!isFocusing) return 0;
        return focusTimer / FOCUS_DURATION;
    }

    public State getState() {
        if (isDead) return State.DEATH;
        if (isNoclip) return State.IDLE;
        if (isDashing) return State.DASHING;
        if (isFocusing) return State.FOCUS;

        if (isAttacking) {
            if (currentAttackType == AttackType.UP) return State.ATTACK_UP;
            if (currentAttackType == AttackType.DOWN) return State.ATTACK_DOWN;
            return State.ATTACK_NORMAL;
        }

        if (!isGrounded) {
            if (hasDoubleJumped && velocity.y > 0 && stateTimer < 0.4f) {
                return State.DOUBLE_JUMPING;
            }
            if (velocity.y > 0) return State.JUMPING;
            if (velocity.y < -20f) return State.FALLING;
        }

        if (velocity.x != 0) return State.RUNNING;
        if (isLookingUp) return State.LOOK_UP;
        if (isLookingDown) return State.LOOK_DOWN;

        return State.IDLE;
    }

    public TextureRegion getFrame(float delta) {
        currentState = getState();
        TextureRegion region;

        switch (currentState) {
            case DEATH:          region = deathAnim.getKeyFrame(stateTimer, false);break;
            case DASHING:
                if (com.HollowKnight.model.Inventory.getInstance().isCharmEquipped("sharp_shadow")) {
                    region = sharpShadowDashAnim.getKeyFrame(stateTimer, false);
                } else {
                    region = dashAnim.getKeyFrame(stateTimer, false);
                }
                break;            case DOUBLE_JUMPING: region = doubleJumpAnim.getKeyFrame(stateTimer, false); break;
            case ATTACK_NORMAL:  region = useAlternateSlash ? slashAltAnim.getKeyFrame(stateTimer, false) : slashAnim.getKeyFrame(stateTimer, false); break;
            case ATTACK_UP:      region = upSlashAnim.getKeyFrame(stateTimer, false); break;
            case ATTACK_DOWN:    region = downSlashAnim.getKeyFrame(stateTimer, false); break;
            case LOOK_UP:        region = lookUpAnim.getKeyFrame(stateTimer, true); break;
            case LOOK_DOWN:      region = lookDownAnim.getKeyFrame(stateTimer, true); break;
            case JUMPING:        region = jumpAnimation.getKeyFrame(stateTimer, false); break;
            case FALLING:        region = fallAnimation.getKeyFrame(stateTimer, true); break;
            case RUNNING:        region = runAnimation.getKeyFrame(stateTimer, true); break;
            case IDLE:
            default:
                region = idleAnimation.getKeyFrame(stateTimer, true);
                break;
        }

        if (currentState == State.DASHING && com.HollowKnight.model.Inventory.getInstance().isCharmEquipped("sharp_shadow")) {
            if (isFacingRight && region.isFlipX()) {
                region.flip(true, false);
            } else if (!isFacingRight && !region.isFlipX()) {
                region.flip(true, false);
            }
        } else {
        if (isFacingRight && !region.isFlipX()) {
            region.flip(true, false);
        } else if (!isFacingRight && region.isFlipX()) {
            region.flip(true, false);
        }
        }

        stateTimer = currentState == previousState ? stateTimer + delta : 0;
        previousState = currentState;

        return region;
    }

    public void render(SpriteBatch batch, float delta) {
        if (isInvincible() && (int)(invincibleTimer * 10) % 2 == 0) return;

        TextureRegion currentFrame = getFrame(delta);
        float drawWidth = 130;
        float drawHeight = 120;

        if (currentState == State.DASHING && com.HollowKnight.model.Inventory.getInstance().isCharmEquipped("sharp_shadow")) {
            drawWidth = 80;
            drawHeight = 80;
        }

        int drawX = (int) (position.x - (drawWidth - WIDTH) / 2);
        int drawY = (int) (position.y - 5);

        //  نشانگر گادمد
        if (isGodMode) {
            batch.setColor(com.badlogic.gdx.graphics.Color.GOLD);
        }

        batch.draw(currentFrame, drawX, drawY, drawWidth, drawHeight);

        if (isGodMode) {
            batch.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        }
        TextureRegion focusFrame = null;

        if (isFocusing) {
            if (focusTimer < focusStartAnim.getAnimationDuration()) {
                focusFrame = focusStartAnim.getKeyFrame(focusTimer, false);
            } else {
                float loopTime = focusTimer - focusStartAnim.getAnimationDuration();
                focusFrame = focusLoopAnim.getKeyFrame(loopTime, true);
            }
        } else if (focusEffectTimer < FOCUS_EFFECT_DURATION) {
            if (focusSuccess) {
                focusFrame = focusGetAnim.getKeyFrame(focusEffectTimer, false);
            } else {
                focusFrame = focusEndAnim.getKeyFrame(focusEffectTimer, false);
            }
        }

        if (focusFrame != null) {
            if (isFacingRight && !focusFrame.isFlipX()) {
                focusFrame.flip(true, false);
            } else if (!isFacingRight && focusFrame.isFlipX()) {
                focusFrame.flip(true, false);
            }

            float focusDrawWidth = 150;
            float focusDrawHeight = 150;

            float focusDrawX = position.x + (WIDTH / 2f) - (focusDrawWidth / 2f);
            float focusDrawY = position.y - 15;

            batch.draw(focusFrame, focusDrawX, focusDrawY, focusDrawWidth, focusDrawHeight);
        }

        if (isAttacking && currentAttackType == AttackType.NORMAL) {
            TextureRegion effectFrame = useAlternateSlash ?
                slashEffectAltAnim.getKeyFrame(stateTimer, false) :
                slashEffectAnim.getKeyFrame(stateTimer, false);

            if (isFacingRight && effectFrame.isFlipX()) {
                effectFrame.flip(true, false);
            } else if (!isFacingRight && !effectFrame.isFlipX()) {
                effectFrame.flip(true, false);
            }

            float effectWidth = 140;
            float effectHeight = 140;

            float offsetX = isFacingRight ? 30 : -30 - (effectWidth - WIDTH);
            float effectX = position.x + offsetX;
            float effectY = position.y - 10;

            batch.draw(effectFrame, (int)effectX, (int)effectY, effectWidth, effectHeight);
        }
    }

    public void setGrounded(boolean grounded) {
        if (!isGrounded && grounded && velocity.y < -200f) {
            com.HollowKnight.audio.AudioManager.getInstance().playHeroLand();
        }

        isGrounded = grounded;
        if (grounded) {
            velocity.y = 0;
            hasDoubleJumped = false;
            coyoteTimeCounter = COYOTE_TIME;
        }
    }

    public void reset() {
        health = maxHealth;
        soul = 0;
        invincibleTimer = 0;
        isDead = false;
        stateTimer = 0;
        hasUsedEmergencyHeal = false;
        cancelFocus();
    }

    public Rectangle getAttackBounds() {
        if (!isAttacking) return null;

        float attackRange = 50f;
        float attackX = isFacingRight ? position.x + WIDTH : position.x - attackRange;
        return new Rectangle(attackX, position.y, attackRange, HEIGHT);
    }


    public void dispose() {
        idleSheet.dispose();
        runSheet.dispose();
        airborneSheet.dispose();
        lookUpSheet.dispose();
        lookDownSheet.dispose();
        doubleJumpSheet.dispose();
        dashSheet.dispose();
        slashSheet.dispose();
        upSlashSheet.dispose();
        downSlashSheet.dispose();
        slashAltSheet.dispose();
        slashEffectSheet.dispose();
        slashEffectAltSheet.dispose();
        deathSheet.dispose();
        focusStartSheet.dispose();
        focusLoopSheet.dispose();
        focusGetSheet.dispose();
        focusEndSheet.dispose();
        if (sharpShadowDashSheet != null) sharpShadowDashSheet.dispose();
    }

}
