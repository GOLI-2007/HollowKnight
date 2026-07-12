package com.HollowKnight.screen;

import com.HollowKnight.GameAssetManager;
import com.HollowKnight.audio.AudioManager;
import com.HollowKnight.controller.GameProcessor;
import com.HollowKnight.model.AchievementManager;
import com.HollowKnight.model.GameSave;
import com.HollowKnight.model.player.Player;
import com.HollowKnight.model.world.World;
import com.HollowKnight.ui.EndGameModal;
import com.HollowKnight.ui.HUD;
import com.HollowKnight.ui.InventoryModal;
import com.HollowKnight.utils.SaveManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen extends AbstractScreen {

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;

    private Player player;
    private World world;
    private GameAssetManager assetManager;

    private GameProcessor gameProcessor;
    private GameSave loadedSave;

    private static final float WORLD_WIDTH = 1280f;
    private static final float WORLD_HEIGHT = 800f;

    private HUD hud;

    private float fadeAlpha = 0f;
    private boolean isFadingOut = false;
    private float deathTimer = 0f;
    private static final float DEATH_ANIM_DURATION = 2.7f;

    private Texture rayTexture;
    private Array<AmbientParticle> particles;

    public GameScreen(GameSave save) {
        this.loadedSave = save;
    }

    public GameScreen() {
        this.loadedSave = null;
    }

    private InventoryModal inventoryModal;
    private boolean isPaused = false;

    private boolean isGameOver = false;
    private boolean isWinStatus = false;

    @Override
    public void show() {
        super.show();
        hud = new HUD();
        AudioManager.getInstance().playGameMusic();

        inventoryModal = new InventoryModal(GameAssetManager.skin);
        inventoryModal.setVisible(false);
        stage.addActor(inventoryModal);

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        shapeRenderer = new ShapeRenderer();

        assetManager = new GameAssetManager();
        assetManager.loadRoom01Assets();
        assetManager.loadRoom02Assets();
        assetManager.loadRoom03Assets();
        assetManager.loadRoom04Assets();
        assetManager.loadRoom05Assets();
        assetManager.getManager().finishLoading();

        player = new Player(100, 150);

        if (loadedSave != null) {
            player = new Player(loadedSave.getPlayerX(), loadedSave.getPlayerY());
            player.health = loadedSave.getHealth();
            player.maxHealth = loadedSave.getMaxHealth();
            player.geo = loadedSave.getGeo();

            world = new World(assetManager, player);
            world.buildRoom(loadedSave.getCurrentRoom(), assetManager);

            AchievementManager.getInstance().setPlayTime(loadedSave.getPlayTimeSeconds());

        } else {
            player = new Player(100, 65);
            world = new World(assetManager, player);
            world.buildRoom(1, assetManager);

            AchievementManager.getInstance().setPlayTime(0f);
        }

        rayTexture = new Texture(Gdx.files.internal("ui/gg_menu_radiant_0000_1.png"));
        particles = new Array<>();
        for (int i = 0; i < 35; i++) {
            particles.add(new AmbientParticle(camera.position.x, camera.position.y));
        }

        gameProcessor = new GameProcessor();

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(gameProcessor);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            isPaused = !isPaused;
            inventoryModal.setVisible(isPaused);
        }

        if (!isPaused) {
            update(delta);
        }
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(com.badlogic.gdx.Gdx.gl20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        world.render(batch);
        player.render(batch, delta);

        // رندر کردن ذرات معلق در هوا
        for (AmbientParticle p : particles) {
            batch.setColor(1, 1, 1, p.alpha);
            batch.draw(rayTexture, p.x, p.y,
                rayTexture.getWidth() / 2f, rayTexture.getHeight() / 2f,
                rayTexture.getWidth(), rayTexture.getHeight(),
                p.scale, p.scale, p.rotation,
                0, 0, rayTexture.getWidth(), rayTexture.getHeight(),
                false, false);
        }
        batch.setColor(1, 1, 1, 1);

        hud.render(batch, player);
        batch.end();

        if (fadeAlpha > 0) {
            Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA, com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);

            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, fadeAlpha);
            shapeRenderer.rect(camera.position.x - WORLD_WIDTH / 2, camera.position.y - WORLD_HEIGHT / 2, WORLD_WIDTH, WORLD_HEIGHT);
            shapeRenderer.end();

            Gdx.gl.glDisable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        }

        super.render(delta);
    }

    private void update(float delta) {
        handleCheatCodes();
        delta = Math.min(delta, 0.05f);
        AchievementManager.getInstance().updatePlayTime(delta);
        player.update(delta);
        world.update(player, delta);

        for (AmbientParticle p : particles) {
            p.update(delta, camera.position.x, camera.position.y);
        }

        if (player.isDead) {
            deathTimer += delta;
            if (deathTimer >= DEATH_ANIM_DURATION && !isFadingOut) {
                isFadingOut = true;
            }
        }

        if (!isGameOver) {
            if (player.isDead) {
                deathTimer += delta;
                if (deathTimer >= DEATH_ANIM_DURATION && !isFadingOut) {
                    isFadingOut = true;
                    isWinStatus = false;
                }
            } else if (world.isBossDefeated && !isFadingOut) {
                isFadingOut = true;
                isWinStatus = true;
            }
        }

        if (isFadingOut) {
            fadeAlpha += delta * 0.7f;
            if (fadeAlpha >= 1f) {
                fadeAlpha = 1f;

                if (!isGameOver) {
                    isGameOver = true;
                    EndGameModal endModal = new EndGameModal(isWinStatus, this);
                    stage.addActor(endModal);
                }
            }
        } else {
            if (fadeAlpha > 0) {
                fadeAlpha -= delta;
                if (fadeAlpha < 0) fadeAlpha = 0;
            }
        }

        if (world.getCurrentRoom() == 1 && player.position.x > WORLD_WIDTH - Player.WIDTH) {
            world.buildRoom(2, assetManager);
            player.position.set(30, player.position.y);
        }
        else if (world.getCurrentRoom() == 2 && player.position.x < 0) {
            world.buildRoom(1, assetManager);
            player.position.set(WORLD_WIDTH - Player.WIDTH - 30, player.position.y);
        }else if (world.getCurrentRoom() == 2 && player.position.x > WORLD_WIDTH - Player.WIDTH) {
            world.buildRoom(3, assetManager);
            player.position.set(30, player.position.y);
        }
        else if (world.getCurrentRoom() == 3 && player.position.x < 0) {
            world.buildRoom(2, assetManager);
            player.position.set(WORLD_WIDTH - Player.WIDTH - 30, player.position.y);
        }
        else if (world.getCurrentRoom() == 3 && player.position.x > WORLD_WIDTH - Player.WIDTH) {
            world.buildRoom(4, assetManager);
            player.position.set(30, player.position.y);
        }
        else if (world.getCurrentRoom() == 4 && player.position.x < 0) {
            world.buildRoom(3, assetManager);
            player.position.set(WORLD_WIDTH - Player.WIDTH - 30, player.position.y);
        }

        else if (world.getCurrentRoom() == 4 && player.position.x > WORLD_WIDTH - Player.WIDTH) {
            world.buildRoom(5, assetManager);
            player.position.set(100, player.position.y);
        }
        else if (world.getCurrentRoom() == 3 && player.position.y < -20 && player.position.x > 900 && player.position.x < 1100) {
            world.buildRoom(6, assetManager);
            player.position.set(player.position.x, 700);
        }
        else if (world.getCurrentRoom() == 6 && player.position.y > 740 && player.position.x > 900 && player.position.x < 1100) {
            world.buildRoom(3, assetManager);
            player.position.set(player.position.x, 20);
            player.velocity.y = 500;
        }

        float targetX = player.position.x + Player.WIDTH / 2;
        float targetY = player.position.y + Player.HEIGHT / 2;

        float clampedX = com.badlogic.gdx.math.MathUtils.clamp(targetX, WORLD_WIDTH / 2, world.roomWidth - (WORLD_WIDTH / 2));
        float clampedY = com.badlogic.gdx.math.MathUtils.clamp(targetY, WORLD_HEIGHT / 2, 800 - WORLD_HEIGHT / 2);

        camera.position.set(clampedX, clampedY, 0);
        if (world.screenShakeTime > 0) {
            float shakeX = (float) (Math.random() - 0.5) * 10;
            float shakeY = (float) (Math.random() - 0.5) * 10;
            camera.position.x += shakeX;
            camera.position.y += shakeY;
            world.screenShakeTime -= delta;
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height, true);
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (player != null) player.dispose();
        if (assetManager != null) assetManager.disposeManager();
        if (hud != null) hud.dispose();
        if (rayTexture != null) rayTexture.dispose();
    }

    // ذرات معلق
    class AmbientParticle {
        float x, y;
        float speedX, speedY;
        float rotation;
        float rotSpeed;
        float scale;
        float alpha;
        boolean fadingIn;

        public AmbientParticle(float camX, float camY) {
            reset(camX, camY);
            this.alpha = MathUtils.random(0f, 0.4f);
        }

        public void reset(float camX, float camY) {
            x = camX + MathUtils.random(-WORLD_WIDTH, WORLD_WIDTH);
            y = camY + MathUtils.random(-WORLD_HEIGHT, WORLD_HEIGHT);
            speedX = MathUtils.random(-15f, 15f);
            speedY = MathUtils.random(-15f, 15f);
            rotation = MathUtils.random(0, 360);
            rotSpeed = MathUtils.random(-15f, 15f);
            scale = MathUtils.random(0.015f, 0.07f);
            alpha = 0f;
            fadingIn = true;
        }

        public void update(float delta, float camX, float camY) {
            x += speedX * delta;
            y += speedY * delta;
            rotation += rotSpeed * delta;

            if (fadingIn) {
                alpha += 0.2f * delta;
                if (alpha >= 0.4f) fadingIn = false;
            } else {
                alpha -= 0.15f * delta;
                if (alpha <= 0f) {
                    reset(camX, camY);
                }
            }
        }
    }

    public void saveCurrentState() {
        if (loadedSave == null) {
            loadedSave = new GameSave();
        }

        loadedSave.setHealth(player.health);
        loadedSave.setMaxHealth(player.maxHealth);
        loadedSave.setGeo(player.geo);
        loadedSave.setCurrentRoom(world.getCurrentRoom());
        loadedSave.setPlayerX(player.position.x);
        loadedSave.setPlayerY(player.position.y);

        long seconds = (long) AchievementManager.getInstance().getPlayTime();
        loadedSave.setPlayTimeSeconds(seconds);

        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        String timeStr = hours > 0 ?
            String.format("%02d:%02d:%02d", hours, minutes, secs) :
            String.format("%02d:%02d", minutes, secs);
        loadedSave.setTimePlayed(timeStr);

        loadedSave.setCurrentScene("ROOM " + world.getCurrentRoom());

        if (loadedSave.getProgress() <= 0) {
            loadedSave.setProgress(1);
        }

        SaveManager saveManager = new SaveManager();
        saveManager.saveGame(loadedSave.getSlotIndex(), loadedSave);
    }

    private void handleCheatCodes() {
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {

            // Boss Arena Teleport (B): انتقال به اتاق باس
            if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
                world.buildRoom(5, assetManager);
                player.position.set(150, 150);
            }

            // Noclip Mode (N): حالت پرواز و عبور از دیوارها
            if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
                player.isNoclip = !player.isNoclip;
            }

            // Emergency Heal (H): دریافت یک جان اضافه
            if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
                if (player.health == 1 && !player.hasUsedEmergencyHeal) {
                    player.health++;
                    player.hasUsedEmergencyHeal = true;
                }
            }

            // Refill Soul (S): پر شدن کامل مخزن روح به ظرفیت 99
            if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                player.soul = 99;
            }

            // God Mode (G): حالت ضد ضربه
            if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
                player.isGodMode = !player.isGodMode;
            }

            // Insta-Kill (K): کشتن آنی تمام دشمنان صفحه (کد تقلب امتیازی)
            if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
                for (com.HollowKnight.model.enemies.Enemy enemy : world.getEnemies()) {
                    enemy.takeDamage(9999);
                }
            }
        }
    }
}
