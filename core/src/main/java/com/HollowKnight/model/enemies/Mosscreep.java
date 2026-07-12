package com.HollowKnight.model.enemies;

import com.HollowKnight.model.player.Player;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Mosscreep extends Enemy {

    private Animation<TextureRegion> walkAnim;
    private Animation<TextureRegion> turnAnim;
    private Animation<TextureRegion> deathLandAnim;
    private Animation<TextureRegion> deathAirAnim;

    private float turnTimer;
    private float deathTimer;
    private boolean isDyingInAir;

    private Player player;

    private float patrolSpeed = 30f;
    private float runSpeed = 80f;
    private float scareDistance = 115f;
    private boolean isScared = false;
    private boolean onGround = true;
    private float gravity = 400f;
    private float floorY;

    public float fadeAlpha = 1f;
    private float minX;
    private float maxX;

    public Mosscreep(float x, float y, Player player, float minX, float maxX) {
        super(x, y, 32, 32, 3);
        this.player = player;
        this.minX = minX;
        this.maxX = maxX;
        this.velocity.x = patrolSpeed;
        this.facingRight = true;
        this.floorY = y;

        walkAnim = createAnimation(new Texture("player/mosscreep/Walk.png"), 1, 3, 0.1f);
        turnAnim = createAnimation(new Texture("player/mosscreep/Turn.png"), 1, 3, 0.15f);
        deathLandAnim = createAnimation(new Texture("player/mosscreep/Death Land.png"), 1, 2, 0.15f);
        deathAirAnim = createAnimation(new Texture("player/mosscreep/Death Air.png"), 1, 4, 0.15f);
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
        if (hitCooldown > 0) hitCooldown -= delta;

        if (isDying()) {
            deathTimer += delta;
            fadeAlpha -= delta * 0.5f;

            if (isDyingInAir) {
                velocity.y -= gravity * delta;
                position.y += velocity.y * delta;
                if (position.y <= floorY) {
                    position.y = floorY;
                    isDyingInAir = false;
                }
            }
            return;
        }

        float distToPlayer = Math.abs(player.position.x - this.position.x);

        if (distToPlayer < scareDistance) {
            isScared = true;
            if (player.position.x < this.position.x) {
                velocity.x = runSpeed;
                facingRight = true;
            } else {
                velocity.x = -runSpeed;
                facingRight = false;
            }

            if (onGround && Math.random() < 0.02) {
                velocity.y = 150f;
                onGround = false;
            }
        } else {
            isScared = false;
            velocity.x = facingRight ? patrolSpeed : -patrolSpeed;
        }

        if (!onGround) {
            velocity.y -= gravity * delta;
        }

        position.add(velocity.x * delta, velocity.y * delta);

        if (position.x <= minX) {
            position.x = minX;
            facingRight = true;
        } else if (position.x >= maxX) {
            position.x = maxX;
            facingRight = false;
        }

        if (position.y <= floorY) {
            position.y = floorY;
            velocity.y = 0;
            onGround = true;
        }
        bounds.setPosition(position.x, position.y);
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame;

        if (isDying()) {
            if (isDyingInAir) {
                currentFrame = deathAirAnim.getKeyFrame(deathTimer, false);
            } else {
                currentFrame = deathLandAnim.getKeyFrame(deathTimer, false);
            }
        } else if (turnTimer > 0) {
            turnTimer -= com.badlogic.gdx.Gdx.graphics.getDeltaTime();
            currentFrame = turnAnim.getKeyFrame(stateTime, false);
        } else {
            float animSpeed = isScared ? stateTime * 2f : stateTime;
            currentFrame = walkAnim.getKeyFrame(animSpeed, true);
        }

        if (currentFrame != null) {
            if (facingRight != currentFrame.isFlipX()) {
                currentFrame.flip(true, false);
            }

            float oldR = batch.getColor().r;
            float oldG = batch.getColor().g;
            float oldB = batch.getColor().b;
            float oldA = batch.getColor().a;

            if (hitCooldown > 0 && hitCooldown % 0.2f > 0.1f && !isDying()) {
                batch.setColor(1, 0.5f, 0.5f, 1);
            } else {
                batch.setColor(oldR, oldG, oldB, Math.max(0, fadeAlpha));
            }

            batch.draw(currentFrame, position.x - 8, position.y, 48, 48);

            batch.setColor(oldR, oldG, oldB, oldA);
        }
    }


    @Override
    public boolean isDying() {
        return health <= 0;
    }

    @Override
    public void takeDamage(int damage) {
        if (!isDying() && hitCooldown <= 0) {
            super.takeDamage(damage);
            if (health <= 0) {
                deathTimer = 0;
                isDyingInAir = !onGround;
            }
        }
    }
}
